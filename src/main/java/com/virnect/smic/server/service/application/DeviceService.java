package com.virnect.smic.server.service.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.NoRunningExecutionException;

@Service
public class DeviceService extends BaseService {

	private final ExecutionRepository executionRepository;

	public DeviceService(
		DeviceRepository deviceRepository,
		ExecutionRepository executionRepository,
		DaemonConfiguration daemonConfiguration
	) {
		super(deviceRepository, executionRepository, daemonConfiguration);
		this.executionRepository = executionRepository;
	}

	public ExecutionResource releaseAllDevices(){
		Execution execution = getCurrentlyRunningExecution();
		updateAllDeviceStatusStopped(getAllRunningDevices(execution.getId()));
		stopDaemon();
		updateExecution(execution, ExecutionStatus.STOPPED);
		return createExecutionResource(getExecutionInfo(execution.getId()), getDevicesInExecution(execution.getId()));
	}

	List<Device> updateAllDeviceStatusStopped(List<Device> devices){
		devices.forEach(device -> updateDeviceStatus(device, ExecutionStatus.STOPPED));
		return devices;
	}

	private Execution getCurrentlyRunningExecution(){
		Execution execution
			= executionRepository.findFirstByOrderByCreatedDateDesc().orElseThrow(NoRunningExecutionException::new);

		if(execution.getExecutionStatus().equals(ExecutionStatus.STARTED)){
			return execution;
		}else{
			throw new NoRunningExecutionException();
		}
	}

	public ExecutionResource getDevicesWithExecution(long executionId){
		return createExecutionResource(getExecutionInfo(executionId), getDevicesInExecution(executionId));
	}
}
