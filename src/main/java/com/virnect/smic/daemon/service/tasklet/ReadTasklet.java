package com.virnect.smic.daemon.service.tasklet;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.stream.mq.topic.ProducerManager;

@Slf4j
@RequiredArgsConstructor
@Component
@Getter @Setter
public class ReadTasklet {

	private OpcUaClient client;
	private String nodeId;
	private Tag tag;
	private ProducerManager producerManager;

	public ReadTasklet(String nodeId){
		this.nodeId = nodeId;
	}

	public void setClient(OpcUaClient client){
		this.client = client;
	}

	public String run() {

		NodeId nodeIdString  = new NodeId(2, nodeId);
		DataValue value;
		try {

			value = client.readValue(0, TimestampsToReturn.Both, nodeIdString)
				.get();
			Object message = value.getValue().getValue();
			if(message != null) {
				log.debug("{} -> {}", nodeId, message);
				if(producerManager != null)
					producerManager.runProducer(1, nodeId.replaceAll(" ", ""), message.toString());
				return message.toString();	
			}else{
				return "";
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			log.error("*******************  exception occurred at " +  tag.toString());
			throw new IllegalStateException();
		}
	}
}
