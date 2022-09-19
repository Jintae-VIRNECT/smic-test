package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.exception.NoRunningDeviceException;
import com.virnect.smic.server.data.error.exception.NoSuchDeviceException;

@Service
public class DeviceService extends BaseService {

	private final DeviceRepository deviceRepository;

	private final DaemonConfiguration daemonConfiguration;

	private final ExecutionService executionService;

	private final ModelMapper modelMapper;

	private final Environment env;

	public DeviceService(
		DeviceRepository deviceRepository,
		DaemonConfiguration daemonConfiguration,
		ExecutionService executionService,
		ModelMapper modelMapper,
		Environment env
	) {
		super(daemonConfiguration, modelMapper);
		this.deviceRepository = deviceRepository;
		this.daemonConfiguration = daemonConfiguration;
		this.executionService = executionService;
		this.modelMapper = modelMapper;
		this.env = env;
	}

	public ExecutionResource releaseAllDevices(){
		Execution execution = executionService.getCurrentlyRunningExecution();
		updateAllDeviceStatusStopped(getAllRunningDevices(execution.getId()));
		stopDaemon();
		executionService.updateExecution(execution, ExecutionStatus.STOPPED);
		return createExecutionResource(
			executionService.getExecutionInfo(execution.getId()), getDevicesInExecution(execution.getId()));
	}

	List<Device> updateAllDeviceStatusStopped(List<Device> devices){
		devices.forEach(device -> updateDeviceStatus(device, ExecutionStatus.STOPPED));
		return devices;
	}

	public ExecutionResource getDevicesWithExecution(long executionId){
		return createExecutionResource(
			executionService.getExecutionInfo(executionId), getDevicesInExecution(executionId));
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

	Optional<Device> getOptionalRunningDeviceInfo(Execution execution,String macAddress) {
		return deviceRepository.findByExecutionIdAndMacAddressAndExecutionStatus(
			execution.getId(), macAddress, ExecutionStatus.STARTED);
	}

	List<Device> getAllRunningDevices(long executionId) {
		return deviceRepository.findByExecutionIdAndExecutionStatus(executionId, ExecutionStatus.STARTED);
	}

	List<Device> getDevicesInExecution(long executionId){
		return deviceRepository.findAllByExecutionId(executionId);
	}

	@PreDestroy
	void setStatusAbandoned(){
		setAllDeviceStatusAbandoned();
	}

	@Transactional
	void updateDeviceStatus(Device device, ExecutionStatus status){
		device.setExecutionStatus(status);
		deviceRepository.save(device);
	}

	void setAllDeviceStatusAbandoned(){
		long id = daemonConfiguration.getRunningExecutionId();
		List<Device> runningDevices
			= deviceRepository.findByExecutionIdAndExecutionStatus(id, ExecutionStatus.STARTED);
		runningDevices.forEach(device-> updateDeviceStatus(device, ExecutionStatus.ABANDONED));
	}
}
