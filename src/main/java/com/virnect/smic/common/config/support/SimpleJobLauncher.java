package com.virnect.smic.common.config.support;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import com.virnect.smic.common.data.dao.JobExecutionRepository;
import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.launch.JobLauncher;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleJobLauncher implements JobLauncher {
	private final JobExecutionRepository jobExecutionRepository;
	private final Environment env;

	@Override
	public JobExecution run() {
		final JobExecution jobExecution = jobExecutionRepository.save(new JobExecution(getExecutionMode()));
		return jobExecution;
	}

	@Override
	public ExecutionMode getExecutionMode() {
		if(env.getProperty("server.daemon").equalsIgnoreCase("true")){
			return ExecutionMode.DAEMON;
		}else{
			return ExecutionMode.SERVER;
		}
	}

}
