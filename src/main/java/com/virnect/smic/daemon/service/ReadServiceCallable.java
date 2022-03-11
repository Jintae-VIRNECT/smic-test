package com.virnect.smic.daemon.service;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;

@Slf4j
@RequiredArgsConstructor
@Component
@Getter @Setter
public class ReadServiceCallable {//implements Callable {

	private final ReadTasklet tasklet;
	private  List<Tag> tags;
	private  OpcUaClient client;

	//@Override
	public Object call() throws Exception {
		String result = "";
		for(Tag tag : tags)
		{
			tasklet.setTag(tag);
			tasklet.initiate(tag.getNodeId(), client);
			result =  tasklet.readAndPublish();
		}
		return result;
	}
}
