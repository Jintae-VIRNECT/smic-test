package com.virnect.smic.common.data.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ExecutionStatus {
	STARTED, STOPPED, ABANDONED, UNKNOWN;
}
