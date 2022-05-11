package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.NoRunningDeviceException;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchDeviceException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExecutionService {

	private final ExecutionRepository executionRepository;
	private final DeviceRepository deviceRepository;
	private final DaemonConfiguration daemonConfiguration;
	private final ModelMapper modelMapper;


	public ExecutionResource getStartExecutionResult(String macAddress){

		Optional<Execution> optionalExecution = getLatestExecutionInfo();

		if(checkLatestExecutionStatusNotStarted(optionalExecution)){
			Execution execution = registerExecution();
			Device device = registerDevice(macAddress, execution);

			startDaemon(execution.getId());

			return createExecutionResource(execution, device);
		}
		// 이미 execution이 존재하지만 STARTED 상태인 경우
		else{
			Execution execution = optionalExecution.get();
			if(getRunningDeviceInfo(execution, macAddress).isPresent()){
				throw new DuplicatedRunningExecutionException(execution.getId());
			}else{
				Device device = registerDevice(macAddress, execution);
				return createExecutionResource(execution, device);
			}

		}
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

	private ExecutionResource createExecutionResource(Execution execution, Device device) {
		return ExecutionResource.builder()
			.executionId(execution.getId())
			.executionStatus(execution.getExecutionStatus())
			.executionCreatedDate(execution.getCreatedDate())
			.executionUpdatedDate(execution.getUpdatedDate())
			.deviceId(device.getId())
			.deviceStatus(device.getExecutionStatus())
			.macAddress(device.getMacAddress())
			.deviceCreatedDate(device.getCreatedDate())
			.deviceUpdatedDate(device.getUpdatedDate())
			.build();
	}

	@Transactional
	Device registerDevice(String macAddress, Execution execution){
		return deviceRepository.save(new Device(macAddress, execution));
	}

	public ExecutionResource getStopExecutionResult (long executionId, long deviceId) {

		Execution execution = getRunningExecutionInfo(executionId);

		Device device = getRunningDeviceInfo(deviceId);

		updateDeviceStatus(device, ExecutionStatus.STOPPED);

		List<Device> runningDevices = getAllRunningDevicesInExecution(executionId);
		if(runningDevices.size()==0){
			stopDaemon();
			execution = updateExecution(execution, ExecutionStatus.STOPPED);
		}

		return createExecutionResource(execution, device);

	}

	private Device getRunningDeviceInfo(long deviceId){
		Device device = deviceRepository.findById(deviceId)
				.orElseThrow(NoSuchDeviceException::new);

		if(!device.getExecutionStatus().equals(ExecutionStatus.STARTED)){
			throw new NoRunningDeviceException();
		}
		return device;
	}

	private Execution getRunningExecutionInfo(long executionId){
		Execution execution = executionRepository.findById(executionId)
			.orElseThrow(NoSuchExecutionException::new);
		if (!execution.getExecutionStatus().equals(ExecutionStatus.STARTED)) {
			throw new NoRunningExecutionException();
		}
		return execution;
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

	private List<Device> getAllRunningDevicesInExecution(long executionId){
		return deviceRepository.findByExecutionIdAndExecutionStatus(executionId, ExecutionStatus.STARTED);
	}

	private void stopDaemon(){
		daemonConfiguration.stopTaskExecutor();
	}

	private void startDaemon(long executionId){
		daemonConfiguration.launchTaskExecutor(executionId);
	}

	public Execution getSearchExecutionResult(long id){
		Optional<Execution> optExecution = executionRepository.findById(id);
		Execution execution = optExecution.orElseThrow(NoSuchElementException::new);
		return execution;
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
