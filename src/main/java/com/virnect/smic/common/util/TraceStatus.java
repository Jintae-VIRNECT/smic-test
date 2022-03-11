package com.virnect.smic.common.util;

import lombok.Getter;

@Getter
public class TraceStatus {
    private TraceId traceId;
    private Long startTimeMs;
    private String className;
    private String methodName;
    
    public TraceStatus(TraceId traceId, Long startTimeMs, String className, String methodName) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.className = className;
        this.methodName = methodName;
    }

}
