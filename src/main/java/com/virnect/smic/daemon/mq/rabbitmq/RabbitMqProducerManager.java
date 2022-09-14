package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.common.data.domain.TaskletStatus;
import com.virnect.smic.daemon.mq.ProducerManager;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
@Component
public class RabbitMqProducerManager implements ProducerManager {

    private static Channel producer;
    private static Environment env;
    private static  AmqpTemplate template;

    private int numberOfConsumers = 3;

    private AMQP.BasicProperties properties;

    @Autowired
    public RabbitMqProducerManager(Environment env){
        this.env = env;
        try {
            producer = createRabbitMqChannel();
            this.numberOfConsumers = Integer.parseInt(env.getProperty("mq.rabbitmq.num-consumer"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expirationMs = env.getProperty("mq.rabbitmq.expiration-ms");
        properties = new AMQP.BasicProperties.Builder()
            .expiration(expirationMs)
            .build();
    }

    private static Channel createRabbitMqChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(env.getProperty("mq.rabbitmq.host"));
        factory.setPort(Integer.parseInt(env.getProperty("mq.rabbitmq.port")));

        Channel channel = factory.newConnection().createChannel();

        return channel;
    }


    public TaskletStatus runProducer(int _i, String queueName, String value) {
        IntStream.range(1, numberOfConsumers+1).forEach(i->{
            String queueNameWithNumber = String.format("%s.%d",queueName, i);
            try {
                producer.basicPublish("amq.topic", queueNameWithNumber, false, properties, value.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return TaskletStatus.COMPLETED;
    }


}
