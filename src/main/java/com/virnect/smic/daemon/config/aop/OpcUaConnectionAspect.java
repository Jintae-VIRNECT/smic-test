package com.virnect.smic.daemon.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.daemon.config.connection.ConnectionPoolImpl;

@Slf4j
@Aspect
@Component
public class OpcUaConnectionAspect {

	OpcUaClient client;

	@Around("@annotation(com.virnect.smic.daemon.config.annotation.OpcUaConnection)")
	public void doConnection(ProceedingJoinPoint joinPoint) throws Throwable{
		Object[] args = joinPoint.getArgs();
		log.info("********************[connection] {} args={}", joinPoint.getSignature(),args );
		ConnectionPoolImpl pool = ConnectionPoolImpl.getInstance();
		client = pool.getConnection();

		args[0] = client;
		joinPoint.proceed(args);
		pool.releaseConnection(client);
	}
}
