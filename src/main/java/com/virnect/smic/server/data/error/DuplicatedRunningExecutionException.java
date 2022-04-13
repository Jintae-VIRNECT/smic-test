package com.virnect.smic.server.data.error;

import lombok.Getter;

@Getter
public class DuplicatedRunningExecutionException extends RuntimeException{
	private long executionId;
	public DuplicatedRunningExecutionException(long executionId) {
		this.executionId = executionId;
	}
}
