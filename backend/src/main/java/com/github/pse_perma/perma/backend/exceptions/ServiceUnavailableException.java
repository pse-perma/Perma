package com.github.pse_perma.perma.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class ServiceUnavailableException extends RuntimeException {

	public ServiceUnavailableException() {
	}

	public ServiceUnavailableException(String s) {
		super(s);
	}

	public ServiceUnavailableException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public ServiceUnavailableException(Throwable throwable) {
		super(throwable);
	}

	public ServiceUnavailableException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
