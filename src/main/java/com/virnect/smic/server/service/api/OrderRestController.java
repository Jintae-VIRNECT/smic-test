package com.virnect.smic.server.service.api;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.service.application.OrderService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderRestController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity createOrder(@RequestBody ReceivedOrderRequest orderRequest){
		try {
			Order order = orderService.createOrder(orderRequest);
			return ResponseEntity.created(null).body(new ApiResponse<>());
		}catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}
}
