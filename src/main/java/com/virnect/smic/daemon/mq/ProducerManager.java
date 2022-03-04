package com.virnect.smic.daemon.mq;

import java.io.IOException;

public interface ProducerManager {
    void runProducer(final int sendMessageCount, String topic, String value) throws IOException ;
}
