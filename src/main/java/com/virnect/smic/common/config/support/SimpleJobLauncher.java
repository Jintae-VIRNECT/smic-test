package com.virnect.smic.common.config.support;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;

import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.launch.JobLauncher;

@Slf4j
@RequiredArgsConstructor
@Service
public class SimpleJobLauncher implements JobLauncher {


	private ExecutionMode execMode ;

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
