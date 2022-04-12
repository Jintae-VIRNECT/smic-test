package com.virnect.smic.server.service.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.NoSuchElementException;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.SearchExecutionResponse;
import com.virnect.smic.server.data.dto.response.StartExecutionResponse;
import com.virnect.smic.server.data.dto.response.StopExecutionResponse;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.service.application.ExecutionService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/executions")
public class ExecutionRestController {

		private final ExecutionService executionService;

	@PostMapping("start")
	public ResponseEntity startExecution(){
		try {
			Execution execution = executionService.getStartExecutionResult();

			ExecutionResource executionResource = new ExecutionResource(
				new StartExecutionResponse(execution.getId(), execution.getExecutionStatus(), execution.getCreatedDate()));

			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(execution.getId());
			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(selfBuilder.slash("stop").withRel("stop"));

			URI createdUri = selfBuilder.toUri();
			return ResponseEntity.created(createdUri).body(new ApiResponse<>(executionResource));
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@PutMapping("{id}/stop")
	public ResponseEntity stopExecution(@PathVariable(name = "id") Long id){
		try {
			Execution execution = executionService.getStopExecutionResult(id);

			ExecutionResource executionResource = new ExecutionResource(
				new StopExecutionResponse(execution.getId(), execution.getExecutionStatus(), execution.getCreatedDate(),
					execution.getDestroyedDate()));

			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(execution.getId());
			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));

			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(executionResource));
		}catch (NoSuchElementException ne){

			ExecutionResource executionResource = new ExecutionResource();
			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(id).slash("stop");
			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));

			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_EXECUTION_DATA_NULL));
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@GetMapping("{id}")
	public ResponseEntity searchExecution(@PathVariable(name = "id") Long id){
		try {
			Execution execution = executionService.getSearchExecutionResult(id);

			ExecutionResource executionResource = new ExecutionResource(
				new SearchExecutionResponse(
					execution.getId(), execution.getExecutionStatus(), execution.getCreatedDate()
					, execution.getUpdatedDate(), execution.getDestroyedDate()));

			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(execution.getId());
			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(selfBuilder.slash("stop").withRel("stop"));

			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<>(executionResource));
		}catch (NoSuchElementException ne){

			ExecutionResource executionResource = new ExecutionResource();
			WebMvcLinkBuilder selfBuilder = linkTo(ExecutionRestController.class).slash(id);
			executionResource.add(selfBuilder.withSelfRel());
			executionResource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));

			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<>(executionResource, ErrorCode.ERR_EXECUTION_DATA_NULL));
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}
}
