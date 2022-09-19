package com.virnect.smic.server.service.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

	@Mock
	DeviceRepository deviceRepository;

	@Mock
	DaemonConfiguration daemonConfiguration;

	@Mock
	ExecutionService executionService;

	@Mock
	ModelMapper modelMapper;

	@Mock
	Environment environment;

	DeviceService deviceService;

	static List<Device> devices;

	@BeforeAll
	static void setup(){

		Execution execution = new Execution();
		execution.setId(1L);

		devices =
			List.of(new Device("test1", execution), new Device("test2", execution));
	}

	@BeforeEach
	void initService(){
		deviceService = new DeviceService(deviceRepository, daemonConfiguration, executionService,modelMapper, environment);
	}

	@Test
	void getAllRunningDevices(){

		when(deviceRepository.findByExecutionIdAndExecutionStatus(1L, ExecutionStatus.STARTED))
			.thenReturn(devices);

		assertNotNull(deviceService.getAllRunningDevices(1L));
	}

	@Test
	void updateDeviceStatusStopped(){
		Execution startExecution = new Execution();
		startExecution.setId(1L);

		Execution stopExecution = new Execution();
		stopExecution.setId(1L);
		stopExecution.setExecutionStatus(ExecutionStatus.STOPPED);

		Device device = new Device("a", startExecution);
		when(deviceRepository.save(device))
			.thenReturn(new Device("a", stopExecution));
		List<Device> devices = deviceService.updateAllDeviceStatusStopped(
			List.of(device));

		devices.forEach(d->assertEquals(d.getExecutionStatus(), ExecutionStatus.STOPPED));
	}
}
