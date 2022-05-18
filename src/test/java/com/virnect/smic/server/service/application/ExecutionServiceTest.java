package com.virnect.smic.server.service.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.exception.DuplicatedRunningDeviceException;
import com.virnect.smic.server.data.error.exception.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoRunningDeviceException;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchDeviceException;
import com.virnect.smic.server.data.error.exception.NoSuchExecutionException;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ExecutionServiceTest {

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	DaemonConfiguration daemonConfiguration;

	@Autowired
	ModelMapper modelMapper;

	@Autowired
	ExecutionRepository executionRepository;

	@Autowired
	DeviceService deviceService;

	static ExecutionService executionService;

	@BeforeAll
	static void setup(@Autowired DeviceRepository deviceRepository
		, @Autowired ExecutionRepository executionRepository
		, @Autowired DaemonConfiguration daemonConfiguration
		, @Autowired ModelMapper modelMapper
		, @Autowired DeviceService deviceService){
		executionService = new ExecutionService(
			deviceRepository, executionRepository, daemonConfiguration, modelMapper,deviceService);
	}
	@Nested
	class StartExecutionTest{

		@Test
		@Transactional
		void get_StartExecutionResult_when_running_execution_exists(){

			// given
			Execution executionStarted = new Execution();
			executionStarted.setId(222222L);
			executionRepository.save(executionStarted);

			// when, then
			DuplicatedRunningExecutionException execption = assertThrows(DuplicatedRunningExecutionException.class, ()->{
				executionService.getStartExecutionResult("temp123");
			});
			List<Device> devices = execption.getExecutionResource().getDevices();
			assertTrue(devices.size()==1);
			devices.forEach(d->assertNotNull(d.getId()));
		}

		@Test
		@Transactional
		void get_StartExecutionResult_when_no_running_execution_exists(){

			// given
			Execution executionStopped = new Execution();
			executionStopped.setId(222222L);
			executionStopped.setExecutionStatus(ExecutionStatus.STOPPED);
			executionRepository.save(executionStopped);

			// when
			ExecutionResource result = executionService.getStartExecutionResult("temp123");

			// then
			assertNotEquals(result.getExecutionId(), executionStopped.getId());
			assertEquals(result.getExecutionStatus(), ExecutionStatus.STARTED);
			assertEquals(result.getExecutionId(), daemonConfiguration.getRunningExecutionId());
		}

	}

	@Nested
	class StartExecutionTest2 {

		Execution executionStarted;
		Long executionId;

		@BeforeEach
		@Transactional
		void setup() {

			Execution execution = new Execution();
			executionStarted =executionRepository.save(execution);
			executionId = executionStarted.getId();
		}

		@Test
		@Transactional
		void get_StartExecutionResult_when_both_running_execution_and_device_exist() {

			// given
			Device device = new Device("temp123", executionStarted);
			deviceRepository.save(device);

			// when, then
			assertThrows(DuplicatedRunningDeviceException.class, () -> {
				executionService.getStartExecutionResult("temp123");
			});
		}
	}

	@Nested
	class StopExecutionTest{
		Execution runningExecution;
		Execution stoppedExecution;
		Device runningDevice;

		@Test
		@Transactional
		void get_StopExecutionResult_when_running_execution_and_several_running_devices_exist(){
			// given
			Execution execution = new Execution();
			execution.setId(222222L);
			runningExecution = executionRepository.save(execution);
			Device device = new Device("temp123", runningExecution);
			runningDevice = deviceRepository.save(device);
			deviceRepository.save(new Device("temp456", runningExecution));

			// when
			ExecutionResource result
				= executionService.getStopExecutionResult(runningExecution.getId(),runningDevice.getId());

			// then
			assertAll(
				()-> assertNotEquals(result.getExecutionStatus(), ExecutionStatus.STOPPED)
				, ()-> result.getDevices().forEach(d-> {
					if(d.getId().equals(runningDevice.getId()))
						assertEquals(d.getExecutionStatus(), ExecutionStatus.STOPPED);})
			);

		}

		@Test
		@Transactional
		void get_StopExecutionResult_when_running_execution_and_only_one_running_device_exist(){
			// given
			Execution execution = new Execution();
			execution.setId(222222L);
			runningExecution = executionRepository.save(execution);
			Device stoppedDevice = new Device("temp456", runningExecution);
			stoppedDevice.setExecutionStatus(ExecutionStatus.STOPPED);
			deviceRepository.save(stoppedDevice);
			runningDevice = deviceRepository.save(new Device("temp123", runningExecution));

			// when
			ExecutionResource result
				= executionService.getStopExecutionResult(runningExecution.getId(),runningDevice.getId());

			// then
			assertAll(
				()-> assertEquals(result.getExecutionStatus(), ExecutionStatus.STOPPED)
				, ()-> result.getDevices().forEach(d-> {
					if(d.getId().equals(runningDevice.getId()))
						assertEquals(d.getExecutionStatus(), ExecutionStatus.STOPPED);})
			);

		}

		@Test
		@Transactional
		void get_StopExecutionResult_when_invalid_device_info_is_given(){
			// given
			Execution execution = new Execution();
			execution.setId(222222L);
			runningExecution = executionRepository.save(execution);
			Device stoppedDevice = new Device("temp456", runningExecution);
			stoppedDevice.setExecutionStatus(ExecutionStatus.STOPPED);
			Device device = deviceRepository.save(stoppedDevice);

			// when, then
			assertAll(
				()-> assertThrows(NoSuchDeviceException.class
					,()->executionService.getStopExecutionResult(runningExecution.getId(),device.getId()+1000L))
				, ()-> assertThrows(NoRunningDeviceException.class
					,()->executionService.getStopExecutionResult(runningExecution.getId(),device.getId()))
			);

		}

		@Test
		@Transactional
		void get_StopExecutionResult_when_invalid_execution_info_given(){
			// given
			Execution execution = new Execution();
			execution.setId(222222L);
			execution.setExecutionStatus(ExecutionStatus.STOPPED);
			stoppedExecution = executionRepository.save(execution);

			// when, then
			assertAll(
				()-> assertThrows(NoSuchExecutionException.class
					,()->executionService.getStopExecutionResult(stoppedExecution.getId()+1000L,99999L))
				, ()-> assertThrows(NoRunningExecutionException.class
					,()->executionService.getStopExecutionResult(stoppedExecution.getId(),99999L))
			);

		}
	}
}