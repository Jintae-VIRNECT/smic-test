package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.common.data.dto.TagDto;
import com.virnect.smic.daemon.mq.TopicManager;

import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
@RequiredArgsConstructor
public class RabbitMqQueueManager implements TopicManager{

    private final List<TagDto> tagList;

    private final Environment env;

    public void create() throws IOException, TimeoutException {

        List<String> tags = tagList.stream().map(o-> o.getQueueName()).collect(Collectors.toList());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getProperty("mq.rabbitmq.host"));
        factory.setPort(Integer.parseInt(env.getProperty("mq.rabbitmq.port")));

        int numberOfConsumers = Integer.parseInt(env.getProperty("mq.rabbitmq.num-consumer"));
        int messageTtl = Integer.parseInt(env.getProperty("mq.rabbitmq.expiration-ms"));

        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {

            IntStream.range(1, numberOfConsumers+1).forEach(i ->
                tags.forEach(queueName-> {
                    queueName = String.format("%s.%d",queueName, i);
                    try {
                        channel.exchangeDeclare("amq.topic", "topic", true, false, null);
                        Map<String, Object> args = new HashMap<String, Object>();
                        args.put("x-max-length", 1);
                        args.put("x-message-ttl", messageTtl);
                        channel.queueDeclare(queueName, false, false, false, args);
                        channel.queueBind(queueName, "amq.topic", queueName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                )
            );
           // channel.close();
        }
    }

}
