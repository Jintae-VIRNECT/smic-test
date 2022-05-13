package com.virnect.smic.common.util;

import com.virnect.smic.common.data.domain.TaskStatus;
import com.virnect.smic.common.data.domain.TaskletStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeLogTraceUtil implements LogTrace {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    private static final String NO_ERROR_MESSAGE = "NO_ERR";

    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();

    @Override
    public TraceStatus begin(String className, String methodName) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        //log.info("[{}] {}[class]:{} ", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), className);
        log.info("[{}] {} {} {} {}"
        , traceId.getId(), methodName, TaskStatus.STARTED, 0, NO_ERROR_MESSAGE);
        return new TraceStatus(traceId, startTimeMs, className, methodName);
    }

    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }

    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {} {} {} {}"
            , traceId.getId(), status.getMethodName(), TaskStatus.COMPLETED, resultTimeMs
            , NO_ERROR_MESSAGE);
        } else {
            log.info("[{}] {} {} {} {}"
            , traceId.getId(), status.getMethodName(), TaskStatus.FAILED, resultTimeMs
            , e.toString());
        }

        releaseLocal();
    }

    private void releaseLocal() {
        traceIdHolder.remove();
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
