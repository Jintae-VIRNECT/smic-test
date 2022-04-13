package com.virnect.smic.server.data.error;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class KioskLoginFailException extends RuntimeException{
	public KioskLoginFailException(String s) {
		super(s);
	}
}
