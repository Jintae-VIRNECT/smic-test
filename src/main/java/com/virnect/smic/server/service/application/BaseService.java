package com.virnect.smic.server.service.application;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dto.response.ExecutionResource;

@Service
@RequiredArgsConstructor
public class BaseService {

	private final DaemonConfiguration daemonConfiguration;

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

}
