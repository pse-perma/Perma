package com.github.pse_perma.perma.backend.controller;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Claudio Merz
 */
public class ServerChooser {

	private static SensorThingsService service;
	private static Set<STServer> knownServers;
	private static Logger logger;

	private static String serverFile = "./config/servers.json";

	static {
		knownServers = new HashSet<>();
		logger = LoggerFactory.getLogger("d.f.p.backend.ServerChooser");
	}

	/**
	 * Initializes the software by reading a JSON file. This file contains all servers that have been added in former
	 * executions of the software.
	 * <br>
	 * If the file is malformed or not existent, no servers will be added to the set that is created on startup.
	 * Otherwise, the user of the frontend will have access to all servers stored in the file that have a correctly
	 * formatted URL.
	 */
	public static void initialize() {
		File servers = new File(serverFile);
		try {
			if (servers.createNewFile()) {
				return;
			}
		} catch (IOException ioe) {
			logger.error("JSON server file couldn't be created.");
			return;
		} catch (SecurityException se) {
			logger.error("No permission to create the JSON server file.");
			return;
		}

		JsonNode json;
		try {
			json = new ObjectMapper().readTree(servers);
		} catch (IOException ioe) {
			logger.error("The JSON file that contains the servers is invalid and couldn't be read.");
			return;
		}

		for (JsonNode server : json) {
			try {
				addServer(server.has("url") ? new URL(server.get("url").asText()) : null, server.has("name") ?
						server.get("name").asText() : "");
			} catch (MalformedURLException mue) {
				logger.warn("The URL provided in the config file was malformed: " + server.get("url").asText());
			}
		}
	}

	/**
	 * Returns the map of known SensorThings servers.
	 *
	 * @return the map of known servers
	 */
	static Set<STServer> getKnownServers() {
		return knownServers;
	}

	/**
	 * Sets the currently used URL and adds it to the map of known SensorThings servers.
	 *
	 * @param url The URL to set
	 *
	 * @throws MalformedURLException if the URL is in the wrong format
	 */
	static void setCurrentURL(URL url) throws MalformedURLException {
		addServer(url, "");
		logger.info("New URL has been set. Value is now " + url.toString());
	}

	/**
	 * Sets the service and adds it to the map of known SensorThings servers.
	 *
	 * @param url The URL of the server
	 *
	 * @return if the server was added
	 * @throws MalformedURLException if the URL is in the wrong format
	 */
	static boolean addServer(URL url, String name) throws MalformedURLException {
		if (url != null) {
			service = new SensorThingsService(url);
			if (knownServers.add(new STServer(service.getEndpoint().toString(), name))) {
				logger.info("Added new SensorThings server with name \"" + (name != null ? name : "") + "\" and URL " + url.toString());
				updateServerFile();
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a server from the map of known SensorThings servers and disconnects, if the current server is to be
	 * removed.
	 *
	 * @param url The URL of the server
	 *
	 * @return if the server was removed
	 */
	static boolean removeServer(URL url) {
		if (url != null) {
			if (knownServers.remove(new STServer(url.toString()))) {
				logger.info("The server with URL " + url.toString() + " has been removed.");
				if (getSTService().getEndpoint().equals(url)) {
					service = null;
					logger.debug("SensorThings service object set to null. Errors may occur until a new server is " +
							"chosen.");
				}
				updateServerFile();
				return true;
			}
		}
		return false;
	}

	static SensorThingsService getSTService() {
		return service;
	}

	private static void updateServerFile() {
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
		try {
			writer.writeValue(new File(serverFile), knownServers);
		} catch (IOException ioe) {
			logger.error("The set of known SensorThings servers could not be written to the config file.");
		}
	}
}
