package com.virnect.smic.daemon.config.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

import com.virnect.smic.daemon.config.connection.ConnectionPoolImpl;
import com.virnect.smic.daemon.config.connection.NoConnectionAvailableException;

@Slf4j
@Aspect
@Component
public class OpcUaConnectionAspect {

	OpcUaClient client;
	private final int MAX_RETRY = 4;

	@Around("@annotation(com.virnect.smic.daemon.config.annotation.OpcUaConnection)")
	public Object doConnection(ProceedingJoinPoint joinPoint) throws Throwable {

		Object[] args = joinPoint.getArgs();
		log.debug("********************[connection] {} args={}", joinPoint.getSignature(), args );

		ConnectionPoolImpl pool = ConnectionPoolImpl.getInstance();

		AtomicInteger count = new AtomicInteger(0);
		while(count.get()< MAX_RETRY){
			try{
				client = pool.getConnection();
				break;
			} catch(NoConnectionAvailableException ncae){
				log.warn("waiting available connection...." + count.get());
				Thread.sleep(500);
				count.getAndIncrement();
			}
		}

		args[0] = client;
		Object result = joinPoint.proceed(args);
		pool.releaseConnection(client);
		return result;
	}

}
