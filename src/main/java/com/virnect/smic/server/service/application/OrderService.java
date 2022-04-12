package com.virnect.smic.server.service.application;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;


import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.response.smic.PlanResponse;

@Service
public class OrderService {
	private final Environment env;
	private final HttpClientManager httpClientHanlder;
	private final WebClient webClient;

	public OrderService(
		Environment env, HttpClientManager httpClientHanlder) {
		this.env = env;
		this.httpClientHanlder = httpClientHanlder;
		this.webClient =  WebClient.builder()
			.clientConnector(new JettyClientHttpConnector(httpClientHanlder.httpClient))
			.baseUrl("http://"+ env.getProperty("smic.kiosk.host") + ":" + env.getProperty("smic.kiosk.port"))
			.build();
	}

	public Order createOrder(ReceivedOrderRequest receivedOrderRequest) {
		if(isSuccessfulLogin()){
			String planCDValue = getPlanCDValue();
		}

		return null;
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
			.block();

		//System.out.println(response.toString());
		return !response.getStatusCode().isError();
	}

	private String getPlanCDValue() {
		String body = "{\"planCat\":\"0\"}";

		PlanResponse response =  webClient.post()
			.uri(env.getProperty("smic.kiosk.plan-uri"))
			.accept(MediaType.APPLICATION_JSON) // application/json-compressed
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(body)
			.retrieve()
			 .bodyToMono(PlanResponse.class)
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
			return String.valueOf(rows.get(0).get_0());
		}
		return null;
	}
}
