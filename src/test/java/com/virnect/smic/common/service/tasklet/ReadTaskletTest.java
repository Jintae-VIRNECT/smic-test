package com.virnect.smic.common.service.tasklet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
//@Execution(ExecutionMode.CONCURRENT)
class ReadTaskletTest{

	@Autowired
	private ReadTasklet readTasklet;

	@Autowired
	private Environment env;

	//@Test
	void concurrentConsumerTestWithDifferentQueue() throws IOException, TimeoutException, InterruptedException {
		// given
		int numberOfConsumers = Integer.parseInt(env.getProperty("mq.rabbitmq.num-consumer"));
		String queueName = "010_AGV_KIT.ANIMATION.RUNNING.KIT";
		String pubValue = String.valueOf((Math.random()*100)+1);
		publish(queueName, pubValue);

		Channel channel =getChannel(queueName);

		ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
		listAppender.start();
		Logger taskletLogger = (Logger) LoggerFactory.getLogger(ReadTaskletTest.class);
		taskletLogger.addAppender(listAppender);

		Consumer consumer = getConsumer(channel);

		// when
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfConsumers);
		CompletableFuture[] futures = new CompletableFuture[numberOfConsumers];
		for(int i=0; i<numberOfConsumers; i++){
			String queueNameWithNumber = String.format("%s.%d",queueName, i+1);
			futures[i] =  CompletableFuture.runAsync(()->{

				try {
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

	//@Test
	void concurrentConsumerTestWithDifferentQueue2() throws IOException, TimeoutException, InterruptedException {
		// given
		String queueName1 = "010_AGV_KIT.ANIMATION.RUNNING.KIT.1";
		String queueName2 = "010_AGV_KIT.ANIMATION.RUNNING.KIT.1";

		Channel channel1 =getChannel(queueName1);
		Channel channel2 =getChannel(queueName2);

		Consumer consumer1 = new DefaultConsumer(channel1) {
			@Override
			public void handleDelivery(
				String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body
			) throws IOException {
				String message = new String(body, "UTF-8");
				log.info("consumer1 "+ Thread.currentThread().getName() + " [x] Received '" + message + "'");
			}
		};
		Consumer consumer2 = new DefaultConsumer(channel2) {
			@Override
			public void handleDelivery(
				String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body
			) throws IOException {
				String message = new String(body, "UTF-8");
				log.info("consumer2 "+ Thread.currentThread().getName() + " [x] Received '" + message + "'");
			}
		};

		while(true){

				try {
					channel1.basicConsume(queueName1, true, consumer1);

					//Thread.sleep(2000);
					channel2.basicConsume(queueName2, true, consumer2);
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	@NotNull
	private DefaultConsumer getConsumer(Channel channel) {
		return new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(
				String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body
			) throws IOException {
				String message = new String(body, "UTF-8");
				log.info(Thread.currentThread().getName() + " [x] Received '" + message + "'");
			}
		};
	}

	private void publish(String queueName, String pubValue){
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
		result.put(queueName, pubValue);
		System.out.println("pub value: " + pubValue);
		readTasklet.publishAndLogAsync(result, "TEST001");
	}

	private Channel getChannel(String queueName) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-max-length", 1);
		//args.put("x-message-ttl", 5000);
		channel.queueDeclare(queueName, false, false, false, args);

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		return  channel;
	}

	//@Test
	void publishTest() throws InterruptedException, IOException, TimeoutException {
		String queueName = "010_AGV_KIT.ANIMATION.RUNNING.KIT.1";

		int i=0;
		while(true){
			i++;
			ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
			result.put(queueName, String.valueOf(i) );
			readTasklet.publishAndLogAsync(result, "TEST001");
			System.out.println("pub "+ queueName+","+ i);

			Thread.sleep(500);
		}

	}
}