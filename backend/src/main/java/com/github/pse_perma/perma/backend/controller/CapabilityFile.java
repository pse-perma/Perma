package com.github.pse_perma.perma.backend.controller;

import java.util.Objects;

public class CapabilityFile {
	private String name;
	private String url;

	CapabilityFile(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CapabilityFile that = (CapabilityFile) o;

		return Objects.equals(url, that.url);
	}

	@Override
	public int hashCode() {
		return url != null ? url.hashCode() : 0;
	}
}
