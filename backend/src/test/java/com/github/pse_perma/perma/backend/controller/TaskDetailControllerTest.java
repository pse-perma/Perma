package com.github.pse_perma.perma.backend.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.fail;

public class TaskDetailControllerTest {

	@Rule
	public DockerComposeContainer perma =
			new DockerComposeContainer(new File("docker-compose.yml")).withExposedService("frost-server", 8080,
					Wait.forHttp("/FROST-Server/v1.0/Actuators").forStatusCode(200));
	private TaskDetailController controller = new TaskDetailController();

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

	@Ignore//(expected = ServiceUnavailableException.class)
	public void getTaskUnavailable() {
		controller.getTask("42");
	}

	@Ignore//(expected = BadRequestException.class)
	public void getTaskNaN() {
		controller.getTask("Not a number");
	}

	@Ignore//(expected = BadRequestException.class)
	public void getTaskNullArg() {
		controller.getTask(null);
	}

	@Ignore
	public void updateTask() {
	}
}
