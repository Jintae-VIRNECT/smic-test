package com.virnect.smic.daemon.service;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;

@Slf4j
@RequiredArgsConstructor
@Getter @Setter
public class ReadServiceCallable implements Callable {

	private final ReadTasklet tasklet;
	private final List<Tag> tags;
	private final OpcUaClient client;

	@Override
	public Object call() throws Exception {
		String result = "";
		for(Tag tag : tags)
		{
			tasklet.setTag(tag);
			tasklet.setNodeId(tag.getNodeId());
			tasklet.setClient(client);
			result =  tasklet.run();
		}
		return result;
	}
}
