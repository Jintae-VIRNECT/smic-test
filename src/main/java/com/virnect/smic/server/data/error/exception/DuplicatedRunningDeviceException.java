package com.virnect.smic.server.data.error.exception;

import lombok.Getter;

import com.virnect.smic.server.data.dto.response.ExecutionResource;

@Getter
public class DuplicatedRunningDeviceException extends RuntimeException{

	private ExecutionResource executionResource;
	public DuplicatedRunningDeviceException(ExecutionResource execution) {
		this.executionResource = execution;
	}
}
