package com.virnect.smic.server.service.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.dto.response.ApiCustomResponse;
import com.virnect.smic.server.data.dto.response.ExecutionListResponse;
import com.virnect.smic.server.data.dto.response.SearchExecutionModelAssembler;
import com.virnect.smic.server.data.dto.response.StartExecutionModelAssembler;
import com.virnect.smic.server.data.dto.response.SearchExecutionResource;
import com.virnect.smic.server.data.dto.response.StartExecutionResource;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;
import com.virnect.smic.server.service.application.ExecutionService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/executions")
//@Tag(name="execution", description="작업 시작/종료/조회 API")
public class ExecutionRestController {

	private final ExecutionService executionService;
	private final StartExecutionModelAssembler startAssembler;
	private final SearchExecutionModelAssembler searchAssembler;

	@PostMapping(produces = "application/hal+json")
	@Operation(summary = "작업 시작", description = "smic 데이터 연동을 시작합니다.")
	// , responses = {
	// 	@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "ok"
	// 		, content = @Content(mediaType = "application/json")
			//, links = {
			//@Link(name = "blabla", operationRef = "20202020")
		//}
	// 	)
	// })
	public ResponseEntity<ApiResponse<StartExecutionResource>> startExecution() {

		try {
			Execution execution = executionService.getStartExecutionResult();

			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(execution.getId());
			URI createdUri = selfBuilder.toUri();
			return ResponseEntity.created(createdUri)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<StartExecutionResource>(startAssembler.toModel(execution)));

		 } catch (DuplicatedRunningExecutionException de) {
			return ResponseEntity.badRequest()
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<StartExecutionResource>(
					startAssembler.withoutModel(de),
				ErrorCode.ERR_EXECUTION_DATA_DUPLICATED
			));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<StartExecutionResource>(
					startAssembler.withoutModel(e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@DeleteMapping(value="{id:^[0-9]*$}", produces = "application/hal+json")
	@Operation(summary = "작업 종료", description = "smic 데이터 연동을 종료합니다.")
	public ResponseEntity<ApiResponse<SearchExecutionResource>> stopExecution(
		@Parameter(name="id", description="작업 id", required = true) @PathVariable(name = "id") Long id) {

		try {
			Execution execution = executionService.getStopExecutionResult(id);

			SearchExecutionResource searchExecutionResource = searchAssembler.toModel(execution);

			// add links
			searchExecutionResource.add(linkTo(ExecutionRestController.class).slash(id).withSelfRel());
			searchExecutionResource.add(linkTo(ExecutionRestController.class).withRel("start"));

			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<SearchExecutionResource>(searchExecutionResource));
		} catch (NoRunningExecutionException nre) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<SearchExecutionResource>(
					searchAssembler.withoutModel(id, nre)
					, ErrorCode.ERR_EXECUTION_DATA_NOT_RUNNING));
		} catch (NoSuchExecutionException ne) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<SearchExecutionResource>(
					searchAssembler.withoutModel(id, ne)
					, ErrorCode.ERR_EXECUTION_DATA_NULL));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<SearchExecutionResource>(
					searchAssembler.withoutModel(id, e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	//@GetMapping(value = "search/{id}", produces = "application/hal+json")
	@GetMapping(value = "{id:^[0-9]*$}", produces = "application/hal+json")
	@Operation(summary = "작업 조회", description = "id에 해당하는 작업 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<SearchExecutionResource>> getExecution(@PathVariable(name = "id", required = true) Long id) {

		try {
			Execution execution = executionService.getSearchExecutionResult(id);

			SearchExecutionResource executionResource = searchAssembler.toModel(execution);

			// add link
			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(id);
			executionResource.add(selfBuilder.withSelfRel());
			//executionResource.add(selfBuilder.withRel("stop"));

			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<SearchExecutionResource>(executionResource));
		} catch (NoSuchElementException ne) {

			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<SearchExecutionResource>(
					searchAssembler.withoutModel(id, ne)
					, ErrorCode.ERR_EXECUTION_DATA_NULL));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<SearchExecutionResource>(
					searchAssembler.withoutModel(id, e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	// @GetMapping(value = "/", produces = "application/hal+json")
	// @Operation(summary = "작업 목록 조회", description = "작업 목록 정보를 조회합니다.")
	// public ResponseEntity<ApiResponse<ExecutionListResponse>> getExecutionList(
	// 	@Parameter(required = false) Pageable pageable) {
	// 	ExecutionListResponse executionList = executionService.getExecutionList(pageable);
	// 	HttpHeaders headers = new HttpHeaders();
	// 	List<MediaType> mediaTypes = new ArrayList<>();
	// 	mediaTypes.add(MediaType.APPLICATION_JSON);
	// 	headers.setAccept(mediaTypes);
	//
	// 	return ResponseEntity.status(HttpStatus.OK)
	// 		.headers(headers)
	// 		.body(new ApiResponse<ExecutionListResponse>(executionList));
	//
	// }

	@GetMapping(value="/latest", produces = "application/hal+json")
	@Operation(summary = "최근 작업 조회", description = "가장 최근의 작업 정보를 조회합니다.", responses={
		@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<ApiResponse<SearchExecutionResource>> getCurrentExecution() {
		Execution execution = executionService.getCurrentExecution();
		SearchExecutionResource searchExecutionResource = searchAssembler.toModel(execution);


		return ResponseEntity.status(HttpStatus.OK)
			.body(new ApiResponse<SearchExecutionResource>(searchExecutionResource));
	}

	// @GetMapping(value = "/", produces = "application/hal+json")
	// @ApiIgnore
	// @Operation(summary = "작업 목록 조회", description = "작업 목록 정보를 조회합니다.", hidden = true)
	// public ResponseEntity<ApiCustomResponse> getExecutionList(
	// 	@Parameter(required = false) Pageable pageable) {
	// 	ExecutionListResponse executionList = executionService.getExecutionList(pageable);
	//
	// 	HttpHeaders headers = new HttpHeaders();
	// 	List<MediaType> mediaTypes = new ArrayList<>();
	// 	mediaTypes.add(MediaType.APPLICATION_JSON);
	// 	headers.setAccept(mediaTypes);
	//
	// 	return ResponseEntity.status(HttpStatus.OK)
	// 		.headers(headers)
	// 		.body(new ApiCustomResponse(executionList));
	//
	// }
}
