package com.virnect.smic.server.service.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

	@Mock
	 DeviceRepository deviceRepository;

	@Mock
	DaemonConfiguration daemonConfiguration;

	@Mock
	ModelMapper modelMapper;

	@Mock
	 ExecutionRepository executionRepository;

	@Mock
	DeviceService deviceService;

	ExecutionService executionService;

	@BeforeEach
	 void setExecutionService(){
		executionService = new ExecutionService(
			deviceRepository, executionRepository, daemonConfiguration, modelMapper,deviceService);
	}

	@Test
	void getStartExecutionResult(){
		// given
		Execution executionStopped = new Execution();
		executionStopped.setId(2l);
		List<Device> devices = new ArrayList<>();
		Execution execution = new Execution();
		execution.setId(1l);
		Device device = new Device("hahaha", execution);
		devices.add(device);
		executionStopped.setDevices(devices);
		executionStopped.setExecutionStatus(ExecutionStatus.STOPPED);

		Execution savedExecution = new Execution();
		savedExecution.setId(2l);

		Execution executionStarted = new Execution();
		executionStarted.setExecutionStatus(ExecutionStatus.STARTED);

		// when
		when(executionRepository.findFirstByOrderByCreatedDateDesc())
		 	.thenReturn(Optional.of(executionStopped))
			.thenReturn(Optional.of(executionStarted));

		when(executionRepository.save(any())).thenReturn(savedExecution);

		// then
		assertNotEquals(executionService.getStartExecutionResult("haaahah"), executionStopped);
		assertThrows(DuplicatedRunningExecutionException.class, ()->{
			executionService.getStartExecutionResult("haaahah");
		});
	}

	@Test
	void registerDevice(){
		Execution execution = new Execution();
		execution.setId(2l);
		Device device = new Device("fkfkfk",execution);
		when(deviceRepository.findByExecutionIdAndMacAddress( anyLong(),anyString()))
			.thenReturn(Optional.of(device));
		Execution exec = new Execution();
		exec.setId(22l);
		executionService.registerDevice("klkl",exec );
	}

}