package com.virnect.smic.server.service.application;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Collections;

import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.daemon.http.HttpClientManager;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.dao.OrderRepository;
import com.virnect.smic.server.data.dto.request.ReceivedOrderRequest;
import com.virnect.smic.server.data.dto.response.PlanResponse;
import com.virnect.smic.server.data.error.exception.NoPlanCDValueException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderServiceTest {

	private final Environment env;

	private final ModelMapper modelMapper;

	private final OrderRepository orderRepository;
	private final ExecutionRepository executionRepository;
	private final DeviceRepository deviceRepository;
	private final HttpClientManager httpClientHanlder;

	static OrderService orderService;

	// mock web server
	public static MockWebServer mockBackEnd;
	private Dispatcher dispatcher;

	static ReceivedOrderRequest orderRequest;

	OrderServiceTest(
		@Autowired Environment env, @Autowired ModelMapper modelMapper,
		@Autowired OrderRepository orderRepository,
		@Autowired ExecutionRepository executionRepository,
		@Autowired DeviceRepository deviceRepository,
		@Autowired HttpClientManager httpClientHanlder
	) {
		this.env = env;
		this.modelMapper = modelMapper;
		this.orderRepository = orderRepository;
		this.executionRepository = executionRepository;
		this.deviceRepository = deviceRepository;
		this.httpClientHanlder = httpClientHanlder;

	}

	@BeforeAll
	static void setup(@Autowired Environment env, @Autowired ModelMapper modelMapper,
		@Autowired OrderRepository orderRepository,
		@Autowired ExecutionRepository executionRepository,
		@Autowired DeviceRepository deviceRepository,
		@Autowired HttpClientManager httpClientHanlder) throws IOException {
		orderService = new OrderService(
			env, httpClientHanlder, modelMapper, orderRepository, executionRepository, deviceRepository
		);

		orderRequest = new ReceivedOrderRequest();

		mockBackEnd = new MockWebServer();
		mockBackEnd.start();
	}

	@BeforeEach
	void initialize() {

	}

	@Test
	@Transactional
	void create_order_when_no_plan_code_provided() {

		//given
		PlanResponse planResponse =  new PlanResponse();
		planResponse.setRows(Collections.EMPTY_LIST);

		Execution execution = new Execution();
		execution = executionRepository.save(execution);
		Device device = new Device("temp123", execution);
		device = deviceRepository.save(device);

		orderRequest.setExecutionId(execution.getId());
		orderRequest.setDeviceId(device.getId());
		Gson gson = new Gson();

		dispatcher = new Dispatcher() {

			@NotNull
			@Override
			public MockResponse dispatch(RecordedRequest request) {
				if (request.getPath().contains(env.getProperty("smic.kiosk.login-uri")))
					return new MockResponse().setResponseCode(200);
				else if (request.getPath().contains((env.getProperty("smic.kiosk.plan-uri"))))
					return new MockResponse().setBody(gson.toJson(planResponse));

				return new MockResponse().setResponseCode(404);
			}
		};
		mockBackEnd.setDispatcher(dispatcher);

		// when then
		assertThrows(NoPlanCDValueException.class, ()->orderService.createOrder(orderRequest));
	}

	@AfterAll
	static void tearDown() throws IOException {
		mockBackEnd.shutdown();
	}

}