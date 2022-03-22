package com.virnect.smic.daemon.config;

import com.virnect.smic.common.service.tasklet.ReadTasklet;
import com.virnect.smic.daemon.config.support.SchedulingTaskLauncher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:application.yml")
public class SchedulingConfiguration {

    private final ReadTasklet tasklet;

    @Bean
    @ConditionalOnProperty(prefix="server",name="daemon", havingValue= "true")
    SchedulingTaskLauncher schedulingTaskLauncher(){
        return new SchedulingTaskLauncher(tasklet);
    }
}
