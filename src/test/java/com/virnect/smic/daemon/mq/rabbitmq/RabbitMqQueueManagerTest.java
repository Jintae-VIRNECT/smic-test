package com.virnect.smic.daemon.mq.rabbitmq;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import com.virnect.smic.common.data.dto.RabbitmqChannelResponse;
import com.virnect.smic.daemon.http.HttpClientManager;
import com.virnect.smic.daemon.http.RabbitmqStatusService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RabbitMqQueueManagerTest {

	@Mock
	WebClient webClient;

	@Autowired
	private  Environment env;

	@Autowired
	private  HttpClientManager httpClientHanlder;

	@Autowired
	private  ModelMapper modelMapper;

	RabbitmqStatusService rabbitmqStatusService;

	// ContentResponse contentResponse;
	//
	// @BeforeAll
	// void httpClinet(){
	// 	contentResponse = new ContentResponse() {
	// 		@Override
	// 		public Request getRequest() {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public <T extends ResponseListener> List<T> getListeners(Class<T> listenerClass) {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public HttpVersion getVersion() {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public int getStatus() {
	// 			return 200;
	// 		}
	//
	// 		@Override
	// 		public String getReason() {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public HttpFields getHeaders() {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public boolean abort(Throwable cause) {
	// 			return false;
	// 		}
	//
	// 		@Override
	// 		public String getMediaType() {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public String getEncoding() {
	// 			return null;
	// 		}
	//
	// 		@Override
	// 		public byte[] getContent() {
	// 			return new byte[0];
	// 		}
	//
	// 		@Override
	// 		public String getContentAsString() {
	// 			return "[\n"
	// 				+ "  {\n"
	// 				+ "    \"acks_uncommitted\": 0,\n"
	// 				+ "    \"confirm\": false,\n"
	// 				+ "    \"connection_details\": {\n"
	// 				+ "      \"name\": \"172.17.0.1:40310 -> 172.17.0.2:5672\",\n"
	// 				+ "      \"peer_host\": \"172.17.0.1\",\n"
	// 				+ "      \"peer_port\": 40310\n"
	// 				+ "    },\n"
	// 				+ "    \"consumer_count\": 2,\n"
	// 				+ "    \"garbage_collection\": {\n"
	// 				+ "      \"fullsweep_after\": 65535,\n"
	// 				+ "      \"max_heap_size\": 0,\n"
	// 				+ "      \"min_bin_vheap_size\": 46422,\n"
	// 				+ "      \"min_heap_size\": 233,\n"
	// 				+ "      \"minor_gcs\": 4\n"
	// 				+ "    },\n"
	// 				+ "    \"global_prefetch_count\": 0,\n"
	// 				+ "    \"idle_since\": \"2022-05-07 14:20:32\",\n"
	// 				+ "    \"messages_unacknowledged\": 0,\n"
	// 				+ "    \"messages_uncommitted\": 0,\n"
	// 				+ "    \"messages_unconfirmed\": 0,\n"
	// 				+ "    \"name\": \"172.17.0.1:40310 -> 172.17.0.2:5672 (1)\",\n"
	// 				+ "    \"node\": \"rabbit@4dbfa6d47887\",\n"
	// 				+ "    \"number\": 1,\n"
	// 				+ "    \"pending_raft_commands\": 0,\n"
	// 				+ "    \"prefetch_count\": 0,\n"
	// 				+ "    \"reductions\": 10118,\n"
	// 				+ "    \"reductions_details\": {\n"
	// 				+ "      \"rate\": 0\n"
	// 				+ "    },\n"
	// 				+ "    \"state\": \"running\",\n"
	// 				+ "    \"transactional\": false,\n"
	// 				+ "    \"user\": \"guest\",\n"
	// 				+ "    \"user_who_performed_action\": \"guest\",\n"
	// 				+ "    \"vhost\": \"/\"\n"
	// 				+ "  }\n"
	// 				+ "]";
	// 		}
	// 	};
	//
	// }

	@Test
	void checkDeviceStatus() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		//factory.setHost("211.50.11.85");
		factory.setPort(5672);
		Map<String, Object> prop = new HashMap<>();
		prop.put("name", "virnect-test-app");
		factory.setClientProperties(prop);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
			System.out.println(" [x] Received '" + message + "'");
		};

		int count=0;

		while (true) {
			count++;
			channel.basicConsume("010_AGV_KIT.EQUIPMENT.ERROR.ERROR_CODE.AGV_NO_1", false, deliverCallback, consumerTag -> {

			});
			Thread.sleep(1000);
			if(count>5)break;
		}

		URI uri = new URI("http://localhost:15672/api/channels");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
		RabbitmqChannelResponse rabbitmqChannelResponse = new RabbitmqChannelResponse(
			"172.17.0.1:40310 -> 172.17.0.2:5672 (1)","2022-05-07 14:20:32");

		List<RabbitmqChannelResponse> lst = new ArrayList<>();
		lst.add(rabbitmqChannelResponse);

		 // when(webClient.get().uri(uri).retrieve().bodyToFlux(RabbitmqChannelResponse.class).collectList().block())
		 // 	.thenReturn(lst);

		rabbitmqStatusService = new RabbitmqStatusService(env, httpClientHanlder, modelMapper);
		Optional<RabbitmqChannelResponse[]> rabbitmqChannelInfo = rabbitmqStatusService.getRabbitmqChannelInfo();

		List<RabbitmqChannelResponse> lst2 = new ArrayList<>();
		if(rabbitmqChannelInfo.isPresent()){
			lst2 = Arrays.stream(rabbitmqChannelInfo.get()).collect(Collectors.toList());
		}
		Boolean isAllIdle = rabbitmqStatusService.isAllDevicesIdle(lst2);
		Thread.sleep(1000);
		assertTrue(isAllIdle);


	}
}