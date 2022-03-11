package com.virnect.smic.common.util;

public interface LogTrace {
    TraceStatus begin(String className, String methodName);

    void end(TraceStatus status);

    void exception(TraceStatus status, Exception e);
}
