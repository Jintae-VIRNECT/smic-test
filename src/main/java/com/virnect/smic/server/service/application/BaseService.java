package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.NoRunningDeviceException;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchDeviceException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;

@Service
@RequiredArgsConstructor
public class BaseService {

	private final DeviceRepository deviceRepository;

	private final ExecutionRepository executionRepository;

	private final DaemonConfiguration daemonConfiguration;

	Execution getRunningExecutionInfo(long executionId){
		Execution execution = executionRepository.findById(executionId)
			.orElseThrow(NoSuchExecutionException::new);
		if (!execution.getExecutionStatus().equals(ExecutionStatus.STARTED)) {
			throw new NoRunningExecutionException();
		}
		return execution;
	}

	public Device getDeviceInfo(long deviceId){
		return deviceRepository.findById(deviceId)
			.orElseThrow(NoSuchDeviceException::new);
	}

	Device getRunningDeviceInfo(long deviceId){
		Device device = getDeviceInfo(deviceId);

		if(!device.getExecutionStatus().equals(ExecutionStatus.STARTED)){
			throw new NoRunningDeviceException();
		}
		return device;
	}

	List<Device> getAllRunningDevices(long executionId) {
		return deviceRepository.findByExecutionIdAndExecutionStatus(executionId, ExecutionStatus.STARTED);
	}

	List<Device> getDevicesInExecution(long executionId){
		return deviceRepository.findAllByExecutionId(executionId);
	}

	@Transactional
	void updateDeviceStatus(Device device, ExecutionStatus status){
		device.setExecutionStatus(status);
		deviceRepository.save(device);
	}

	@Transactional
	Execution updateExecution(Execution execution, ExecutionStatus status){
		execution.setExecutionStatus(status);
		return executionRepository.save(execution);
	}

	public Execution getExecutionInfo(long id){
		Optional<Execution> optExecution = executionRepository.findById(id);
		Execution execution = optExecution.orElseThrow(NoSuchElementException::new);
		return execution;
	}

	ExecutionResource createExecutionResource(Execution execution, List<Device> devices) {

		return ExecutionResource.builder()
			.executionId(execution.getId())
			.executionStatus(execution.getExecutionStatus())
			.executionCreatedDate(execution.getCreatedDate())
			.executionUpdatedDate(execution.getUpdatedDate())
			.devices(devices)
			// .deviceId(device.getId())
			// .deviceStatus(device.getExecutionStatus())
			// .macAddress(device.getMacAddress())
			// .deviceCreatedDate(device.getCreatedDate())
			// .deviceUpdatedDate(device.getUpdatedDate())
			.build();
	}

	void stopDaemon(){
		daemonConfiguration.stopTaskExecutor();
	}

	void startDaemon(long executionId){
		daemonConfiguration.launchTaskExecutor(executionId);
	}

	@PreDestroy
	void setStatusAbandoned(){
		setExecutionStatusAbandoned();
		setAllDeviceStatusAbandoned();
	}

	private void setExecutionStatusAbandoned(){
		long id = daemonConfiguration.getRunningExecutionId();
		executionRepository.findById(id).ifPresent(o->{
			updateExecution(o, ExecutionStatus.ABANDONED);
		});
	}

	private void setAllDeviceStatusAbandoned(){
		long id = daemonConfiguration.getRunningExecutionId();
		List<Device> runningDevices
			= deviceRepository.findByExecutionIdAndExecutionStatus(id, ExecutionStatus.STARTED);
		runningDevices.forEach(device-> updateDeviceStatus(device, ExecutionStatus.ABANDONED));
	}
}
