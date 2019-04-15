package com.github.pse_perma.perma.backend.controller;

import de.fraunhofer.iosb.ilt.sta.model.Actuator;
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

public class ActuatorDetailControllerTest {

	@Rule
	public DockerComposeContainer perma =
			new DockerComposeContainer(new File("docker-compose.yml")).withExposedService("frost-server", 8080,
					Wait.forHttp("/FROST-Server/v1.0/Actuators").forStatusCode(200));
	private ActuatorDetailController controller = new ActuatorDetailController();

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

	@Ignore(value = "Weird Java \"No such method\" error")
	public void getActuator() {
		JSONObject json = new JSONObject();
		json.put("name", "Test Creation Actuator");
		json.put("description", "A description");
		json.put("encodingType", "application/virtual.actuator");
		json.put("metadata", "none");
		ActuatorOverviewController OvController = new ActuatorOverviewController();
		OvController.createActuator(json);
		Actuator expected = OvController.getActuators("-1", "1", null, null).get(0);

		assertEquals(expected, controller.getActuator(expected.getId().toString()));
	}

	@Ignore
	public void getActuatorLocations() {
		assertNotNull(controller.getActuatorLocations("1"));
	}

	@Ignore//(expected = BadRequestException.class)
	public void getActLocationNaN() {
		controller.getActuatorLocations("kappa");
	}

	@Ignore//(expected = BadRequestException.class)
	public void getActLocationsNullArg() {
		controller.getActuatorLocations(null);
	}

	@Ignore // already ignored
	public void updateActuator() {
	}
}
