package com.virnect.smic.server.service.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.dto.response.assembler.SearchExecutionModelAssembler;
import com.virnect.smic.server.data.dto.response.assembler.StartExecutionModelAssembler;
import com.virnect.smic.server.data.dto.response.SearchExecutionResource;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.assembler.StopExecutionModelAssembler;
import com.virnect.smic.server.data.error.exception.DuplicatedRunningDeviceException;
import com.virnect.smic.server.data.error.exception.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchExecutionException;
import com.virnect.smic.server.service.application.ExecutionService;

@Slf4j
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/executions")
public class ExecutionRestController {

	private final ExecutionService executionService;
	private final StartExecutionModelAssembler startAssembler;
	private final SearchExecutionModelAssembler searchAssembler;
	private final StopExecutionModelAssembler stopAssembler;

	@PostMapping(produces = "application/hal+json")
	@Operation(summary = "?????? ??????", description = "smic ????????? ????????? ???????????????.")
	public ResponseEntity<ApiResponse<ExecutionResource>>
		startExecution(@Parameter(name="macAddress", description = "??????(????????????) MAC ADDRESS")
			@RequestParam(name="macAddress") String macAddress) {

		try {
			ExecutionResource execution = executionService.getStartExecutionResult(macAddress);

			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(execution.getExecutionId());
			URI createdUri = selfBuilder.toUri();
			return ResponseEntity.created(createdUri)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<ExecutionResource>(startAssembler.withModel(execution)));

		 }  catch (DuplicatedRunningExecutionException de) {
			return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<ExecutionResource>(
					startAssembler.withoutModel(de),
					ErrorCode.ERR_EXECUTION_DATA_DUPLICATED
				));
		} catch (DuplicatedRunningDeviceException dde) {
			return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<ExecutionResource>(
					startAssembler.withoutModel(dde),
					ErrorCode.ERR_DEVICE_DATA_DUPLICATED
				));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<ExecutionResource>(
					startAssembler.withoutModel(e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@DeleteMapping(value="{id:^[0-9]*$}", produces = "application/hal+json")
	@Operation(summary = "?????? ??????", description = "smic ????????? ????????? ???????????????.")
	public ResponseEntity<ApiResponse<ExecutionResource>> stopExecution(
		@Parameter(name="id", description="?????? id", required = true)
		@PathVariable(name = "id") Long id
		, @Parameter(name="deviceId", description = "??????(????????????) id")
		@RequestParam(name="deviceId") long deviceId) {

		try {
			ExecutionResource execution = executionService.getStopExecutionResult(id, deviceId);

			ExecutionResource executionResource = stopAssembler.withModel(execution);

			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<ExecutionResource>(executionResource));
		} catch (NoRunningExecutionException nre) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<ExecutionResource>(
					stopAssembler.withoutModel(id, nre)
					, ErrorCode.ERR_EXECUTION_DATA_NOT_RUNNING));
		} catch (NoSuchExecutionException ne) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<ExecutionResource>(
					stopAssembler.withoutModel(id, ne)
					, ErrorCode.ERR_EXECUTION_DATA_NULL));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<ExecutionResource>(
					stopAssembler.withoutModel(id, e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@GetMapping(value = "{id:^[0-9]*$}", produces = "application/hal+json")
	@Operation(summary = "?????? ??????", description = "id??? ???????????? ?????? ????????? ???????????????.")
	public ResponseEntity<ApiResponse<SearchExecutionResource>>
		getExecution(@PathVariable(name = "id", required = true) Long id) {

		try {
			Execution execution = executionService.getExecutionInfo(id);

			SearchExecutionResource executionResource = searchAssembler.toModel(execution);

			// add link
			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(id);
			executionResource.add(selfBuilder.withSelfRel());

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
	// @Operation(summary = "?????? ?????? ??????", description = "?????? ?????? ????????? ???????????????.")
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
	@Operation(summary = "?????? ?????? ??????", description = "?????? ????????? ?????? ????????? ???????????????.", responses={
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
	// @Operation(summary = "?????? ?????? ??????", description = "?????? ?????? ????????? ???????????????.", hidden = true)
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
