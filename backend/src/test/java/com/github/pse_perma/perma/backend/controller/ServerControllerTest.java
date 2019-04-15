package com.github.pse_perma.perma.backend.controller;

import net.minidev.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ServerControllerTest {

	private ServerController controller = new ServerController();
	private Map<String, String> json;

	@Before
	public void setUp() throws Exception {
		json = new HashMap<>();
	}

	@After
	public void tearDown() throws Exception {
		json = new HashMap<>();
	}

	@Ignore
	public void addServer() {
		json.put("url", "http://www.kit.edu");
		json.put("name", "KIT Webseite");
		assertTrue(controller.addServer(new JSONObject(json)));
	}

	@Ignore
	public void removeServer() {
		json.put("url", "http://www.google.de");
		json.put("name", "Google");
		controller.addServer(new JSONObject(json));

		Map<String, String> other = new HashMap<>();
		other.put("url", "http://www.google.de");
		assertTrue(controller.removeServer(new JSONObject(other)));
	}
}
