package com.virnect.smic.common.service.tasklet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ReadTaskletTest{

	@Autowired
	private ReadTasklet readTasklet;

	@Autowired
	private Environment env;

	@Test
	void concurrentConsumerTestWithDifferentQueue() throws IOException, TimeoutException, InterruptedException {
		// given
		int numberOfConsumers = Integer.parseInt(env.getProperty("mq.rabbitmq.num-consumer"));
		String queueName = "010_AGV_KIT.ANIMATION.RUNNING.KIT";
		String pubValue = String.valueOf((Math.random()*100)+1);
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
		result.put(queueName, pubValue);
		System.out.println("pub value: " + pubValue);
		readTasklet.publishAndLogAsync(result, "TEST001");

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(queueName, false, false, false, null);

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Logger taskletLogger = (Logger) LoggerFactory.getLogger(ReadTaskletTest.class);
		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		taskletLogger.addAppender(listAppender);

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				log.info( Thread.currentThread().getName() + " [x] Received '" + message + "'");
				//Assert.assertEquals(pubValue, message);
			}
		};

		ExecutorService executorService = Executors.newFixedThreadPool(numberOfConsumers);
		CompletableFuture[] futures = new CompletableFuture[numberOfConsumers];
		for(int i=0; i<numberOfConsumers; i++){
			String queueNameWithNumber = String.format("%s.%d",queueName, i+1);

			futures[i] =  CompletableFuture.runAsync(()->{

				try {
					// when
					channel.basicConsume(queueNameWithNumber, false, consumer);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}, executorService);
		}

		CompletableFuture.allOf(futures).join();

		Thread.sleep(500);

		List<ILoggingEvent> logsList = listAppender.list;
		List<ILoggingEvent> collect = logsList.stream()
			.filter(log -> log.getFormattedMessage().contains(pubValue))
			.collect(Collectors.toList());

		// then
		Assert.assertEquals(numberOfConsumers, collect.size());

	}
}