package com.github.pse_perma.perma.backend.controller;

import com.github.pse_perma.perma.backend.exceptions.BadRequestException;
import com.github.pse_perma.perma.backend.exceptions.ServiceUnavailableException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.Task;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static com.github.pse_perma.perma.backend.controller.ServerChooser.getSTService;

/**
 * @author Claudio Merz
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/api/tasks/details"})
public class TaskDetailController {

	private Logger logger = LoggerFactory.getLogger("d.f.p.backend.TaskDetailController");

	@GetMapping
	public Task getTask(@RequestParam String id) {
		try {
			Task task = getSTService().tasks().find(Long.parseLong(id));
			task.setNumber(new IdLong(Long.parseLong(id)));
			logger.info("The task with ID " + id + " was found on the SensorThings server.");
			return task;
		} catch (ServiceFailureException sfe) {
			logger.error("Couldn't find a task with ID " + id + ".");
			throw new ServiceUnavailableException();
		} catch (NullPointerException npe) {
			logger.error("No ID was provided.");
			throw new BadRequestException();
		} catch (NumberFormatException nfe) {
			logger.error("ID malformed. Not a number.");
			throw new BadRequestException();
		}
	}

	@PutMapping
	public void updateTask(@RequestBody JSONObject json) {
		String id = json.getAsString("id");
		Task task = getTask(id);
		if (task == null) return;

		updateTaskProperties(task, json);

		try {
			getSTService().update(task);
			logger.info("The task with ID " + id + " was updated on the server.");
		} catch (ServiceFailureException sfe) {
			logger.error("Http PUT rejected. Updating task failed.");
			throw new ServiceUnavailableException();
		}
	}

	//TODO Task properties in FROST-Client
	private void updateTaskProperties(Task task, JSONObject json) {
		if (json.getAsString("name") != null && !json.getAsString("name").isEmpty()) {
			//			task.setName(json.getAsString("name"));
		}
	}
}
