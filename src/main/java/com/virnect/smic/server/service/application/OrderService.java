package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.dao.OrderRepository;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.request.smic.SendOrderRequest;
import com.virnect.smic.server.data.dto.response.smic.PlanResponse;
import com.virnect.smic.server.data.error.KioskLoginFailException;
import com.virnect.smic.server.data.error.NoPlanCDValueException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;

@Service
public class OrderService {
	private final Environment env;
	private final HttpClientManager httpClientHanlder;
	private final WebClient webClient;
	private final ModelMapper modelMapper;
	private final OrderRepository orderRepository;
	private final ExecutionRepository executionRepository;

	public OrderService(
		Environment env
		, HttpClientManager httpClientHanlder
		, ModelMapper modelMapper
	    ,OrderRepository orderRepository
		,ExecutionRepository executionRepository) {
		this.env = env;
		this.httpClientHanlder = httpClientHanlder;
		this.webClient =  WebClient.builder()
			.clientConnector(new JettyClientHttpConnector(httpClientHanlder.httpClient))
			.baseUrl("http://"+ env.getProperty("smic.kiosk.host") + ":" + env.getProperty("smic.kiosk.port"))
			.build();
		this.modelMapper = modelMapper;
		this.orderRepository = orderRepository;
		this.executionRepository = executionRepository;
	}

	public Order createOrder(ReceivedOrderRequest receivedOrderRequest) throws NoSuchExecutionException {

		Optional<Execution> optExecution = executionRepository.findById(receivedOrderRequest.getExecution_id());
		Execution execution = optExecution.orElseThrow(()->
			new NoSuchExecutionException("no execution exists with id "+ receivedOrderRequest.getExecution_id()));

		if(isSuccessfulLogin()){
			Optional<String> optPlanCDValue = getPlanCDValue();
			String planCDValue = optPlanCDValue.orElseThrow(NoPlanCDValueException::new);

			SendOrderRequest sendOrderRequest =  modelMapper.map(receivedOrderRequest, SendOrderRequest.class);
			ResponseEntity<Void> response = sendOrderRequest(sendOrderRequest, planCDValue);

			Order order = modelMapper.map(sendOrderRequest, Order.class);
			order.setResponseStatus(response.getStatusCode().value());
			order.setExecution(execution);

			Order savedOrder = orderRepository.save(order);
			return savedOrder;

		} else {
			throw new KioskLoginFailException("smic kiosk login failed");
		}

	}

	private boolean isSuccessfulLogin() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("thingworx-form-userid", env.getProperty("smic.kiosk.user-id"));
		params.add("thingworx-form-password", env.getProperty("smic.kiosk.password"));
		//params.add("OrganizationName", URLEncoder.encode("%26%23x3164;", "UTF-8"));
		params.add("x-thingworx-session","true");

		ResponseEntity<Void> response = webClient.post()
			.uri(uriBuilder -> uriBuilder.path(
					env.getProperty("smic.kiosk.login-uri"))
				.queryParams(params)
				.build())
			.retrieve()
			.toBodilessEntity()
			.onErrorMap(
				throwable -> {
					throwable.printStackTrace();
					return throwable;
				})
			.block();

		//System.out.println(response.toString());
		return !response.getStatusCode().isError();
	}

	private Optional<String> getPlanCDValue() {
		String body = "{\"planCat\":\"0\"}";

		PlanResponse response =  webClient.post()
			.uri(env.getProperty("smic.kiosk.plan-uri"))
			.accept(MediaType.APPLICATION_JSON) // application/json-compressed
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.retrieve()
			.bodyToMono(PlanResponse.class)
			.onErrorMap(
				throwable -> {
					throwable.printStackTrace();
					return throwable;
				})
			.block();

		List<PlanResponse.Row> rows = response
			.getRows()
			.stream()
			.filter(row -> row.get_1().equals("SMIC 현장주문")
				|| row.get_1().equals("SMIC현장주문")
				|| row.get_1().equals("SMIC_현장주문"))
			.collect(Collectors.toList());
		//System.out.println(response.toString());
		if(rows.size()>0){
			return Optional.of(String.valueOf(rows.get(0).get_0()));
		}
		return Optional.empty();
	}

	private ResponseEntity sendOrderRequest(SendOrderRequest sendOrderRequest, String planCDValue){
		sendOrderRequest.setUserID(env.getProperty("smic.kiosk.user-id"));
		sendOrderRequest.setPlanCDValue(planCDValue);

		return webClient.post()
			.uri(uriBuilder -> uriBuilder.path(
					env.getProperty("smic.kiosk.order-uri"))
				.build())
			.accept(MediaType.APPLICATION_JSON) // application/json-compressed
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(sendOrderRequest), SendOrderRequest.class)
			.retrieve()
			.toBodilessEntity()
			.onErrorMap(
				throwable -> {
					throwable.printStackTrace();
					return throwable;
				})
			.block();
	}
}
