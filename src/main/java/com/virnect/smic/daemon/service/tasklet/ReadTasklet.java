package com.virnect.smic.daemon.service.tasklet;

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
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.virnect.smic.common.data.domain.ExecutionStatus;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.mq.ProducerManager;
import com.virnect.smic.daemon.mq.rabbitmq.RabbitMqProducerManager;

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
	private final ProducerManager producerManager;// = new RabbitMqProducerManager(); //new KafkaProducerManager();
	
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
			log.error("*******************  exception occurred at " +  tag.toString());
			throw new IllegalStateException();
		} finally{
			nodeIdHolder.remove();
			valueHolder.remove();
		}
	}

	public ConcurrentHashMap<String, String> readAndPublishAsync(List<Tag> tags, OpcUaClient client, boolean isPub){
		counter.incrementAndGet();

		Queue<NodeId> nodes = getNodeIds(tags);

		CompletableFuture<List<DataValue>> cfData 
		= client.readValues(0, TimestampsToReturn.Both, nodes.stream().collect(Collectors.toCollection(ArrayList::new)));

		List<DataValue> dataValues= cfData.join();

		ConcurrentHashMap<String, String> result  = getResultMap(nodes, Collections.synchronizedList(dataValues));

		if(isPub){
			publishAndLogAsync(result);
		} else {
			logResult(result);
		}
		
		return result;
    }

	private Queue<NodeId> getNodeIds(List<Tag> tags){
		Queue<NodeId> nodeIds = new ConcurrentLinkedQueue<NodeId>();
		tags.parallelStream()
			.map(tag->tag.getNodeId())
			.forEach(
				nodeId -> nodeIds.add(new NodeId(2, nodeId))
			);

		return nodeIds;
	}

	private ConcurrentHashMap<String, String> getResultMap(Queue<NodeId> nodes, List<DataValue> dataValues){
		ConcurrentHashMap<String, String> result = new ConcurrentHashMap<>();
		dataValues.parallelStream().forEach(
			value-> {
				String nodeId = nodes.poll().getIdentifier().toString();
				String dataValue = "";
				if(value.getValue().getValue()!= null){
					dataValue =  value.getValue().getValue().toString();
				}
				result.put(nodeId, dataValue);	
			}
		);

		return result;
	}

	private void publishAndLogAsync(ConcurrentHashMap<String, String> result){
		result.entrySet().parallelStream().forEach(item->{
			ExecutionStatus status = ExecutionStatus.UNKNOWN;
			try {
				status = producerManager.runProducer(1, item.getKey().replaceAll(" ", ""), item.getValue().toString());
				
			} catch (IOException e) {
				e.printStackTrace();
				status = ExecutionStatus.FAILED;
			} finally {
				log.info("{} {} {} {}", counter.get(), item.getKey().replaceAll(" ", ""), item.getValue().toString(), status);
			}
		});
		
	}

	private void logResult(ConcurrentHashMap<String, String> result){
		result.entrySet().parallelStream().forEach(item->{
				log.info("{} {} {} {}", counter.get(), item.getKey().replaceAll(" ", ""), item.getValue().toString(), "READONLY");
		});
	}
	
}
