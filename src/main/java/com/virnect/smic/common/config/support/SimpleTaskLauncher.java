package com.virnect.smic.common.config.support;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.config.connection.ConnectionPoolImpl;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.service.tasklet.ReadTasklet;
import com.virnect.smic.common.util.OpcUaServerConfigUtil;

@Slf4j
@Getter @Setter
@Component
public class SimpleTaskLauncher implements DisposableBean {

	private final ReadTasklet tasklet;

	// @Autowired
	// @Qualifier("modelList")
    // private List<ModelLine> modelLines;

	@Autowired
	@Qualifier("tagList")
    private List<Tag> tags;

	private OpcUaClient client;

	public SimpleTaskLauncher( ReadTasklet tasklet){
		this.tasklet = tasklet;
	}
	
	private  ConnectionPoolImpl pool;

	//@OpcUaConnection
	public void run(OpcUaClient client) {

		int numOfCores = Runtime.getRuntime().availableProcessors();
		log.debug("************** number of cores: "+ numOfCores);
		log.info("parallelism: "+ ForkJoinPool.getCommonPoolParallelism());
		// System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "100");
		setClient(client);
		try {
			OpcUaServerConfigUtil.getServerConfigInfo(client);
		} catch (UaException e) {
			e.printStackTrace();
		}
	    // runOneTimeWithTaskExec(client);
		
	}

	private void runOneTimeWithTaskExec(OpcUaClient client){
		tasklet.readAndPublishAsync(tags, getClient(), true, null);
	}

	@Override
	public void destroy() throws Exception {
		System.out.println(
			"Callback triggered at SimpleTaskLauncher - bean destroy method called.");
	    // something
		System.out.println(
			"Callback triggered at SimpleTaskLauncher - bean destroy method ends");
	}

}
