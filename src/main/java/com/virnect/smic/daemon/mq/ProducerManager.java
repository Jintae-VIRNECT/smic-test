package com.virnect.smic.daemon.mq;

import java.io.IOException;

import com.virnect.smic.common.data.domain.TaskletStatus;

import org.springframework.stereotype.Service;

@Service
public interface ProducerManager {

    default String getHost(){
        return null;
    }

    TaskletStatus runProducer(final int sendMessageCount, String topic, String value) throws IOException ;
}
