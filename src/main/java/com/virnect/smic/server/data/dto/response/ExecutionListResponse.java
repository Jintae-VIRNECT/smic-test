package com.virnect.smic.server.data.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
//@Schema(name="executionListResponse", description = "작업 목록 응답")
public class ExecutionListResponse {

	//@Schema
	private final List<SearchExecutionResource> executionList;
	//@Schema
	private final PageMetadataResponse pageMeta;
}
