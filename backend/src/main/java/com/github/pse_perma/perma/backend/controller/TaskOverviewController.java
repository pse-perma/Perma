package com.github.pse_perma.perma.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pse_perma.perma.backend.exceptions.ServiceUnavailableException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.*;
import de.fraunhofer.iosb.ilt.sta.model.Constraints.*;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.model.parameterTypes.*;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import com.github.pse_perma.perma.backend.exceptions.BadRequestException;
import com.github.pse_perma.perma.backend.exceptions.NotImplementedException;
import net.minidev.json.JSONObject;
import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.github.pse_perma.perma.backend.controller.ServerChooser.getSTService;

/**
 * @author Claudio Merz
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/api"})
public class TaskOverviewController {

	private Logger logger = LoggerFactory.getLogger("d.f.p.backend.TaskController");

	@GetMapping(path = {"/tasks"})
	public List<Task> getTasks(@RequestParam String numberoftasks, @RequestParam String pagenumber,
	                           @RequestParam(required = false) String filter,
	                           @RequestParam(required = false) String sort) {
		Query<Task> taskQuery = new Query<>(getSTService(), Task.class);
		List<Task> tasks;
		try {
			if (filter != null && sort != null) {
				tasks = new ArrayList<>(taskQuery
						.filter(filter)
						.orderBy(sort).expand("taskingCapability/Thing")
						.list());
			}
			else if (filter != null) {
				tasks = new ArrayList<>(taskQuery
						.filter(filter)
						.orderBy("id desc").expand("taskingCapability/Thing")
						.list());
			}
			else {
				tasks = new ArrayList<>(taskQuery
						.orderBy("id desc").expand("taskingCapability/Thing")
						.list());
			}
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error. Loading of tasks failed. Message: " + sfe.getMessage());
			throw new ServiceUnavailableException();
		}

		int taskCount, page;

		try {
			taskCount = Integer.parseInt(numberoftasks);
			page = Integer.parseInt(pagenumber);
		} catch (NumberFormatException nfe) {
			logger.error("Number parameter malformed.");
			throw new BadRequestException();
		}

		Task[] taskArray = tasks.toArray(new Task[0]);
		tasks.clear();

		//TODO Query for all tasks ( -1 ?)

		for (int i = taskCount * (page - 1); page > 0 && i >= 0 && i < taskArray.length && i < (taskCount * page); i++) {
			taskArray[i].setNumber(taskArray[i].getId());
			tasks.add(taskArray[i]);
		}
		return tasks;
	}

	@GetMapping(path = {"/tasks/count"})
	public double getTaskCount(@RequestParam String numPerPage) {
		try {
			double count = Double.parseDouble(numPerPage);
			return Math.ceil((double) new Query<>(getSTService(), Task.class).list().size() / count);
		} catch (ServiceFailureException sfe) {
			logger.error("Query failed. Couldn't count tasks.");
			throw new ServiceUnavailableException();
		} catch (NumberFormatException nfe) {
			logger.error("Number parameter malformed.");
			throw new BadRequestException();
		}
	}

	@GetMapping(path = {"/sensors"})
	public Set<Sensor> getSensors() {
		Query<Sensor> query = new Query<>(getSTService(), Sensor.class);
		try {
			return new HashSet<>(query.expand("Datastreams/Observations").list());
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error. Loading of sensors failed.");
			throw new ServiceUnavailableException();
		}
	}

	@PostMapping(path = {"/tasks"})
	@ResponseStatus(HttpStatus.CREATED)
	public void createTask(@RequestBody String json, @RequestParam(required = false) String dry) {
		// First, find and parse the root JSON content.
		JsonNode rootNode = readJsonFromString(json);

		// Then, detect the actuator that is supposed to execute the task.
		Actuator actuator;
		if (rootNode.has("actuatorID")) {
			try {
				actuator = getSTService().actuators().find(Long.parseLong(rootNode.get("actuatorID").asText()));
				actuator.setCapabilities(getSTService().taskingCapabilities().query().filter("Actuator/id eq '" + actuator.getId() + "'").list());
			} catch (ServiceFailureException sfe) {
				logger.error("The actuator with ID " + rootNode.get("actuatorID").asText() + " couldn't be found on " + "the SensorThings server.");
				throw new ServiceUnavailableException();
			} catch (NumberFormatException nfe) {
				logger.error("The provided ID was not a number: " + rootNode.get("actuatorID").asText());
				throw new BadRequestException();
			}
		}
		else {
			logger.error("No actuator ID provided. Cannot create task if no actuator is provided to execute it.");
			throw new BadRequestException();
		}
		EntityList<TaskingCapability> capabilities = actuator.getCapabilities();

		// Now, find and parse the parameters from the JSON.
		JsonNode parameterNode;
		if (rootNode.has("parameters")) {
			parameterNode = rootNode.path("parameters");
		}
		else {
			logger.error("Task couldn't be created. No parameters were provided.");
			throw new BadRequestException();
		}

		// Next, create the necessary parameters. This is the most complex part.
		Map<TaskingCapability, TaskingParameterType> parameters = new HashMap<>();
		boolean capFound = false;
		for (TaskingCapability capability : capabilities) {
			if (rootNode.get("capability").asText().equals(capability.getName())) {
				capFound = true;

				// Load the constraints, if they exist.
				HashMap constraints = new HashMap<>();
				if (capability.getTaskingParameters().containsKey("constraint")) {
					try {
						constraints = (HashMap) capability.getTaskingParameters().get("constraint");
					} catch (ClassCastException cce) {
						logger.error("Capabilities of actuator malformed. No constraints provided.");
						throw new ServiceUnavailableException();
					}
				}

				String type, name, label, description;
				try {
					type = (String) capability.getTaskingParameters().get("type");
					name = (String) capability.getTaskingParameters().get("name");
					label = (String) capability.getTaskingParameters().get("label");
				} catch (ClassCastException | NullPointerException e) {
					logger.error("No information about the tasking parameter found in the capability. Unable to " +
							"create parameters.");
					throw new ServiceUnavailableException();
				}
				try {
					description = (String) capability.getTaskingParameters().get("description");
				} catch (ClassCastException | NullPointerException e) {
					logger.warn("No description about the tasking parameter found in the capability.");
					description = "";
				}

				// Find the correct parameter type and create the parameter object accordingly.
				switch (type) {
					case "Boolean":
						boolean bool = Boolean.parseBoolean(parameterNode.get("status").asText());
						parameters.put(capability, new BooleanType(label, description, bool));
						break;
					case "Text":
						String text = parameterNode.get(name).asText();

						String textConstraint;
						if (constraints.containsKey("pattern")) {
							textConstraint = (String) constraints.get("pattern");
						}
						else {
							logger.warn("No regular expression for text constraint provided.");
							textConstraint = ".*";
						}

						parameters.put(capability, new TextType(label, description, text,
								new AllowedPatternTokens(textConstraint)));
						break;
					case "Category":
						String category = parameterNode.get(name).asText();

						String[] categoryConstraint = new String[] {};
						if (constraints.containsKey("value")) {
							categoryConstraint = ((ArrayList<String>) constraints.get("value")).toArray(categoryConstraint);
						}
						else {
							logger.error("No value array of allowed strings found in the constraint.");
							throw new ServiceUnavailableException();
						}

						parameters.put(capability, new CategoryType(label, description, category,
								new AllowedTokens(categoryConstraint)));
						break;
					case "Count":
						double count = parameterNode.get(name).asDouble();

						Double[] countConstraint = new Double[] {};
						if (constraints.containsKey("interval")) {
							countConstraint = ((ArrayList<Double>) constraints.get("interval")).toArray(countConstraint);
						}
						else {
							logger.error("No value array of an allowed interval found in the constraint. Using Double" + ".MIN_VALUE and Double.MAX_VALUE instead.");
							countConstraint = new Double[]{Double.MIN_VALUE, Double.MAX_VALUE};
						}

						if (countConstraint.length != 2) {
							logger.error("Too few/many values to create an interval of allowed values.");
							throw new ServiceUnavailableException();
						}

						parameters.put(capability, new CountType(label, description, count,
								new AllowedInterval(countConstraint[0], countConstraint[1])));
						break;
					case "Quantity":
						double quantity = parameterNode.get(name).asDouble();
						double[] quantityConstraint;

						Object[] quantityEntries = constraints.values().toArray();
						Object[] quantityValues;
						if (quantityEntries.length >= 2) {
							quantityValues = ((ArrayList) quantityEntries[1]).toArray();
						}
						else {
							logger.error("No value array of allowed values found in the constraint.");
							throw new ServiceUnavailableException();
						}
						quantityConstraint = new double[quantityValues.length];
						for (int i = 0; i < quantityValues.length; i++) {
							try {
								quantityConstraint[i] = Double.parseDouble(quantityValues[i].toString());
							} catch (NumberFormatException nfe) {
								logger.error("One of the provided values in the constraint was not a number: " + quantityValues[i].toString());
								throw new ServiceUnavailableException();
							}
						}

						if (constraints.containsKey("value")) {
							parameters.put(capability, new QuantityType(label, description, quantity,
									new AllowedValues(quantityConstraint)));
						}
						else if (constraints.containsKey("interval")) {
							parameters.put(capability, new QuantityType(label, description, quantity,
									new AllowedInterval(quantityConstraint[0], quantityConstraint[1])));
						}
						else {
							logger.error("Capabilities of actuator malformed. No quantity constraints provided.");
							throw new ServiceUnavailableException();
						}

						break;
					case "Time":
						LocalDateTime time;
						try {
							time = LocalDateTime.parse(parameterNode.get("time").asText());
						} catch (DateTimeParseException dtpe) {
							logger.error("The provided time string couldn't be parsed: " + parameterNode.get("time").asText());
							throw new BadRequestException();
						}

						parameters.put(capability, new TimeType(label, description, time));
						break;
					case "Location":
						JsonNode locationNode = parameterNode.get(name);

						switch (locationNode.get("type").asText()) {
							case "Point":
								JsonNode coordNode = locationNode.get("coordinates");
								Double[] coordinates = new Double[coordNode.size()];
								for (int i = 0; i < coordinates.length; i++) {
									coordinates[i] = coordNode.get(i).asDouble();
								}
								// For potential restructuring of LocationType to GeoJson standard
								GeoJsonObject target = new Point(coordinates[0], coordinates[1]);

								parameters.put(capability, new LocationType(label, description, coordinates));
								break;
							case "Feature":
								break;
							default:
								break;
						}
						break;
					case "CategoryRange":
						JsonNode rangeNode = parameterNode.get(name);
						String[] range = new String[rangeNode.size()];
						for (int i = 0; i < range.length; i++) {
							range[i] = rangeNode.get(i).asText();
						}

						String[] categoryRangeConstraint;

						Object[] catRangeEntries = constraints.values().toArray();
						Object[] catRangeValues;
						if (catRangeEntries.length >= 2) {
							catRangeValues = ((ArrayList) catRangeEntries[1]).toArray();
						}
						else {
							logger.error("No value array of an allowed value sequence found in the constraint.");
							throw new ServiceUnavailableException();
						}

						categoryRangeConstraint = new String[catRangeValues.length];
						for (int i = 0; i < catRangeValues.length; i++) {
							categoryRangeConstraint[i] = catRangeValues[i].toString();
						}

						if (rangeNode.size() != 2) {
							logger.error("Too few/many values to create an interval of allowed values.");
							throw new BadRequestException();
						}

						parameters.put(capability, new CategoryRangeType(label, description, range,
								new AllowedTokenRange(categoryRangeConstraint[0],
										categoryRangeConstraint[categoryRangeConstraint.length - 1])));
						break;
					case "CountRange":
						JsonNode countNode = parameterNode.get(name);
						double[] countRange = new double[countNode.size()];
						for (int i = 0; i < countRange.length; i++) {
							countRange[i] = countNode.get(i).asDouble();
						}

						Object[] countRangeValues;
						if (constraints.containsKey("interval")) {
							countRangeValues = ((ArrayList) constraints.get("interval")).toArray();
						}
						else {
							logger.error("No value array of an allowed interval found in the constraint.");
							throw new ServiceUnavailableException();
						}

						double[] countRangeConstraint = new double[countRangeValues.length];
						for (int i = 0; i < countRangeValues.length; i++) {
							try {
								countRangeConstraint[i] = Double.parseDouble(countRangeValues[i].toString());
							} catch (NumberFormatException nfe) {
								logger.error("One of the provided values in the constraint was not a number: " + countRangeValues[i].toString());
								throw new ServiceUnavailableException();
							}
						}

						if (countNode.size() != 2) {
							logger.error("Too few/many values to create an interval of allowed values.");
							throw new BadRequestException();
						}

						parameters.put(capability, new CountRangeType(label, description, countRange, new AllowedIntervalRange(countRangeConstraint[0], countRangeConstraint[1])));
						break;
					case "QuantityRange":
						JsonNode quantityNode = parameterNode.get(name);
						double[] quantityRange = new double[quantityNode.size()];
						for (int i = 0; i < quantityRange.length; i++) {
							quantityRange[i] = quantityNode.get(i).asDouble();
						}

						Object[] quantityRangeValues;
						if (constraints.containsKey("interval")) {
							quantityRangeValues = ((ArrayList) constraints.get("interval")).toArray();
						}
						else {
							logger.error("No value array of an allowed interval found in the constraint.");
							throw new ServiceUnavailableException();
						}

						Double[] quantityRangeConstraint = new Double[quantityRangeValues.length];
						for (int i = 0; i < quantityRangeValues.length; i++) {
							try {
								quantityRangeConstraint[i] = Double.parseDouble(quantityRangeValues[i].toString());
							} catch (NumberFormatException nfe) {
								logger.error("One of the provided values in the constraint was not a number: " + quantityRangeValues[i].toString());
								throw new ServiceUnavailableException();
							}
						}

						if (quantityNode.size() != 2) {
							logger.error("Too few/many values to create an interval of allowed values.");
							throw new BadRequestException();
						}

						parameters.put(capability, new QuantityRangeType(label, description, quantityRange,
								new AllowedIntervalRange(quantityRangeConstraint[0], quantityRangeConstraint[1])));
						break;
					case "TimeRange":
						JsonNode timeRange = parameterNode.get(name);
						LocalDateTime[] times = new LocalDateTime[timeRange.size()];
						for (int i = 0; i < times.length; i++) {
							try {
								times[i] = LocalDateTime.parse(timeRange.get(i).asText());
							} catch (DateTimeParseException dte) {
								logger.error("The provided time string couldn't be parsed: " + timeRange.get(i).asText());
								throw new BadRequestException();
							}
						}

						parameters.put(capability, new TimeRangeType(label, description, times,
								new AllowedTimeRange(LocalDateTime.MIN, LocalDateTime.MAX)));
						break;
					case "DataRecord":
						logger.error("DataRecord task type not implemented yet.");
						throw new NotImplementedException();
					default:
						logger.error("The type of the task was not specified or is unknown.");
						throw new BadRequestException();
				}
			}
		}

		if (!capFound) {
			logger.error("None of the capabilities that the actuator implements was found in the parameters.");
			throw new BadRequestException();
		}

		for (TaskingParameterType parameter : parameters.values()) {
			if (!parameter.checkInput(parameter.getValue())) {
				logger.error("One of the provided values didn't match the given constraint. Value was: " + parameter.getValue());
				throw new BadRequestException();
			}
		}

		Set<Task> tasks = new HashSet<>();
		capabilities.forEach(capability -> {
			Map<String, Object> taskParams = new HashMap<>();
			if (parameters.containsKey(capability)) {
				taskParams.put((String) capability.getTaskingParameters().get("name"),
						parameters.get(capability).getValue());
				tasks.add(new Task(capability, taskParams));
			}
		});

		if (!Boolean.valueOf(dry)) {
			tasks.forEach(task -> {
				try {
					task.setService(getSTService());
					getSTService().create(task);
					logger.info("A task has been created.");
				} catch (ServiceFailureException e) {
					logger.error("SensorThings server error: " + e.getMessage());
					throw new ServiceUnavailableException();
				}
			});
		}
		else {
			logger.info("Successfully checked parameters on dry run.");
			logger.debug("All parameters that have been passed are valid. Tasks can be created when the request is " + "repeated without the 'dry' flag.");
		}
	}

	@PostMapping(path = {"/tasks/direct"})
	public void sendTaskJson(@RequestBody String json) {
	}

	@PostMapping(path = {"/tasks/import"})
	public void importTask(@RequestBody JSONObject json) {

	}

	@PostMapping(path = {"/tasks/virtual"})
	public void createVirtualActuator(@RequestParam(required = false) String id, @RequestBody String json) {
		Map<String, Object> parameters = new HashMap<>();
		JsonNode taskNode = readJsonFromString(json);

		Actuator vaServer;
		try {
			vaServer = getSTService().actuators().find(Long.parseLong(id));
			vaServer.setCapabilities(getSTService().taskingCapabilities().query().filter("Actuator/id eq '" + id + "'").list());
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error: " + sfe.getMessage());
			throw new ServiceUnavailableException();
		} catch (NullPointerException npe) {
			logger.error("No ID provided.");
			throw new BadRequestException();
		}

		TaskingCapability taskCap = new TaskingCapability();

		boolean hasCap = false;
		for (TaskingCapability capability : vaServer.getCapabilities()) {
			if (capability.getName().equals("createNewVA")) {
				taskCap = capability;
				hasCap = true;
			}
		}

		if (!hasCap) {
			throw new ServiceUnavailableException();
		}

		String vaName = taskNode.has("vaName") ? taskNode.get("vaName").asText() : "";
		String vaDescription = taskNode.has("vaDescription") ? taskNode.get("vaDescription").asText() : "";
		String thingName = taskNode.has("thingName") ? taskNode.get("thingName").asText() : "";
		String thingDescription = taskNode.has("thingDescription") ? taskNode.get("thingDescription").asText() : "";
		String capabilityList = taskNode.has("capabilityList") ? taskNode.get("capabilityList").asText() : "";

		parameters.put("vaName", vaName);
		parameters.put("vaDescription", vaDescription);
		parameters.put("thingName", thingName);
		parameters.put("thingDescription", thingDescription);
		parameters.put("capabilityList", capabilityList);

		Task task = new Task(taskCap, parameters);
		try {
			getSTService().tasks().create(task);
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error: " + sfe.getMessage());
			throw new ServiceUnavailableException();
		}
	}

	@DeleteMapping(path = {"/tasks"})
	public void deleteTask(@RequestParam String id) {
		try {
			//TODO Delete creates Http 301 ("Moved permanently") to https in server
			getSTService().delete(getSTService().tasks().find(new IdLong(Long.parseLong(id))));
			logger.info("The task with ID " + id + "was deleted from the SensorThings server.");
		} catch (ServiceFailureException sfe) {
			logger.error("Http DELETE rejected or task search failed. Deleting task failed.");
			throw new ServiceUnavailableException();
		} catch (NumberFormatException nfe) {
			logger.error("ID malformed. Not a number: " + id);
			throw new BadRequestException();
		} catch (NullPointerException npe) {
			logger.error("No ID was provided.");
			throw new BadRequestException();
		}
	}

	private JsonNode readJsonFromString(String json) {
		try {
			return new ObjectMapper().readTree(json);
		} catch (IOException ioe) {
			logger.error("The provided JSON content was malformed.");
			throw new BadRequestException();
		}
	}
}
