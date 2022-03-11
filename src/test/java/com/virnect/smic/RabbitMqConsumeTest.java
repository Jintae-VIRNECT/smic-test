package com.virnect.smic;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.junit.jupiter.api.Test;

public class RabbitMqConsumeTest {
    @Test
    public static void consume() throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //factory.useSslProtocol("TLSv1.2");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };

        channel.basicConsume("AddressSpace.OPC_UA_01.010_AGV_KIT.R10016", true, deliverCallback, consumerTag -> { });
      
    }
}
