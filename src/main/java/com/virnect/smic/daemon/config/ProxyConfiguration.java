package com.virnect.smic.daemon.config;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.dao.TaskRepository;
import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.daemon.config.advice.LogTraceAdvice;
import com.virnect.smic.daemon.config.support.SimpleTaskLauncher;
import com.virnect.smic.daemon.service.ReadServiceRunnable;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class ProxyConfiguration {


    // @Bean
    // public ReadTasklet readTasklet(LogTrace logTrace){
    //     ReadTasklet readTasklet = new ReadTasklet();
    //     ProxyFactory factory = new ProxyFactory(readTasklet);
    //     factory.addAdvisor(getAdvisor(logTrace));
    //     ReadTasklet proxy = (ReadTasklet) factory.getProxy();
    //     log.info("ProxyFactory proxy={}, target={}", proxy.getClass(), readTasklet.getClass());
    //     return proxy;
    // }

    private Advisor getAdvisor(LogTrace logTrace){

        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("runReadService");

        LogTraceAdvice advice = new LogTraceAdvice(logTrace);
        return new DefaultPointcutAdvisor(pointcut, advice);

    }
    
}
