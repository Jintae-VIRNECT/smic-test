package com.virnect.smic.common.service.tasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.virnect.smic.common.config.annotation.TimeLogTrace;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.mq.ProducerManager;

@Slf4j
@Component
@Getter @Setter
@RequiredArgsConstructor
public class ReadTasklet {
	private OpcUaClient client;
	private ThreadLocal<String> nodeIdHolder = new ThreadLocal<>();
	private ThreadLocal<DataValue> valueHolder = new ThreadLocal<>();
	private Tag tag;
	
	private final AtomicInteger counter = new AtomicInteger(0);
	private final ProducerManager producerManager;
	
	public void initiate(String nodeId, OpcUaClient client){
		nodeIdHolder.set(nodeId);
		this.client = client;
	}

	public String readAndPublish(boolean isPub) {
		counter.incrementAndGet();
		try {
			
			valueHolder.set(client.readValue(2147483647, TimestampsToReturn.Both, new NodeId(2, nodeIdHolder.get())).get());
			
			if(valueHolder.get().getValue().getValue() != null) {
				log.debug("{} {} {}", 
					counter.get(),
					nodeIdHolder.get().replaceAll(" ", ""), 
					valueHolder.get().getValue().getValue().toString());
				if(isPub)
					producerManager.runProducer(1, nodeIdHolder.get().replaceAll(" ", ""), valueHolder.get().getValue().getValue().toString());
				return valueHolder.get().getValue().getValue().toString();	
			}else{
				return "";
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error(" exception occurred at " +  tag.toString());
			throw new IllegalStateException();
		} finally{
			nodeIdHolder.remove();
			valueHolder.remove();
		}
	}

	@TimeLogTrace
	public ConcurrentHashMap<String, String> readAndPublishAsync(List<Tag> tags
		, OpcUaClient client, boolean isPub, String uuid){
		counter.incrementAndGet();

		ConcurrentLinkedQueue<NodeId> nodes = getNodeIds(tags);

		CompletableFuture<List<DataValue>> cfData 
		= client.readValues(0, TimestampsToReturn.Both, nodes.stream().collect(Collectors.toCollection(ArrayList::new)));

		List<DataValue> dataValues= cfData.join();

		ConcurrentHashMap<String, String> result  = getResultMap(tags, Collections.synchronizedList(dataValues), nodes);

		if(isPub){
			publishAndLogAsync(result, uuid, tags);
		} else {
			logResult(result, uuid, tags);
		}
		
		return result;
    }

	private ConcurrentLinkedQueue<NodeId> getNodeIds(List<Tag> tags){
		ConcurrentLinkedQueue<NodeId> nodeIds = new ConcurrentLinkedQueue<NodeId>();
		tags.parallelStream()
			.map(tag->tag.getNodeId())
			.forEach(
				nodeId -> nodeIds.add(new NodeId(2, nodeId))
			);

		return nodeIds;
	}

	private ConcurrentHashMap<String, String> getResultMap(List<Tag> tags
		, List<DataValue> dataValues
		, ConcurrentLinkedQueue<NodeId> nodes){
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();

		dataValues.forEach(
			value-> {
				String nodeId = nodes.poll().getIdentifier().toString();
				String queueName = tags.parallelStream().filter(t->t.getNodeId().equals(nodeId)).map(t->t.getQueueName()).findFirst().get();
				String dataValue = "";
				if(value.getValue().getValue()!= null){
					dataValue =  value.getValue().getValue().toString();
				}
				result.put(queueName, dataValue);

			}

		);

		return result;
	}

	private void publishAndLogAsync(ConcurrentHashMap<String, String> result, String uuid, List<Tag> tags){
		result.entrySet().stream().forEach(item->{
			ExecutionStatus status = ExecutionStatus.UNKNOWN;
			try {
				status = producerManager.runProducer(1, item.getKey(), item.getValue().toString());
				
			} catch (IOException e) {
				e.printStackTrace();
				status = ExecutionStatus.FAILED;
			} finally {
				log.info("[{}] {} {} [{}] {} [{}]"
				, uuid
				, counter.get()
				//, nodes.poll().getIdentifier().toString().replaceAll(" ", "")
					, tags.parallelStream().filter(t->t.getQueueName().equals(item.getKey())).map(t->t.getNodeId()).findFirst().get()
				, item.getValue().toString()
				, status
				, item.getKey());
			}
		});
		
	}

	private void logResult(ConcurrentHashMap<String, String> result, String uuid, List<Tag> tags){
		result.entrySet().stream().forEach(item->{
				log.info("[{}] {} {} [{}] {} [{}]"
				, uuid
				, counter.get()
				//, nodes.poll().getIdentifier().toString().replaceAll(" ", "")
					, tags.parallelStream().filter(t->t.getQueueName().equals(item.getKey())).map(t->t.getNodeId()).findFirst().get()
				, item.getValue().toString(), "READONLY"
				, item.getKey());
		});
	}
	
}
