package com.virnect.smic.server.data.dto.response;

import java.util.List;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
@Schema(name="executionListResponse", description = "작업 목록 응답")
public class ExecutionListResponse extends RepresentationModel {

	@Schema(name="executions", description = "list of executions")
	private final List<SearchExecutionResource> executions;


	@Schema(name="page", description = "page meta data")
	private final PageMetadataResponse page;


}
