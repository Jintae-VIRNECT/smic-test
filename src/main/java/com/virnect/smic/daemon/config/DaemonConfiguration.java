package com.virnect.smic.daemon.config;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import com.virnect.smic.common.config.ConfigurationException;
import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.dto.TagDto;
import com.virnect.smic.daemon.config.support.SchedulingTaskLauncher;
import com.virnect.smic.daemon.mq.TopicManager;
import com.virnect.smic.daemon.mq.kafka.KafkaTopicManager;
import com.virnect.smic.daemon.mq.rabbitmq.RabbitMqQueueManager;

@Slf4j
@Configuration
public class DaemonConfiguration {
	private final TagRepository tagRepository;
	private final Environment env;

	@Autowired
	@Qualifier("tagList")
    private List<TagDto> tags;

	private TopicManager topicManager;

	@Autowired(required = false)
	private SchedulingTaskLauncher schedulingTaskLauncher;

	public DaemonConfiguration(TagRepository tagRepository, Environment env) {
		this.tagRepository = tagRepository;
		this.env = env;
		
	}

	private OpcUaClient client;


	// public void initialize(long executionId) {
	// 	try {
	// 		launchTaskExecutor();
	// 	} catch (Exception e) {
	// 		throw new ConfigurationException(e);
	// 	}
	// }

	//@EventListener(ApplicationReadyEvent.class)
	public void launchTaskExecutor(long executionId) {
		
		try {
			if(schedulingTaskLauncher != null){

				createTopics();
				schedulingTaskLauncher.run(client);
				schedulingTaskLauncher.setExecutionId(executionId);
				log.info("run scheduling task started");
			}
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	private void createTopics() throws Exception{
		if(env.getProperty("mq.queue-manager").equalsIgnoreCase("rabbitmq")){
			topicManager = new RabbitMqQueueManager(tags, env);
		}else{
			topicManager = new KafkaTopicManager(env, tagRepository);
		}

		topicManager.create();
	}

	public void stopTaskExecutor(){
		schedulingTaskLauncher.setClient(null);
		schedulingTaskLauncher.setExecutionId(-1l);
	}

	public long getRunningExecutionId(){
		return  (schedulingTaskLauncher.getClient()!=null?schedulingTaskLauncher.getExecutionId():-1l);
	}
}
