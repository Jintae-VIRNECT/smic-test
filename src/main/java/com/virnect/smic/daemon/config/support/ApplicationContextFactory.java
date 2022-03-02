package com.virnect.smic.daemon.config.support;

import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationContextFactory {
	ConfigurableApplicationContext createApplicationContext();
}
