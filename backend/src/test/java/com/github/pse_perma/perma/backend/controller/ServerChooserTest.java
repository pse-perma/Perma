package com.github.pse_perma.perma.backend.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class ServerChooserTest {

	@BeforeClass
	public static void configure() throws Exception {
		ServerChooser.initialize();
		ServerChooser.addServer(new URL("http://www.google.de/"), "Google");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getKnownServers() {
		assertNotNull(ServerChooser.getKnownServers());
	}

	@Test
	public void getSTService() {
		assertNotNull(ServerChooser.getSTService());
	}

	@Test
	public void addServer() {
		try {
			assertTrue(ServerChooser.addServer(new URL("http://celinsci.de/"), "Celinsci"));
			ServerChooser.removeServer(new URL("http://celinsci.de/"));
		} catch (MalformedURLException mue) {
			fail(mue.getMessage());
		}
	}

	@Test
	public void addInvalidServer() {
		try {
			assertFalse(ServerChooser.addServer(null, ""));
		} catch (MalformedURLException mue) {
			fail(mue.getMessage());
		}
	}

	@Test
	public void removeServer() {
		try {
			assertTrue(ServerChooser.removeServer(new URL("http://www.google.de/")));
		} catch (MalformedURLException mue) {
			fail(mue.getMessage());
		}
	}

	@Test(expected = MalformedURLException.class)
	public void setCurrentURL() throws MalformedURLException {
		ServerChooser.setCurrentURL(new URL("test url"));
	}
}
