package com.virnect.smic.common.launch;

import com.virnect.smic.common.data.domain.ExecutionMode;

public interface JobLauncher {
	public void run(ExecutionMode mode);
}
