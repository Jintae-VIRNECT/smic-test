package com.virnect.smic.daemon.service;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;
import com.virnect.smic.daemon.stream.mq.topic.ProducerManager;

@Slf4j
//@Service
@RequiredArgsConstructor
@Getter @Setter
public class ReadServiceCallable implements Callable {

	private final ReadTasklet tasklet;
	private final List<Tag> tags;
	private final OpcUaClient client;
	private final ProducerManager producer;

	@Override
	public Object call() throws Exception {
		String result = "";
		for(Tag tag : tags)
		{
			tasklet.setTag(tag);
			tasklet.setNodeId(tag.getNodeId());
			tasklet.setClient(client);
			tasklet.setProducerManager(producer);
			result =  tasklet.run();
		}
		return result;
	}
}
