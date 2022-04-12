package com.virnect.smic.server.data.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.virnect.smic.common.data.domain.ExecutionStatus;

@Data
@AllArgsConstructor
public class StopExecutionResponse {
	private Long executionId;
	private ExecutionStatus executionStatus;
	private LocalDateTime createdDate;
	private LocalDateTime destroyedDate;
}
