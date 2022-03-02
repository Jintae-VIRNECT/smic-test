package com.virnect.smic.daemon.config.support;

import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.data.domain.TaskExecution;

public interface TaskLauncher {
	public TaskExecution run(JobExecution jobExecution);
}
