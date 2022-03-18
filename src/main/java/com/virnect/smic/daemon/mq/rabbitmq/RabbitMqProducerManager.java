package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.mq.ProducerManager;

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
    
    @Autowired
    public RabbitMqProducerManager(Environment env){
        this.env = env;
        try {
            producer = createRabbitMqChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static Channel createRabbitMqChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(env.getProperty("mq.rabbitmq.host"));
        factory.setPort(Integer.parseInt(env.getProperty("mq.rabbitmq.port")));
        Channel channel = factory.newConnection().createChannel();
        // List<String> tags = getTagList();
        // tags.forEach(tag-> {
        //     try {
        //         String queueName = tag.replaceAll(" ", "");
        //         channel.exchangeDeclare("amp.topic", "topic", true, false, null);
        //         channel.queueDeclare(queueName, false, false, false, null);
        //         channel.queueBind(queueName, "amp.topic", queueName);
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // });
        return channel;
    }

    public ExecutionStatus runProducer(int i, String queueName, String value) {
        try {
            producer.basicPublish("amq.topic", queueName, null, value.getBytes("UTF-8"));
            return ExecutionStatus.COMPLETED;
        } catch (IOException e) {
            e.printStackTrace();
            return ExecutionStatus.FAILED;
        }
    }

}
