package com.virnect.smic.server.service.application;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpClientManager {

	protected final HttpClient httpClient = new HttpClient() {
		@Override
		public Request newRequest(URI uri) {
			Request request = super.newRequest(uri);
			return enhance(request);
		}
	};

	private Request enhance(Request inboundRequest) {
		StringBuilder sbRequest = new StringBuilder();
		StringBuilder sbResponse = new StringBuilder();

		inboundRequest.onRequestBegin(request ->
			sbRequest.append("\nRequest: \n")
				.append("URI: ")
				.append(request.getURI())
				.append("\n")
				.append("Method: ")
				.append(request.getMethod()));

		       inboundRequest.onRequestHeaders(request -> {
		           sbRequest.append("\nHeaders:\n");
		           for (HttpField header : request.getHeaders()) {
		               sbRequest.append("\t\t" + header.getName() + " : " + header.getValue() + "\n");
		           }
		       });

		inboundRequest.onRequestContent((request, content) -> {
			String bufferAsString = StandardCharsets.UTF_8.decode(content).toString();
			sbRequest.append("\nRequest Body:\n\t" + bufferAsString);
		});

		inboundRequest.onResponseBegin(response ->
			sbResponse.append("\nResponse:\n")
				.append("Status: ")
				.append(response.getStatus())
				.append("\n"));

		       inboundRequest.onResponseHeaders(response -> {
		           sbResponse.append("Headers:\n");
		           for (HttpField header : response.getHeaders()) {
		               sbResponse.append("\t\t" + header.getName() + " : " + header.getValue() + "\n");
		           }
		       });

		inboundRequest.onResponseContent(((response, content) -> {
			String bufferAsString = StandardCharsets.UTF_8.decode(content).toString();
			sbResponse.append("Response Body:\n\t" + bufferAsString);
		}));

		inboundRequest.onRequestSuccess(request -> log.info(sbRequest.toString()));
		inboundRequest.onResponseSuccess(response -> log.info(sbResponse.toString()));

		return inboundRequest;
	}
}
