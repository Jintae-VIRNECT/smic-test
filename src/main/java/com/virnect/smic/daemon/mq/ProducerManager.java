package com.virnect.smic.daemon.mq;

import java.io.IOException;

import com.virnect.smic.common.data.domain.ExecutionStatus;

public interface ProducerManager {
    ExecutionStatus runProducer(final int sendMessageCount, String topic, String value) throws IOException ;
}
