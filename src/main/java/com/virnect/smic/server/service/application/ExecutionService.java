package com.virnect.smic.server.service.application;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.smic.common.data.dao.ExecutionRepository;
import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.daemon.config.DaemonConfiguration;

@RequiredArgsConstructor
@Service
public class ExecutionService {

	private final ExecutionRepository executionRepository;
	private final DaemonConfiguration daemonConfiguration;

	@Transactional
	public Execution getStartExecutionResult(){
		daemonConfiguration.initialize();
		return executionRepository.save(new Execution());
	}

	@Transactional
	public Execution getStopExecutionResult(long id){
		daemonConfiguration.stopTaskExecutor();

		Optional<Execution> optExecution = executionRepository.findById(id);
		Execution execution = optExecution.orElseThrow(NoSuchElementException::new);
		execution.setExecutionStatus(ExecutionStatus.STOPPED);

		return executionRepository.save(execution);
	}

	public Execution getSearchExecutionResult(long id){
		Optional<Execution> optExecution = executionRepository.findById(id);
		Execution execution = optExecution.orElseThrow(NoSuchElementException::new);
		return execution;
	}
}
