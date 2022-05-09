package com.virnect.smic.daemon.http;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.virnect.smic.common.data.dto.RabbitmqChannelResponse;

@Service
public class RabbitmqStatusService {

	private final Environment env;
	private final HttpClientManager httpClientHanlder;
	private final WebClient webClient;
	private final ModelMapper modelMapper;

	public RabbitmqStatusService(
		Environment env, HttpClientManager httpClientHanlder,
		ModelMapper modelMapper
	) {
		this.env = env;
		this.httpClientHanlder = httpClientHanlder;
		this.modelMapper = modelMapper;
		this.webClient = WebClient.builder()
			.clientConnector(new JettyClientHttpConnector(httpClientHanlder.httpClient))
			.baseUrl("http://"
				+ env.getProperty("mq.rabbitmq.host") + ":" + env.getProperty("mq.rabbitmq.management-port"))
			.build();
	}

	public Optional<RabbitmqChannelResponse[]> getRabbitmqChannelInfo(){

		String userpass =   "guest:guest";
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));

		Mono<RabbitmqChannelResponse[]> block = webClient.get()
			.uri(uriBuilder -> uriBuilder.path(env.getProperty("mq.rabbitmq.channel-uri")).build())
			.header("Content-Type", "application/x-www-form-urlencoded")
			.header(HttpHeaders.AUTHORIZATION,  basicAuth)
			.retrieve()
			.bodyToMono(RabbitmqChannelResponse[].class)
			.log();

		return block.blockOptional();
	}

	public Boolean isAllDevicesIdle(List<RabbitmqChannelResponse> responses) {
		return true;
	}
}
