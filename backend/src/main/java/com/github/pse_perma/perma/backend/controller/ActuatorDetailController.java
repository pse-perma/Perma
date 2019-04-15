package com.github.pse_perma.perma.backend.controller;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.*;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import com.github.pse_perma.perma.backend.exceptions.BadRequestException;
import com.github.pse_perma.perma.backend.exceptions.ServiceUnavailableException;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

import static com.github.pse_perma.perma.backend.controller.ServerChooser.getSTService;

/**
 * @author Claudio Merz
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/api/actuators/details"})
public class ActuatorDetailController {

	private Logger logger = LoggerFactory.getLogger("d.f.p.backend.ActuatorDetailController");

	@GetMapping
	public Actuator getActuator(@RequestParam String id) {
		try {
			Actuator actuator = getSTService().actuators().find(Long.parseLong(id));
			actuator.setNumber(new IdLong(Long.parseLong(id)));
			actuator.setCapabilities(getSTService().taskingCapabilities().query().filter("Actuator/id eq '" + actuator.getId() + "'").list());
			logger.info("The actuator with ID " + id + " was found on the SensorThings server.");
			return actuator;
		} catch (ServiceFailureException sfe) {
			logger.error("Couldn't find an actuator with ID " + id + ".");
			throw new ServiceUnavailableException();
		} catch (NullPointerException npe) {
			logger.error("No ID was provided.");
			throw new BadRequestException();
		} catch (NumberFormatException nfe) {
			logger.error("ID malformed. Not a number.");
			throw new BadRequestException();
		}
	}

	@GetMapping(path = {"/sensor"})
	public Sensor getActuatorSensor(@RequestParam String id) {
		try {
			Object metadata = getSTService().actuators().find(Long.parseLong(id)).getMetadata();
			String sensorIdString;
			try {
				sensorIdString = metadata.toString().split("=")[1].split("}")[0];
			} catch (ArrayIndexOutOfBoundsException aie) {
				logger.warn("No sensor ID found in the metadata of the actuator.");
				throw new ServiceUnavailableException();
			}
			int sensorID = Integer.parseInt(sensorIdString);

			return new Query<>(getSTService(), Sensor.class).filter("id eq'" + sensorID + "'").expand(
					"Datastreams/Observations").first();
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error while trying to find Observations of a sensor belonging to an " +
					"actuator. Error was: " + sfe.getMessage());
			throw new ServiceUnavailableException();
		}
	}

	@GetMapping(path = {"/locations"})
	public Set<Location> getActuatorLocations(@RequestParam String id) {
		try {
			Actuator actuator = getSTService().actuators().find(Long.parseLong(id));
			actuator.setNumber(new IdLong(Long.parseLong(id)));
			logger.info("The actuator with ID " + id + " was found on the SensorThings server.");
			try {
				Set<TaskingCapability> actuatorCapabilities =
						new HashSet<>(getSTService().taskingCapabilities().query().filter("Actuator/id eq '" + id +
								"'").list());
				Set<Location> actuatorLocations = new HashSet<>();
				for (TaskingCapability capability : actuatorCapabilities) {
					if (capability.getThing() != null) {
						if (capability.getThing().getLocations() != null) {
							// Are the thing and the locations already included in the object???
							actuatorLocations.addAll(capability.getThing().getLocations());
						}
					}
				}
				logger.info("The locations related to the actuator with ID " + id + " were found on the SensorThings " +
						"server.");
				return actuatorLocations;
			} catch (ServiceFailureException sfe) {
				logger.error("Couldn't find things that are related to the actuator with ID " + id);
				throw new ServiceUnavailableException();
			}
		} catch (ServiceFailureException sfe) {
			logger.error("Couldn't find an actuator with ID " + id + ".");
			throw new ServiceUnavailableException();
		} catch (NullPointerException npe) {
			logger.error("Probably no ID was provided.");
			throw new BadRequestException();
		} catch (NumberFormatException nfe) {
			logger.error("ID malformed. Not a number.");
			throw new BadRequestException();
		}
	}

	@PutMapping
	public void updateActuator(@RequestBody JSONObject json) {
		String id = json.getAsString("id");
		Actuator actuator = getActuator(id);
		if (actuator == null) return;

		updateActuatorProperties(actuator, json);

		try {
			getSTService().update(actuator);
			logger.info("The actuator with ID " + id + " was updated on the server.");
		} catch (ServiceFailureException sfe) {
			logger.error("Http PUT rejected. Updating actuator failed.");
		}
	}

	//TODO Actuator properties in FROST-Client
	private void updateActuatorProperties(Actuator actuator, JSONObject json) {
		if (json.getAsString("name") != null && !json.getAsString("name").isEmpty()) {
			actuator.setName(json.getAsString("name"));
		}

		if (json.getAsString("description") != null && !json.getAsString("description").isEmpty()) {
			actuator.setDescription(json.getAsString("description"));
		}
	}
}
