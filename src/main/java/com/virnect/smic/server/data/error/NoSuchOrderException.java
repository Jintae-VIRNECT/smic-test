package com.virnect.smic.server.data.error;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoSuchOrderException extends RuntimeException {
	public NoSuchOrderException(String s) {
		super(s);
	}
}
