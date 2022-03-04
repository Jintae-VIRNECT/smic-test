package com.virnect.smic.daemon.config;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.daemon.config.support.SimpleJobLauncher;
import com.virnect.smic.daemon.config.support.SimpleTaskLauncher;
import com.virnect.smic.daemon.launch.JobLauncher;
import com.virnect.smic.daemon.mq.TopicManager;
import com.virnect.smic.daemon.mq.kafka.KafkaTopicManager;
import com.virnect.smic.daemon.mq.rabbitmq.RabbitMqQueueManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DefaultConfiguration {
	private final SimpleJobLauncher jobLauncher;
	private final SimpleTaskLauncher simpleTaskLauncher;
	private final TagRepository tagRepository;
	private final Environment env;
	
	private TopicManager topicManager;
	
	private JobExecution jobExecution;
	private OpcUaClient client;
	
	//temp
    @Bean (name="tagFiles")
	@ConfigurationProperties(prefix = "resource.tag.file")
	public List<String> getTagFiles(){
		return new ArrayList<String>();
	}

	//temp
    @Bean (name="avgConv011NodeIds")
	@ConfigurationProperties(prefix = "my.node-id-sim")
	public List<String> getNodeIds(){
		return new ArrayList<String>();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		try {
			jobExecution =  jobLauncher.run();
			launchTaskExecutor(jobExecution);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	public JobLauncher getJobLauncher() {
		return jobLauncher;
	}

	public void launchTaskExecutor(JobExecution jobExecution) {
		
		try {
			topicManager  = new RabbitMqQueueManager(tagRepository);
			//topicManager = new KafkaTopicManager(env, tagRepository);
			topicManager.create();
			simpleTaskLauncher.run(client, jobExecution);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
