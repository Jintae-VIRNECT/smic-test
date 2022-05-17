package com.virnect.smic.server.service.api;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.DeviceResource;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.dto.response.assembler.SearchDeviceModelAssembler;
import com.virnect.smic.server.data.dto.response.assembler.StopDeviceModelAssembler;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchDeviceException;
import com.virnect.smic.server.service.application.DeviceService;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping("/devices")
public class DeviceRestController {

	private final DeviceService deviceService;

	private final StopDeviceModelAssembler stopAssembler;

	private final SearchDeviceModelAssembler searchAssembler;

	@DeleteMapping(value= "/all", produces = "application/hal+json")
	@Operation(summary = "모든 장비 중지", description = "현재 기동 중인 작업의 모든 장비를 정지 상태로 설정합니다."
		+ " 모든 장비가 정지 상태가 되면 데이터 연동 작업도 중지됩니다.")
	public ResponseEntity<ApiResponse<ExecutionResource>> releaseAllDevices(){
		try {
			ExecutionResource executionResource = deviceService.releaseAllDevices();

			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<ExecutionResource>(stopAssembler.withModel(executionResource)));
		} catch (NoRunningExecutionException nre) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<ExecutionResource>(
					stopAssembler.withoutModel(nre)
					, ErrorCode.ERR_EXECUTION_DATA_NOT_RUNNING));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<ExecutionResource>(
					stopAssembler.withoutModel(e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@GetMapping(value="{id}", produces = "application/hal+json")
	@Operation(summary = "장비 정보 조회", description = "장비의 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<DeviceResource>> getDevice(
		@Parameter(name="id", description = "장비 id")
		@PathVariable(name = "id") long id){
		try{
			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<DeviceResource>(searchAssembler.toModel(deviceService.getDeviceInfo(id))));
		} catch (NoSuchDeviceException nde) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<DeviceResource>(
					searchAssembler.withoutModel(nde)
					, ErrorCode.ERR_EXECUTION_DATA_NOT_RUNNING));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<DeviceResource>(
					searchAssembler.withoutModel(e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}

	@GetMapping(value="all", produces = "application/hal+json")
	@Operation(summary = "작업 장비 정보 조회", description = "작업 id에 해당하는 장비의 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<ExecutionResource>> getAllDevices(
		@Parameter(name="executionId", description = "작업 id")
		@RequestParam long executionId){
		try{
			ExecutionResource executionResource = deviceService.getDevicesWithExecution(executionId);
			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<ExecutionResource>(searchAssembler.withExecution(executionResource)));
		} catch (NoSuchElementException nre){
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ApiResponse<ExecutionResource>(
					searchAssembler.withoutExecution(nre)
					, ErrorCode.ERR_EXECUTION_DATA_NULL));
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.contentType(MediaType.APPLICATION_JSON)
				.body(new ApiResponse<ExecutionResource>(
					searchAssembler.withoutExecution(e)
					, ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}

	}
}
