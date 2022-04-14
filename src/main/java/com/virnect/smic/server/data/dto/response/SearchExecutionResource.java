package com.virnect.smic.server.data.dto.response;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Schema(name="defaultExecutionResource", description = "기본 작업 응답 리소스")
public class SearchExecutionResource extends RepresentationModel {

	@JsonUnwrapped
	@Schema(name="searchExecutionResponse")
	private SearchExecutionResponse searchExecutionResponse;

	public SearchExecutionResource(SearchExecutionResponse searchExecutionResponse){
		this.searchExecutionResponse = searchExecutionResponse;
	}
}
