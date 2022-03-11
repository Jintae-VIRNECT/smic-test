package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.daemon.mq.ProducerManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
@Component
public class RabbitMqProducerManager implements ProducerManager {

    private static Channel producer;

    // @Autowired
	// @Qualifier("tagList")
    // private final List<String> tags;
    //private final TagRepository tagRepository;

    public RabbitMqProducerManager(){//TagRepository tagRepository){//(List<String> tags){
        //this.tagRepository = tagRepository;
        try {
            producer = createRabbitMqChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private  Channel createRabbitMqChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        Channel channel = factory.newConnection().createChannel();
        // List<String> tags = getTagList();
        // tags.forEach(tag-> {
        //     try {
        //         String queueName = tag.replaceAll(" ", "");
        //         channel.exchangeDeclare("amp.topic", "topic", true, false, null);
        //         channel.queueDeclare(queueName, false, false, false, null);
        //         channel.queueBind(queueName, "amp.topic", queueName);
        //     } catch (IOException e) {
        //         e.printStackTrace();
        //     }
        // });
        return channel;
    }

    public void runProducer(int i, String queueName, String value) {//throws IOException {
        try {
            producer.basicPublish("amq.topic", queueName, null, value.getBytes("UTF-8"));
            //producer.basicPublish("amq.topic", queueName, null, value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  private List<String> getTagList(){
	// 	return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
	// 		.stream()
    //         .filter(tag -> tag.getTask().getId() ==2 )
	// 		.map(o-> o.getNodeId())
	// 		.collect(Collectors.toList());

	// }
}
