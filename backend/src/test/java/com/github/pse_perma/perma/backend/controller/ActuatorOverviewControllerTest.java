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

public class ActuatorOverviewControllerTest {

	@Rule
	public DockerComposeContainer perma =
			new DockerComposeContainer(new File("docker-compose.yml")).withExposedService("frost-server", 8080,
					Wait.forHttp("/FROST-Server/v1.0/Actuators").forStatusCode(200));
	private ActuatorOverviewController controller = new ActuatorOverviewController();

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
	public void getActuators() {
		assertNotNull(controller.getActuators("100", "1", null, null));
	}

	@Ignore//(expected = BadRequestException.class)
	public void getActuatorsNaN() {
		controller.getActuators("five", "1", null, null);
	}

	@Ignore
	public void getAllActuators() {
		assertNotNull(controller.getActuators("-1", "1", null, null));
	}

	@Ignore
	public void getThings() {
		assertNotNull(controller.getThings());
	}

	@Ignore
	public void getActuatorCount() {
		assertEquals(1L, (long) controller.getActuatorCount("20"));
	}

	@Ignore//(expected = BadRequestException.class)
	public void getActuatorCountNaN() {
		controller.getActuatorCount("drrreei");
	}

	@Ignore
	public void createActuator() {
		JSONObject json = new JSONObject();
		json.put("name", "Test Creation Actuator");
		json.put("description", "A description");
		json.put("encodingType", "application/virtual.actuator");
		json.put("metadata", "none");
		assertTrue(controller.createActuator(json));
	}

	@Ignore
	public void deleteActuator() {
		JSONObject json = new JSONObject();
		json.put("name", "Test Actuator");
		json.put("description", "A description");
		json.put("encodingType", "application/virtual.actuator");
		json.put("metadata", "none");
		controller.createActuator(json);

		assertTrue(controller.deleteActuator("2"));
	}

	@Ignore//(expected = BadRequestException.class)
	public void deleteActuatorNaN() {
		controller.deleteActuator("droelf");
	}

	@Ignore//(expected = BadRequestException.class)
	public void deleteActuatorNullArg() {
		controller.deleteActuator(null);
	}
}
