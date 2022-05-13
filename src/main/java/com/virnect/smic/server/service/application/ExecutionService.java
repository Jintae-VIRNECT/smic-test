package com.virnect.smic.server.service.application;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.util.PagingUtils;
import com.virnect.smic.server.data.dao.DeviceRepository;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dto.response.ExecutionListResponse;
import com.virnect.smic.server.data.dto.response.PageMetadataResponse;
import com.virnect.smic.server.data.dto.response.SearchExecutionResource;
import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.DuplicatedRunningDeviceException;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;

@Slf4j
@Service
public class ExecutionService extends BaseService {
	private final ExecutionRepository executionRepository;
	private final DeviceRepository deviceRepository;
	private final ModelMapper modelMapper;

	public ExecutionService(
		DeviceRepository deviceRepository,
		ExecutionRepository executionRepository,
		DaemonConfiguration daemonConfiguration,
		ModelMapper modelMapper
	) {
		super(deviceRepository, executionRepository, daemonConfiguration);
		this.executionRepository = executionRepository;
		this.deviceRepository = deviceRepository;
		this.modelMapper = modelMapper;
	}

	public ExecutionResource getStartExecutionResult(String macAddress){

		Optional<Execution> latestExecution = getLatestExecutionInfo();

		if(checkLatestExecutionStatusNotStarted(latestExecution)){
			Execution registeredExecution = registerExecution();
			startDaemon(registeredExecution.getId());

			return getStartExecutionResource(macAddress, registeredExecution);
		}
		// 이미 execution이 존재하지만 STARTED 상태인 경우
		else{
			Execution runningExecution = latestExecution.get();
			Optional<Device> runningDevice = getRunningDeviceInfo(runningExecution, macAddress);

			if(runningDevice.isPresent()){
				ExecutionResource response = createExecutionResource(runningExecution, List.of(runningDevice.get()));
				throw new DuplicatedRunningDeviceException(response);
			}else{
				ExecutionResource response = getStartExecutionResource(macAddress, runningExecution);
				throw new DuplicatedRunningExecutionException(response);
			}

		}
	}

	private ExecutionResource getStartExecutionResource(String macAddress, Execution execution) {
		if(macAddress.equalsIgnoreCase("all"))
			return createExecutionResource(execution, Collections.EMPTY_LIST);
		else
			return createExecutionResource(execution, List.of(registerDevice(macAddress, execution))
			);
	}

	private Optional<Device> getRunningDeviceInfo(Execution execution,String macAddress) {
		return deviceRepository.findByExecutionIdAndMacAddressAndExecutionStatus(
			execution.getId(), macAddress, ExecutionStatus.STARTED);
	}

	private Optional<Execution> getLatestExecutionInfo(){
		return executionRepository.findFirstByOrderByCreatedDateDesc();
	}

	private  Boolean checkLatestExecutionStatusNotStarted(Optional<Execution> optionalExecution){
		return !optionalExecution.isPresent()
			|| !optionalExecution.get().getExecutionStatus().equals(ExecutionStatus.STARTED);
	}

	@Transactional
	Execution registerExecution(){
		return executionRepository.save(new Execution());
	}

	@Transactional
	Device registerDevice(String macAddress, Execution execution){
		return deviceRepository.save(new Device(macAddress, execution));
	}

	public ExecutionResource getStopExecutionResult (long executionId, long deviceId) {

		Execution execution = getRunningExecutionInfo(executionId);

		Device device = getRunningDeviceInfo(deviceId);

		updateDeviceStatus(device, ExecutionStatus.STOPPED);

		List<Device> runningDevices = getAllRunningDevices(executionId);
		if(runningDevices.size()==0){
			stopDaemon();
			execution = updateExecution(execution, ExecutionStatus.STOPPED);
		}

		return createExecutionResource(execution, List.of(device));

	}

	public Execution getCurrentExecution(){
		Optional<Execution> optExecution = executionRepository.findFirstByOrderByCreatedDateDesc();
		return optExecution.orElseThrow();
	}

	public ExecutionListResponse getExecutionList(Pageable pageable) {
		Page<Execution> all = executionRepository.findAll(pageable);
		List<SearchExecutionResource> content = all.getContent().stream()
			.map(o->modelMapper.map(o, SearchExecutionResource.class)).collect(Collectors.toList());

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
			true,
			pageable,
			all.getNumberOfElements(),
			all.getTotalPages(),
			all.getTotalElements(),
			all.isLast()
		);
		return new ExecutionListResponse(content, pageMeta);
	}

}
