package com.virnect.smic.daemon.config.support;

import java.util.List;
import java.util.concurrent.Callable;

import com.virnect.smic.common.config.annotation.OpcUaConnection;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.dto.TagDto;
import com.virnect.smic.common.service.tasklet.ReadTasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
@RequiredArgsConstructor
public class SchedulingTaskLauncher {

    private OpcUaClient client;
	private long executionId = -1l;

	@Autowired
    private ReadTasklet tasklet;

    @OpcUaConnection
    public void run(OpcUaClient client) {
        setClient(client);
    }
    
    @Async
	@Scheduled(fixedDelay = 500, initialDelay = 5000)
	void runScheduledFixedDelay(){
		if(getClient()!= null )
			tasklet.readAndPublishAsync(getClient(), true, null);
	}
}
