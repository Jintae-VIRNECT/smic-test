package com.virnect.smic.daemon.config;

import com.virnect.smic.daemon.config.support.SchedulingTaskLauncher;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;

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
