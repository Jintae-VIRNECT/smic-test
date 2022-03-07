package com.virnect.smic.daemon.service.tasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.mq.ProducerManager;
import com.virnect.smic.daemon.mq.kafka.KafkaProducerManager;
import com.virnect.smic.daemon.mq.rabbitmq.RabbitMqProducerManager;

@Slf4j
@RequiredArgsConstructor
@Component
@Getter @Setter
public class ReadTasklet {
	private OpcUaClient client;
	private ThreadLocal<String> nodeIdHolder = new ThreadLocal<>();
	private ThreadLocal<DataValue> valueHolder = new ThreadLocal<>();
	private Tag tag;
	private final ProducerManager producerManager = new RabbitMqProducerManager(); //new KafkaProducerManager();

	public void initiate(String nodeId, OpcUaClient client){
		nodeIdHolder.set(nodeId);
		this.client = client;
	}

	// public void setClient(OpcUaClient client){
	// 	this.client = client;
	// }

	public String readAndPublish() {

		try {

			valueHolder.set(client.readValue(0, TimestampsToReturn.Both, new NodeId(2, nodeIdHolder.get())).get());
			if(valueHolder.get().getValue().getValue() != null) {
				log.debug("{} -> {}", nodeIdHolder.get(), valueHolder.get().getValue().getValue().toString());
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

	public synchronized String readOnly() {

		try {

			valueHolder.set(client.readValue(0, TimestampsToReturn.Both, new NodeId(2, nodeIdHolder.get())).get());
			if(valueHolder.get().getValue().getValue() != null) {
				log.debug("{} -> {}", nodeIdHolder.get(), valueHolder.get().getValue().getValue().toString());
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
}
