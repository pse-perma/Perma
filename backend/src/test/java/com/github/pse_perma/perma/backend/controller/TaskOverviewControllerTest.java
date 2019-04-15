package com.github.pse_perma.perma.backend.controller;

import net.minidev.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class TaskOverviewControllerTest {

	@Rule
	public DockerComposeContainer perma =
			new DockerComposeContainer(new File("docker-compose.yml")).withExposedService("frost-server", 8080,
					Wait.forHttp("/FROST-Server/v1.0/Actuators").forStatusCode(200));
	private TaskOverviewController controller = new TaskOverviewController();

	@Before
	public void setUp() throws Exception {
		String address = perma.getServiceHost("frost-server", 8080);
		int port = perma.getServicePort("frost-server", 8080);

		try {
			ServerChooser.setCurrentURL(new URL("http://" + address + ":" + port + "/FROST-Server/v1.0/"));
		} catch (MalformedURLException mue) {
			fail(mue.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception {
		String address = perma.getServiceHost("frost-server", 8080);
		int port = perma.getServicePort("frost-server", 8080);

		try {
			ServerChooser.removeServer(new URL("http://" + address + ":" + port + "/FROST-Server/v1.0/"));
		} catch (MalformedURLException mue) {
			fail(mue.getMessage());
		}
	}

	@Ignore
	public void getTasks() {
		assertNotNull(controller.getTasks("100", "1", null, null));
	}

	@Ignore
	public void getTaskCount() {
		assertEquals(0L, (long) controller.getTaskCount("100"));
	}

	@Ignore
	public void getSensors() {
		assertNotNull(controller.getSensors());
	}

	@Ignore //already ignored
	public void createTask() {
		JSONObject json = new JSONObject();
		json.put("actuatorID", "1");
		//		assert(controller.createTask());
	}

	@Ignore // already ignored
	public void createVirtualActuator() {
	}

	@Ignore//(expected = BadRequestException.class)
	public void deleteTaskNaN() {
		controller.deleteTask("Not a number");
	}

	@Ignore//(expected = BadRequestException.class)
	public void deleteTaskNullArg() {
		controller.deleteTask(null);
	}

	@Ignore//(expected = ServiceUnavailableException.class)
	public void deleteTaskInvalidID() {
		controller.deleteTask("-1");
	}
}
