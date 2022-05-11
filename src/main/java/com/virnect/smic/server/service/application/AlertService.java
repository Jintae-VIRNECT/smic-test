package com.virnect.smic.server.service.application;

import java.util.Base64;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import com.virnect.smic.daemon.http.HttpClientManager;

@Service
public class AlertService {

	private final Environment env;
	private final HttpClientManager httpClientHanlder;
	private final WebClient webClient;
	private final String basicAuth;

	public AlertService(Environment env, HttpClientManager httpClientHanlder) {
		this.env = env;
		this.httpClientHanlder = httpClientHanlder;
		this.webClient = WebClient.builder()
			.clientConnector(new JettyClientHttpConnector(httpClientHanlder.httpClient))
			.baseUrl("http://"
				+ env.getProperty("smic.kiosk.host") + ":" + env.getProperty("smic.kiosk.port"))
			.build();

		String userpass =  env.getProperty("smic.kiosk.user-id") +  ":" + env.getProperty("smic.kiosk.password") ;
		this.basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
	}

	public String getSummary() {

		Mono<String> log = webClient.post()
			.uri(uriBuilder -> uriBuilder.path(env.getProperty("smic.kiosk.alert-summary-uri")).build())
			.accept(MediaType.APPLICATION_JSON)
			.header(HttpHeaders.AUTHORIZATION, basicAuth)
			.contentType(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(String.class)
			.onErrorResume(e->Mono.just(e.getMessage()));
			//.doOnError(e-> {throw new SmicUnknownHttpException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());})
			//.log();

		return log.block();
	}
}
