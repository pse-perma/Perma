package com.github.pse_perma.perma.backend.controller;

import java.util.Objects;

class STServer {
	private String url;
	private String name;

	STServer(String url) {
		this.url = url;
		this.name = "";
	}

	STServer(String url, String name) {
		this.url = url;
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		STServer server = (STServer) o;

		return Objects.equals(url, server.url);
	}

	@Override
	public int hashCode() {
		return url != null ? url.hashCode() : 0;
	}
}
