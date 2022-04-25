package com.virnect.smic.server.config;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

//@Component
@RequiredArgsConstructor
public class Workaround implements WebMvcOpenApiTransformationFilter {

	private final Environment env;

	@Override
	public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
		String serverUrl = env.getProperty("server.host") + ":" + env.getProperty("server.port");

		OpenAPI openApi = context.getSpecification();
		Server localServer = new Server();
		localServer.setDescription("smic-api-server");
		localServer.setUrl(serverUrl);

		openApi.setServers(Arrays.asList(localServer));
		return openApi;
	}

	@Override
	public boolean supports(DocumentationType documentationType) {
		return documentationType.equals(DocumentationType.OAS_30);
	}
}