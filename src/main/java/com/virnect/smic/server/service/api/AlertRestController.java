package com.virnect.smic.server.service.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.service.application.AlertService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alerts")
public class AlertRestController {

	private final AlertService alertService;

	@GetMapping("/summary")
	@Operation(summary = "알림 메세지 요약 조회", description = "알림 메세지 요약(alert message summary) 정보를 조회합니다.")
	public ResponseEntity<ApiResponse> getSummary(){
		try {
			String data = alertService.getSummary();
			return ResponseEntity.status(HttpStatus.OK)
				.body(new ApiResponse<String>(data));
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<String>(e.getMessage()));
		}
	}
}
