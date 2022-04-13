package com.virnect.smic.server.service.application;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.smic.server.data.dao.ExecutionRepository;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;

@RequiredArgsConstructor
@Service
public class ExecutionService {

	private final ExecutionRepository executionRepository;
	private final DaemonConfiguration daemonConfiguration;

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
