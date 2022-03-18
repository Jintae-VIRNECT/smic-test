package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.mq.TopicManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
@RequiredArgsConstructor
public class RabbitMqQueueManager implements TopicManager{

    @Autowired
	@Qualifier("tagList")
    private final List<Tag> tagList;

    private final Environment env;

    public void create() throws IOException, TimeoutException {

        List<String> tags = tagList.stream().map(o-> o.getNodeId()).collect(Collectors.toList());

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getProperty("mq.rabbitmq.host"));
        factory.setPort(Integer.parseInt(env.getProperty("mq.rabbitmq.port")));
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
                
                tags.forEach(tag-> {
                    try {
                        String queueName = tag.replaceAll(" ", "");
                        channel.exchangeDeclare("amq.topic", "topic", true, false, null);
                        Map<String, Object> args = new HashMap<String, Object>();
                        args.put("max-length", 1);
                        channel.queueDeclare(queueName, false, false, false, args);
                        channel.queueBind(queueName, "amq.topic", queueName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            );
           // channel.close();
        }
    }

}
