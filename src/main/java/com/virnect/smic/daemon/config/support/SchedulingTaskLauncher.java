package com.virnect.smic.daemon.config.support;

import java.util.List;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.config.annotation.OpcUaConnection;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final ReadTasklet tasklet;

	@Autowired
	@Qualifier("tagList")
    private List<Tag> tags;

    @OpcUaConnection
    public void run(OpcUaClient client) {
        setClient(client);
    }
    
    @Async
	@Scheduled(fixedDelay = 1000, initialDelay = 5000)
	void runScheduledFixedDelay(){
		if(getClient()!= null)
			tasklet.readAndPublishAsync(tags, getClient(), true);
	}
}
