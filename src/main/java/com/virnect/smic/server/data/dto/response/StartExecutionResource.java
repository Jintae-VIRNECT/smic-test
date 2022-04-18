package com.virnect.smic.server.data.dto.response;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.common.data.domain.ExecutionStatus;

@Getter @Setter
@Builder
@AllArgsConstructor
@Schema(name="executionResource", description = "작업 응답 리소스")
public class StartExecutionResource extends RepresentationModel<StartExecutionResource> {

	@Schema(name="execution id", description = "작업 id")
	private Long executionId;
	@Schema(name="execution status", description = "작업 상태", example = "STARTED")
	private ExecutionStatus executionStatus;
	@Schema(name="execution created date", description = "작업 생성일시", example = "2022-04-12 16:11:36")
	private LocalDateTime createdDate;

	public StartExecutionResource(){
		super();
	}
}
