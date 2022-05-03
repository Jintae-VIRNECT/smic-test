package com.virnect.smic.server.service.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.DeviceResource;

@RestController
@RequiredArgsConstructor
@RequestMapping("/devices")
public class DeviceRestController {

	@GetMapping
	public ResponseEntity<ApiResponse<DeviceResource>> getDevice(){
		return null;
	}

}
