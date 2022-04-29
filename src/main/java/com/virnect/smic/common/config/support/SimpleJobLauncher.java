package com.virnect.smic.common.config.support;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.common.launch.JobLauncher;
import com.virnect.smic.server.data.dao.ExecutionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleJobLauncher implements JobLauncher {

	private final ExecutionRepository executionRepository;

	private ExecutionMode execMode ;

	@Transactional
	public void presetExecutionStatus(){
		Optional<Execution> latestExecution = executionRepository.findFirstByOrderByCreatedDateDesc();

		if( latestExecution.isPresent() &&
			latestExecution.get().getExecutionStatus().equals(ExecutionStatus.STARTED)){
			Execution execution = latestExecution.get();
			execution.setExecutionStatus(ExecutionStatus.UNKNOWN);
			executionRepository.save(execution);
		}
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
