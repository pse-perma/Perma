package com.github.pse_perma.perma.backend.controller;

import com.github.pse_perma.perma.backend.exceptions.BadRequestException;
import com.github.pse_perma.perma.backend.exceptions.ServiceUnavailableException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Actuator;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.pse_perma.perma.backend.controller.ServerChooser.getSTService;

/**
 * @author Claudio Merz
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/api"})
public class ActuatorOverviewController {

	private Logger logger = LoggerFactory.getLogger("d.f.p.backend.ActuatorController");

	@GetMapping(path = {"/actuators"})
	public List<Actuator> getActuators(@RequestParam String numberofactuators, @RequestParam String pagenumber,
	                                   @RequestParam(required = false) String filter,
	                                   @RequestParam(required = false) String sort) {
		Query<Actuator> actuatorQuery = new Query<>(getSTService(), Actuator.class);
		List<Actuator> actuators;
		try {
			if (filter != null && sort != null) {
				actuators = new ArrayList<>(actuatorQuery.filter(filter).orderBy(sort).list());
			}
			else if (filter != null) {
				actuators = new ArrayList<>(actuatorQuery.filter(filter).orderBy("id desc").list());
			}
			else {
				actuators = new ArrayList<>(actuatorQuery.orderBy("id desc")
						//						.expand("TaskingCapabilities")
						.list());
			}
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error. Loading of actuators failed.");
			throw new ServiceUnavailableException();
		}

		actuators.forEach(actuator -> {
			try {
				actuator.setCapabilities(getSTService().taskingCapabilities().query().filter("Actuator/id eq '" + actuator.getId() + "'").list());
			} catch (ServiceFailureException sfe) {
				logger.error("SensorThings server error. Matching capabilities to actuators failed.");
			}
		});

		int actuatorCount, page;

		try {
			actuatorCount = Integer.parseInt(numberofactuators);
			page = Integer.parseInt(pagenumber);
		} catch (NumberFormatException nfe) {
			logger.error("Number parameter malformed.");
			throw new BadRequestException();
		}

		actuators.forEach(actuator -> actuator.setNumber(actuator.getId()));

		if (actuatorCount < 0) {
			return actuators;
		}

		Actuator[] actuatorArray = actuators.toArray(new Actuator[0]);
		actuators.clear();

		for (int i = actuatorCount * (page - 1); page > 0 && i < actuatorArray.length && i < (actuatorCount * page); i++) {
			actuators.add(actuatorArray[i]);
		}
		return actuators;
	}

	@GetMapping(path = {"/things"})
	public Set<Thing> getThings() {
		Query<Thing> query = new Query<>(getSTService(), Thing.class);
		try {
			return new HashSet<>(query.list());
		} catch (ServiceFailureException sfe) {
			logger.error("SensorThings server error. Loading of things failed.");
			throw new ServiceUnavailableException();
		}
	}

	@GetMapping(path = {"/actuators/count"})
	public double getActuatorCount(@RequestParam String numPerPage) {
		try {
			double count = Double.parseDouble(numPerPage);
			return Math.ceil((double) new Query<>(getSTService(), Actuator.class).list().size() / count);
		} catch (ServiceFailureException sfe) {
			logger.error("Query failed. Couldn't count actuators.");
			throw new ServiceUnavailableException();
		} catch (NumberFormatException nfe) {
			logger.error("Number parameter malformed.");
			throw new BadRequestException();
		}
	}

	@PostMapping(path = {"/actuators"})
	@ResponseStatus(HttpStatus.CREATED)
	public boolean createActuator(@RequestBody JSONObject json) {
		String name = json.getAsString("name");
		String description = json.getAsString("description");
		String encodingType = json.getAsString("encodingType");
		Object metadata = json.get("metadata");
		Actuator actuator = new Actuator(name, description, encodingType, metadata);
		try {
			getSTService().create(actuator);
			logger.info("New actuator created with name \"" + name + "\".");
			return true;
		} catch (ServiceFailureException sfe) {
			logger.error("Http POST rejected. Creating actuator failed.");
			throw new ServiceUnavailableException();
		}
	}

	@DeleteMapping(path = {"/actuators"})
	public boolean deleteActuator(@RequestParam String id) {
		try {
			getSTService().delete(getSTService().actuators().find(new IdLong(Long.parseLong(id))));
			logger.info("The actuator with ID " + id + " was deleted from the SensorThings server.");
			return true;
		} catch (ServiceFailureException sfe) {
			logger.error("Http DELETE rejected or actuator search failed. Deleting actuator failed.");
			throw new ServiceUnavailableException();
		} catch (NumberFormatException nfe) {
			logger.error("ID malformed. Not a number: " + id);
			throw new BadRequestException();
		} catch (NullPointerException npe) {
			logger.error("No ID was provided.");
			throw new BadRequestException();
		}
	}
}
