package com.virnect.smic.common.config.aop;

import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TraceStatus;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Order(2)
@Component
@RequiredArgsConstructor
public class TimeLogTraceAspect {
    
    private final LogTrace trace;

    //@Around("execution(* *..*.service..*Service*.*(..)) || execution(* com.virnect.smic.daemon.config.support.*TaskLauncher.*(..))")
    @Around("@annotation(com.virnect.smic.common.config.annotation.TimeLogTrace)")
    public Object traceLogAround(ProceedingJoinPoint joinPoint) {
        TraceStatus status = null;
        Object result = null;
        try{
                String className = joinPoint.getTarget().getClass().getName();
                String methodNamd = joinPoint.getSignature().getName();
                status = trace.begin(className, methodNamd);

                // pass UUID
                Object[] args = joinPoint.getArgs();
                args[2] = status.getTraceId().getId();

                try {
                    result =  joinPoint.proceed(args);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                trace.end(status);
                return result;
        } catch(Exception e){
            trace.exception(status, e);
            throw e;
        }
    }
}
