package com.virnect.smic.server.service.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.dto.response.ExecutionModelAssembler;
import com.virnect.smic.server.data.dto.response.SearchExecutionResource;
import com.virnect.smic.server.data.dto.response.StartExecutionResource;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.SearchExecutionResponse;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;
import com.virnect.smic.server.service.application.ExecutionService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/executions")
@Tag(name="execution", description="작업 시작/종료/조회 API")
public class ExecutionRestController {

	private final ExecutionService executionService;
	private final ExecutionModelAssembler assembler;
	@PostMapping("start")
	@Operation(summary = "작업 시작", description = "smic 데이터 연동을 시작합니다.")
	// , responses = {
	// 	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "ok"
	// 		, content = @Content(mediaType = "application/json"))
	// })
	public ResponseEntity<ApiResponse<StartExecutionResource>> startExecution() {

		try {
			Execution execution = executionService.getStartExecutionResult();

			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(execution.getId());
			URI createdUri = selfBuilder.toUri();
			return ResponseEntity.created(createdUri)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<StartExecutionResource>(assembler.toModel(execution)));

		 } catch (DuplicatedRunningExecutionException de) {
			return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<StartExecutionResource>(
					assembler.withoutModel(de),
				ErrorCode.ERR_EXECUTION_DATA_DUPLICATED
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<StartExecutionResource>(
					assembler.withoutModel(e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@PutMapping("{id}/stop")
	@Operation(summary = "작업 종료", description = "smic 데이터 연동을 종료합니다.")
	public ResponseEntity stopExecution(
		@Parameter(name="execution id", description="작업 id", required = true) @PathVariable(name = "id") Long id) {
		SearchExecutionResource executionResource = new SearchExecutionResource();
		WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(id).slash("stop");
		;
		executionResource.add(selfBuilder.withSelfRel());
		executionResource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));

		try {
			Execution execution = executionService.getStopExecutionResult(id);

			SearchExecutionResponse stopExecutionResponse =
				new SearchExecutionResponse(execution.getId(), execution.getExecutionStatus()
					, execution.getCreatedDate(), execution.getUpdatedDate());
			executionResource.setSearchExecutionResponse(stopExecutionResponse);

			selfBuilder = selfBuilder.slash(execution.getId());

			executionResource.hasLinks();
			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(executionResource));
		} catch (NoRunningExecutionException nre) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_EXECUTION_DATA_NOT_RUNNING));
		} catch (NoSuchExecutionException ne) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_EXECUTION_DATA_NULL));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@GetMapping("{id}")
	public ResponseEntity searchExecution(@PathVariable(name = "id") Long id) {

		SearchExecutionResource executionResource = new SearchExecutionResource();
		WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(id);
		executionResource.add(selfBuilder.withSelfRel());

		try {
			Execution execution = executionService.getSearchExecutionResult(id);

			SearchExecutionResponse searchExecutionResponse =
				new SearchExecutionResponse(
					execution.getId(), execution.getExecutionStatus(), execution.getCreatedDate()
					, execution.getUpdatedDate());
			executionResource.setSearchExecutionResponse(searchExecutionResponse);

			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(selfBuilder.slash("stop").withRel("stop"));

			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<>(executionResource));
		} catch (NoSuchElementException ne) {

			executionResource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));
			executionResource.add(linkTo(ExecutionRestController.class).withRel("search-list"));

			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_EXECUTION_DATA_NULL));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@GetMapping("/")
	public ResponseEntity searchExecutionList() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@searchExecutionList");
		return  null;
	}
}
