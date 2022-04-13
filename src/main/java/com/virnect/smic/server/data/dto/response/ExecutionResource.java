package com.virnect.smic.server.data.dto.response;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ExecutionResource extends RepresentationModel {
	@JsonUnwrapped
	private StartExecutionResponse startExecutionResponse;

	@JsonUnwrapped
	private StopExecutionResponse stopExecutionResponse;

	@JsonUnwrapped
	private SearchExecutionResponse searchExecutionResponse;

	public ExecutionResource(StartExecutionResponse startExecutionResponse){
		this.startExecutionResponse = startExecutionResponse;
	}

	public ExecutionResource(StopExecutionResponse stopExecutionResponse){
		this.stopExecutionResponse = stopExecutionResponse;
	}

	public ExecutionResource(SearchExecutionResponse searchExecutionResponse){
		this.searchExecutionResponse = searchExecutionResponse;
	}
}
