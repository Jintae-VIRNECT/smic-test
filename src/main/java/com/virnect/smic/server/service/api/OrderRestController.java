package com.virnect.smic.server.service.api;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.assembler.OrderModelAssembler;
import com.virnect.smic.server.data.dto.response.OrderResource;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.exception.KioskLoginFailException;
import com.virnect.smic.server.data.error.exception.NoPlanCDValueException;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchOrderException;
import com.virnect.smic.server.data.error.exception.SmicUnknownHttpException;
import com.virnect.smic.server.service.application.OrderService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
//@Tag(name="order", description="주문 API")
public class OrderRestController {

	private final OrderService orderService;
	private final OrderModelAssembler assembler;

	@PostMapping(produces = "application/hal+json")
	@Operation(summary = "주문 요청", description = "주문 생성을 요청합니다.")
	public ResponseEntity<ApiResponse<OrderResource>> createOrder(@RequestBody @Valid ReceivedOrderRequest orderRequest
		, Errors errors){

		log.info("createOrder [request]: "+ orderRequest.toString());

		if(errors.hasErrors()){
			OrderResource resource = new OrderResource();
			resource.setErrors(errors.getAllErrors());
			return ResponseEntity.badRequest().body(
				new ApiResponse<OrderResource>(resource, ErrorCode.ERR_INVALID_REQUEST_PARAMETER));
		}

		try {
			Order order = orderService.createOrder(orderRequest);
			URI createdUri = linkTo(OrderRestController.class).slash(order.getId()).toUri();

			return ResponseEntity.created(createdUri).body(new ApiResponse<OrderResource>(assembler.toModel(order)));

		} catch (NoSuchExecutionException nsee) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<OrderResource>(
				assembler.withoutModel(nsee),
				ErrorCode.ERR_EXECUTION_DATA_NULL
			));
		} catch (NoRunningExecutionException nree) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<OrderResource>(
				assembler.withoutModel(nree),
				ErrorCode.ERR_EXECUTION_DATA_NOT_RUNNING
			));
		} catch (NoPlanCDValueException npce) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<OrderResource>(
				assembler.withoutModel(npce),
				ErrorCode.ERR_EXTERNAL_ERROR_NO_PLAN_CODE
			));
		} catch (KioskLoginFailException klfe) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<OrderResource>(
				assembler.withoutModel(klfe),
				ErrorCode.ERR_EXTERNAL_ERROR_LOGIN_FAIL
			));
		} catch(SmicUnknownHttpException suhe){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<OrderResource>(
				assembler.withoutModel(suhe),
				ErrorCode.ERR_EXTERNAL_ERROR_LOGIN_FAIL
			));
		}catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<OrderResource>(
					assembler.withoutModel(e),
					ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}
	}
	@GetMapping(value = "{id:^[0-9]*$}", produces = "application/hal+json")
	@Operation(summary = "주문 조회", description = "id에 해당하는 주문 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<OrderResource>> getOrder(@PathVariable(name = "id", required = true) Long id){

		try {
			Order order = orderService.getOrder(id);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<OrderResource>(assembler.toModel(order)));
		}catch(NoSuchOrderException nse){
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ApiResponse<OrderResource>(
				assembler.withoutModel(nse),
				ErrorCode.ERR_ORDER_DATA_NULL
			));
		}catch (Exception e){
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body(new ApiResponse<OrderResource>(
					assembler.withoutModel(e),
					ErrorCode.ERR_UNEXPECTED_SERVER_ERROR));
		}

	}
}
