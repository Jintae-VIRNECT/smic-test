package com.virnect.smic.common.service.tasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.virnect.smic.common.config.annotation.TimeLogTrace;
import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.dto.TagDto;
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

	@Autowired
	@Qualifier("tagList")
	private List<TagDto> tags;

	@Autowired
	@Qualifier("queueNameMap")
	private ConcurrentHashMap<String, String> queueNameMap;

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
	public ConcurrentHashMap<String, String> readAndPublishAsync(OpcUaClient client, boolean isPub, String uuid){
		counter.incrementAndGet();

		ConcurrentLinkedQueue<NodeId> nodes = getNodeIds();

		CompletableFuture<List<DataValue>> cfData 
		= client.readValues(0, TimestampsToReturn.Both, nodes.stream().collect(Collectors.toCollection(ArrayList::new)));

		List<DataValue> dataValues= cfData.join();

		ConcurrentHashMap<String, String> result  = getResultMap(Collections.synchronizedList(dataValues), nodes);

		if(isPub){
			publishAndLogAsync(result, uuid);
		} else {
			logResult(result, uuid);
		}
		
		return result;
    }

	private ConcurrentLinkedQueue<NodeId> getNodeIds(){
		ConcurrentLinkedQueue<NodeId> nodeIds = new ConcurrentLinkedQueue<NodeId>();
		tags.parallelStream()
			.map(tag->tag.getNodeId())
			.forEach(
				nodeId -> nodeIds.add(new NodeId(2, nodeId))
			);

		return nodeIds;
	}

	private ConcurrentHashMap<String, String> getResultMap(List<DataValue> dataValues
		, ConcurrentLinkedQueue<NodeId> nodes){
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();

		dataValues.parallelStream().forEachOrdered(
			value-> {
				String nodeId = nodes.poll().getIdentifier().toString();
				Optional<String> queueName = queueNameMap.entrySet()
					.parallelStream()
					.filter(e->e.getValue().equals(nodeId))
					.map(e->e.getKey())
					.findFirst();

				if(queueName.isPresent()){
					String dataValue = "";
					if(value.getValue().getValue()!= null){
						dataValue =  value.getValue().getValue().toString();
					}
					result.put(queueName.get(), dataValue);
				}
			}
		);

		return result;
	}

	void publishAndLogAsync(ConcurrentHashMap<String, String> result, String uuid){
		result.entrySet().stream().forEach(item->{
			ExecutionStatus status = ExecutionStatus.UNKNOWN;
			try {
				status = producerManager.runProducer(1, item.getKey(), item.getValue());
				
			} catch (IOException e) {
				e.printStackTrace();
				status = ExecutionStatus.FAILED;
			} finally {
				log.info("[{}] {} {} [{}] {} [{}]"
				, uuid
				, counter.get()
					, queueNameMap.get(item.getKey())
				, item.getValue()
				, status
				, item.getKey());
			}
		});
		
	}

	void logResult(ConcurrentHashMap<String, String> result, String uuid){
		result.entrySet().stream().forEach(item->{
				log.info("[{}] {} {} [{}] {} [{}]"
				, uuid
				, counter.get()
					, queueNameMap.get(item.getKey())
				, item.getValue(), "READONLY"
				, item.getKey());
		});
	}

	@TimeLogTrace
	public ConcurrentHashMap<String, String> readAndPublishAsync(OpcUaClient client,
		boolean isPub, String uuid, List<TagDto> tags) {
		this.setTags(tags);
		return readAndPublishAsync(client, isPub, uuid);
	}
}
