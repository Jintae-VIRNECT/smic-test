package com.virnect.smic.common.config.support;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.common.launch.JobLauncher;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleJobLauncher implements JobLauncher {

	private final ExecutionRepository executionRepository;

	private final DeviceRepository deviceRepository;

	private ExecutionMode execMode ;

	public void presetStatus(){
		setStatusUnknownIfNotProperlyStoppedExecution();
		setStatusUnknownIfNotProperlyStoppedDevices();
	}

	private void setStatusUnknownIfNotProperlyStoppedExecution(){
		List<Execution> stillStartedStatusExecutions = getExecutionsWithStartedStatus();
		stillStartedStatusExecutions.forEach(execution -> updateExecutionStatusUnknown(execution));
	}

	private void setStatusUnknownIfNotProperlyStoppedDevices() {
		List<Device> stillStartedStatusDevices = getDevicesWithStartedStatus();
		stillStartedStatusDevices.forEach(device->updateDeviceStatusUnknown(device));
	}

	private List<Execution> getExecutionsWithStartedStatus(){
		return executionRepository.findByExecutionStatus(ExecutionStatus.STARTED);
	}

	private List<Device> getDevicesWithStartedStatus(){
		return deviceRepository.findByExecutionStatus(ExecutionStatus.STARTED);
	}

	@Transactional
	void updateExecutionStatusUnknown(Execution execution){
		execution.setExecutionStatus(ExecutionStatus.UNKNOWN);
		executionRepository.save(execution);
	}

	@Transactional
	void updateDeviceStatusUnknown(Device device){
		device.setExecutionStatus(ExecutionStatus.UNKNOWN);
		deviceRepository.save(device);
	}

	@Override
	public void run(ExecutionMode mode) {
		this.execMode = mode;
		log.info("{} job starts", mode );
	}

	@PreDestroy
	public void destroy(){
		log.info("{} job ends", execMode );
	}

}
