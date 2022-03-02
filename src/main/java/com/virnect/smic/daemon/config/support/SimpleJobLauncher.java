package com.virnect.smic.daemon.config.support;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.JobExecutionRepository;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.daemon.launch.JobLauncher;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleJobLauncher implements JobLauncher {
	private final JobExecutionRepository jobExecutionRepository;

	@Override
	public JobExecution run() {
		final JobExecution jobExecution = jobExecutionRepository.save(new JobExecution());
		return jobExecution;
	}

}
