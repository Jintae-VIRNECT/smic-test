package com.virnect.smic.server.data.error.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchExecutionException extends RuntimeException {
	public NoSuchExecutionException(String s) {
		super(s);
	}
}
