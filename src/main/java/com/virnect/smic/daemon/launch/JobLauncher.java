package com.virnect.smic.daemon.launch;

import com.virnect.smic.common.data.domain.JobExecution;

public interface JobLauncher {
	public JobExecution run();
}
