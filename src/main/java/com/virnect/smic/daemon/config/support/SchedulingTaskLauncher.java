package com.virnect.smic.daemon.config.support;


import com.virnect.smic.common.config.annotation.OpcUaConnection;
import com.virnect.smic.common.service.tasklet.ReadTasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.annotation.Autowired;
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
	private long executionId = -1L;

	@Autowired
    private ReadTasklet tasklet;

    @OpcUaConnection
    public void run(OpcUaClient client) {
        setClient(client);
    }

	@Scheduled(fixedDelay = 500, initialDelay = 5000)
	void runScheduledFixedDelay(){
		if(getClient()!= null )
			tasklet.readAndPublishAsync(getClient(), true, null);
	}
}
