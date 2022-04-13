package com.virnect.smic.server.service.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.OrderResource;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.KioskLoginFailException;
import com.virnect.smic.server.data.error.NoPlanCDValueException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;
import com.virnect.smic.server.service.application.OrderService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderRestController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity createOrder(@RequestBody @Valid ReceivedOrderRequest orderRequest
		, Errors errors){

		if(errors.hasErrors()){
			return ResponseEntity.badRequest().body(
				new ApiResponse<>(errors.getAllErrors(), ErrorCode.ERR_INVALID_REQUEST_PARAMETER));
		}

		try {
			Order order = orderService.createOrder(orderRequest);
			OrderResource orderResource = new OrderResource(order);

			WebMvcLinkBuilder selfBuilder = linkTo(OrderRestController.class).slash(order.getId());
			orderResource.add(selfBuilder.withSelfRel());

			URI createdUri = selfBuilder.toUri();

			if (200 <= order.getResponseStatus() && order.getResponseStatus() < 400)
				return ResponseEntity.created(createdUri).body(new ApiResponse<>(orderResource));
			else
				return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<>(
					orderResource,
					ErrorCode.ERR_EXTERNAL_ERROR_UNKNOWN
				));
		} catch (NoSuchExecutionException nsee) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<>(
				ErrorCode.ERR_EXECUTION_DATA_NULL
			));
		} catch (NoPlanCDValueException npce) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<>(
				ErrorCode.ERR_EXTERNAL_ERROR_NO_PLAN_CODE
			));
		} catch (KioskLoginFailException klfe) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<>(
				ErrorCode.ERR_EXTERNAL_ERROR_LOGIN_FAIL
			));
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<>(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}
}
