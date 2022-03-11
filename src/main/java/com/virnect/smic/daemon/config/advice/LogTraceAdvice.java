package com.virnect.smic.daemon.config.advice;

import java.lang.reflect.Method;

import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TraceStatus;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LogTraceAdvice implements MethodInterceptor{
    private final LogTrace logTrace;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TraceStatus status = null;
        try{
            Method method = invocation.getMethod();
            // String message = method.getDeclaringClass().getSimpleName()+ "."
            //                 + method.getName()+ "()";
            status = logTrace.begin(method.getDeclaringClass().getSimpleName(), method.getName());

            Object result = invocation.proceed();

            logTrace.end(status);
            return result;
        }catch(Exception e){
            logTrace.exception(status, e);
            throw e;
        }
    }
}
