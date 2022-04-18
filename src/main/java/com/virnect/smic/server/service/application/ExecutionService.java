package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.smic.common.util.PagingUtils;
import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.dto.response.ExecutionListResponse;
import com.virnect.smic.server.data.dto.response.PageMetadataResponse;
import com.virnect.smic.server.data.dto.response.SearchExecutionResource;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;

@RequiredArgsConstructor
@Service
public class ExecutionService {

	private final ExecutionRepository executionRepository;
	private final DaemonConfiguration daemonConfiguration;
	private final ModelMapper modelMapper;

	@Transactional
	public Execution getStartExecutionResult(){
		Optional<Execution> optExec = executionRepository.findFirstByOrderByCreatedDateDesc();
		//if(daemonConfiguration.getRunningExecutionId()<0){
		if(!optExec.isPresent() || !optExec.get().getExecutionStatus().equals(ExecutionStatus.STARTED)){
			Execution execution = executionRepository.save(new Execution());
			daemonConfiguration.launchTaskExecutor(execution.getId());
			return execution;
		}else{
			DuplicatedRunningExecutionException duplicatedException =
				new DuplicatedRunningExecutionException(daemonConfiguration.getRunningExecutionId());
			throw duplicatedException;
		}

	}

	@Transactional
	public Execution getStopExecutionResult(long id) {

		Optional<Execution> optExecution = executionRepository.findFirstByOrderByCreatedDateDesc();//executionRepository.findById(id);
		Execution execution = optExecution.orElseThrow(NoSuchExecutionException::new);
		if (!execution.getExecutionStatus().equals(ExecutionStatus.STARTED)) {
			throw new NoRunningExecutionException();
		} else if (!execution.getId().equals(id)) {
			throw new NoSuchExecutionException();
		}
		execution.setExecutionStatus(ExecutionStatus.STOPPED);

		daemonConfiguration.stopTaskExecutor();

		return executionRepository.save(execution);
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
	void setExecutionStatusAbandoned(){
		long id = daemonConfiguration.getRunningExecutionId();
		Optional<Execution> optExecution = executionRepository.findById(id);
		optExecution.ifPresent(o->{
			o.setExecutionStatus(ExecutionStatus.ABANDONED);
			executionRepository.save(o);
		});
	}

}
