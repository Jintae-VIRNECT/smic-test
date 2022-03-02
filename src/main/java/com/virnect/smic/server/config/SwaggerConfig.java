package com.virnect.smic.server.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virnect.smic.server.data.error.ErrorCode;
import com.virnect.smic.server.data.error.ErrorResponseMessage;

import org.eclipse.milo.opcua.stack.core.channel.messages.ErrorMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import lombok.RequiredArgsConstructor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@EnableOpenApi
@RequiredArgsConstructor
public class SwaggerConfig {

    private final TypeResolver typeResolver;
	private final ObjectMapper objectMapper;

    String title = "VIRNECT SMIC API Document.";
    String version = "v1.0";

    @Bean
	public List<Response> globalResponseMessage() {
		ArrayList<Response> response = new ArrayList<>();
		for (ErrorCode errorCode : ErrorCode.values()) {
			try {
				response.add(new ResponseBuilder().code(String.valueOf(errorCode.getCode()))
					.description(objectMapper.writeValueAsString(new ErrorResponseMessage(errorCode)))
					.build());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		response.add(new ResponseBuilder().code("200").description("success").build());
		response.add(new ResponseBuilder().code("200").description("success").build());
		response.add(new ResponseBuilder().code("500").description("Server Error").build());
		response.add(new ResponseBuilder().code("404").description("Invalid Request").build());

		return response;
	}

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .globalResponses(HttpMethod.GET, globalResponseMessage())
			    .globalResponses(HttpMethod.POST, globalResponseMessage())
			    .globalResponses(HttpMethod.PUT, globalResponseMessage())
			    .globalResponses(HttpMethod.DELETE, globalResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.virnect.smic.server"))
                .paths(PathSelectors.any())
                .build()
                .additionalModels(typeResolver.resolve(ErrorMessage.class))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .contact(new Contact("Sangeun Oh", "https://virnect.com", "sangeun@virnect.com"))
                .title(title)
                .description("SMIC API Docs")
                .version(version)
                .license("VIRNECT INC All rights reserved")
                .build();
    }
}
