package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.daemon.http.HttpClientManager;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.dao.OrderRepository;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.request.smic.SendOrderRequest;
import com.virnect.smic.server.data.dto.response.PlanResponse;
import com.virnect.smic.server.data.error.exception.KioskLoginFailException;
import com.virnect.smic.server.data.error.exception.NoPlanCDValueException;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchDeviceException;
import com.virnect.smic.server.data.error.exception.NoSuchExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchOrderException;
import com.virnect.smic.server.data.error.exception.SmicUnknownHttpException;

@Service
public class OrderService {
	private final Environment env;
	private final WebClient webClient;
	private final ModelMapper modelMapper;
	private final OrderRepository orderRepository;
	private final ExecutionRepository executionRepository;
	private final DeviceRepository deviceRepository;

	public OrderService(
		Environment env
		, HttpClientManager httpClientHanlder
		, ModelMapper modelMapper
	    , OrderRepository orderRepository
		, ExecutionRepository executionRepository
		, DeviceRepository deviceRepository) {
		this.env = env;
		this.webClient =  WebClient.builder()
			.clientConnector(new JettyClientHttpConnector(httpClientHanlder.httpClient))
			.baseUrl("http://"+ env.getProperty("smic.kiosk.host") + ":" + env.getProperty("smic.kiosk.port"))
			.build();
		this.modelMapper = modelMapper;
		this.orderRepository = orderRepository;
		this.executionRepository = executionRepository;
		this.deviceRepository = deviceRepository;
	}

	@Transactional
	public Order createOrder(ReceivedOrderRequest receivedOrderRequest)
		throws NoSuchExecutionException, KioskLoginFailException {

		Optional<Execution> optExecution = executionRepository.findById(receivedOrderRequest.getExecutionId());

		Execution execution = optExecution.orElseThrow(()->
			// no execution with id
			new NoSuchExecutionException("no execution exists with id "+ receivedOrderRequest.getExecutionId()));

		// execution with id exists but not started status (=> stopped/abandoned)
		if(!execution.getExecutionStatus().equals(ExecutionStatus.STARTED)){
			throw new NoRunningExecutionException();
		}

		Device device = deviceRepository.findByIdAndExecutionId(
			receivedOrderRequest.getDeviceId(), receivedOrderRequest.getExecutionId())
			.orElseThrow(NoSuchDeviceException::new);

		if(isSuccessfulLogin()){
			Optional<String> optPlanCDValue = getPlanCDValue();
			String planCDValue = optPlanCDValue.orElseThrow(NoPlanCDValueException::new);

			SendOrderRequest sendOrderRequest =  modelMapper.map(receivedOrderRequest, SendOrderRequest.class);
			ResponseEntity<Void> response = sendOrderRequest(sendOrderRequest, planCDValue);

			if(response.getStatusCode().value()==10000||response.getStatusCode().value()==200){
				modelMapper.getConfiguration()
					.setMatchingStrategy(MatchingStrategies.STRICT);
				Order order = modelMapper.map(sendOrderRequest, Order.class);
				order.setResponseStatus(response.getStatusCode().value());
				order.setExecution(execution);
				order.setDevice(device);
				Order savedOrder = orderRepository.save(order);
				return savedOrder;
			} else{
				throw new SmicUnknownHttpException(response.getStatusCode()
					, response.getStatusCode().getReasonPhrase());
			}

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
					return new KioskLoginFailException();
				})
			.block();

		//System.out.println(response.toString());
		return !response.getStatusCode().isError();
	}

	Optional<String> getPlanCDValue() {
		String body = "{\"planCat\":\"0\"}";

		PlanResponse response =  webClient.post()
			.uri(uriBuilder -> uriBuilder.path(env.getProperty("smic.kiosk.plan-uri")).build())
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
			.filter(row -> row.getPlan_name().equals("SMIC 현장주문")
				|| row.getPlan_name().equals("SMIC현장주문")
				|| row.getPlan_name().equals("SMIC_현장주문"))
			.collect(Collectors.toList());
		//System.out.println(response.toString());
		if(rows.size()>0){
			return Optional.of(String.valueOf(rows.get(0).getPlan_cd()));
		}
		return Optional.empty();
	}

	private ResponseEntity sendOrderRequest(SendOrderRequest sendOrderRequest, String planCDValue){
		sendOrderRequest.setUserID(env.getProperty("smic.kiosk.user-id"));
		sendOrderRequest.setPlanCDValue(planCDValue);

		return webClient.post()
			.uri(uriBuilder -> uriBuilder.path(
					env.getProperty("smic.kiosk.order-uri"))
				//.queryParam("Accept", "application/json-compressed")
				.build())
			.accept(MediaType.parseMediaType("application/json-compressed"))//MediaType.APPLICATION_JSON) // application/json-compressed
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

	public Order getOrder(Long id) {

		Optional<Order> order = orderRepository.findById(id);
		return order.orElseThrow(NoSuchOrderException::new);
	}
}
