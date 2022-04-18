package com.virnect.smic.server.data.error;

import org.springframework.http.HttpStatus;

public class SmicUnknownHttpException extends RuntimeException{
	private final HttpStatus statusCode;
	private final String reasonPhrase;

	public SmicUnknownHttpException(HttpStatus statusCode, String reasonPhrase) {
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}
}
