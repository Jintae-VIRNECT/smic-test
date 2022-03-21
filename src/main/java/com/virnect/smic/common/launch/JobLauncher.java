package com.virnect.smic.common.launch;

import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.data.domain.JobExecution;


public interface JobLauncher {
	public JobExecution run();
	public ExecutionMode getExecutionMode();
}
