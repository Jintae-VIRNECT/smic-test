package com.virnect.smic.common.service.tasklet;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteTasklet implements Callable {

	private final String tagId;
	private final OpcUaClient client;
	private final Object data;

	public WriteTasklet(String tagId, OpcUaClient client, Object data) {
		this.tagId = tagId;
		this.client = client;
		this.data = data;
	}

	@Override
	public Object call() throws Exception {

		NodeId nodeId  = new NodeId(2, tagId);
		Variant value = new Variant(data);
		DataValue dataValue = new DataValue(value, null, null);
		CompletableFuture<StatusCode> future = client.writeValue(nodeId, dataValue);
		StatusCode status = future.get();

		log.info("Wrote '{}' to nodeId={}, result={}", value, nodeId, status);
		return null;
	}
}
