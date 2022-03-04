package com.virnect.smic.daemon.mq.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.daemon.mq.TopicManager;

import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter @Setter
@RequiredArgsConstructor
public class RabbitMqQueueManager implements TopicManager{

    //private final Environment env;
	private final TagRepository tagRepository;

    public void create() throws IOException, TimeoutException {

        List<String> tags = getTagList();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        try (Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
                tags.forEach(tag-> {
                    try {
                        channel.queueDeclare(tag.replaceAll(" ", ""), false, false, false, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            );
           // channel.close();
        }
    }
    
    private List<String> getTagList(){
		return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
			.stream()
			.map(o-> o.getNodeId())
			.collect(Collectors.toList());

	}
}
