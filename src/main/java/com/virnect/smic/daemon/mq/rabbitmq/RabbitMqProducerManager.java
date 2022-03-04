package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.daemon.mq.ProducerManager;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
public class RabbitMqProducerManager implements ProducerManager {

    private static Channel producer;

    public RabbitMqProducerManager(){
        try {
            producer = createRabbitMqChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static Channel createRabbitMqChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        return factory.newConnection().createChannel();
    }

    public void runProducer(int i, String queueName, String value) throws IOException {
        producer.basicPublish("", queueName, null, value.getBytes());
    }
    
}
