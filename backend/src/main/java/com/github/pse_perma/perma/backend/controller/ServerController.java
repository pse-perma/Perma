package com.github.pse_perma.perma.backend.controller;

import com.github.pse_perma.perma.backend.exceptions.BadRequestException;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

/**
 * @author Claudio Merz
 */
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping({"/api/servers"})
public class ServerController {

	private Logger logger = LoggerFactory.getLogger("d.f.p.backend.ServerController");

	/**
	 * GETs the collection of known servers in a format that can be used by the angular website.
	 *
	 * @return Collection of known servers
	 */
	@GetMapping
	public Set<STServer> getAllServers() {
		return ServerChooser.getKnownServers();
	}

	/**
	 * Adds a server to the map of known SensorThings servers.
	 *
	 * @param json Contains information about the server
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public boolean addServer(@RequestBody JSONObject json) {
		try {
			URL url = new URL(json.getAsString("url"));
			String name = json.getAsString("name");
			return ServerChooser.addServer(url, name);
		} catch (MalformedURLException mue) {
			logger.error("The given URL is malformed.");
			throw new BadRequestException();
		}
	}

	/**
	 * Establishes a connection to the given web address and adds it to the map of known servers.
	 *
	 * @param url The URL to connect to
	 */
	@PostMapping(path = {"/connect"})
	public void connectToServer(@RequestBody String url) {
		try {
			ServerChooser.setCurrentURL(new URL(url));
		} catch (MalformedURLException mue) {
			logger.error("The given URL is malformed.");
			throw new BadRequestException();
		}
	}

	/**
	 * Removes a server from the map of known SensorThings servers and disconnects, if the current server is to be
	 * removed.
	 *
	 * @param json Contains the URL of the server
	 */
	@DeleteMapping
	public boolean removeServer(@RequestBody JSONObject json) {
		try {
			URL url = new URL(json.getAsString("url"));
			return ServerChooser.removeServer(url);
		} catch (MalformedURLException mue) {
			logger.error("The given URL is malformed.");
			throw new BadRequestException();
		}
	}
}
