package com.virnect.smic.daemon.config.aop;

import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TraceStatus;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class TimeLogTraceAspect {
    
    private final LogTrace trace;

    @Around("execution(* *..*.service..*Service*.*(..)) || execution(* com.virnect.smic.daemon.config.support.SimpleTaskLauncher.*(..))")
    public Object traceLogAround(ProceedingJoinPoint joinPoint) {
        TraceStatus status = null;
        Object result = null;
        try{
                String className = joinPoint.getTarget().getClass().getName();
                String methodNamd = joinPoint.getSignature().getName() + "()";
                status = trace.begin(className, methodNamd);

                try {
                    result =  joinPoint.proceed();
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
