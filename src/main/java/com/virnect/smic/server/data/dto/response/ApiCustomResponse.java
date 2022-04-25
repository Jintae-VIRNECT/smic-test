package com.virnect.smic.server.data.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiCustomResponse {
	@JsonProperty("executions")
	List<SearchExecutionResource> executions;

	@JsonProperty("pages")
	PageMetadataResponse page;


	public ApiCustomResponse(ExecutionListResponse executionList){
		executions = executionList.getExecutions();
		page = executionList.getPage();
	}
}
