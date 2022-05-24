package com.virnect.smic.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class HttpConfig {
	private final Environment env;

	public HttpConfig(Environment env) {
		this.env = env;
	}

	@Bean(name="httpSmicKiosk")
	public String httpSmicKiosk(){
		return "http://"+ env.getProperty("smic.kiosk.host") + ":" + env.getProperty("smic.kiosk.port");
	}
}
