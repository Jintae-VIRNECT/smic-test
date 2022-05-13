package com.virnect.smic.server.data.error;

import lombok.Getter;

import com.virnect.smic.server.data.dto.response.ExecutionResource;

@Getter
public class DuplicatedRunningExecutionException extends RuntimeException{

	private ExecutionResource executionResource;
	public DuplicatedRunningExecutionException(ExecutionResource execution) {
		this.executionResource = execution;
	}
}
