package com.github.pse_perma.perma.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	public BadRequestException() {
	}

	public BadRequestException(String s) {
		super(s);
	}

	public BadRequestException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public BadRequestException(Throwable throwable) {
		super(throwable);
	}

	public BadRequestException(String s, Throwable throwable, boolean b, boolean b1) {
		super(s, throwable, b, b1);
	}
}
