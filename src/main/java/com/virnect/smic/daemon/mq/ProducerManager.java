package com.virnect.smic.daemon.mq;

import java.io.IOException;

import com.virnect.smic.common.data.domain.ExecutionStatus;

import org.springframework.stereotype.Service;

@Service
public interface ProducerManager {

    default String getHost(){
        return null;
    }

    ExecutionStatus runProducer(final int sendMessageCount, String topic, String value) throws IOException ;
}
