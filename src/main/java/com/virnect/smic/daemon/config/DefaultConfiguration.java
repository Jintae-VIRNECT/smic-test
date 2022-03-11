package com.virnect.smic.daemon.config;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.dao.TaskRepository;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.domain.Task;
import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TimeLogTraceUtil;
import com.virnect.smic.daemon.config.support.SimpleJobLauncher;
import com.virnect.smic.daemon.config.support.SimpleTaskLauncher;
import com.virnect.smic.daemon.launch.JobLauncher;
import com.virnect.smic.daemon.mq.TopicManager;
import com.virnect.smic.daemon.mq.rabbitmq.RabbitMqQueueManager;

@Slf4j
@Configuration
public class DefaultConfiguration {
	private final SimpleJobLauncher jobLauncher;
	private final SimpleTaskLauncher simpleTaskLauncher;
	private final TaskRepository taskRepository;
	private final TagRepository tagRepository;

	//private final Environment env;

	public DefaultConfiguration(SimpleJobLauncher jobLauncher, @Lazy SimpleTaskLauncher simpleTaskLauncher,
	TaskRepository taskRepository, TagRepository tagRepository) {
		this.jobLauncher = jobLauncher;
		this.simpleTaskLauncher = simpleTaskLauncher;
		this.taskRepository = taskRepository;
		this.tagRepository = tagRepository;
	}

	private TopicManager topicManager;

	private JobExecution jobExecution;
	private OpcUaClient client;

	@Bean
    public LogTrace logTrace(){
        return new TimeLogTraceUtil();
    }

	@Bean (name="taskList")
	public List<Task> taskList(){
		return taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
	}

	@Bean (name="tagList")
	public List<Tag> tagList(){
		return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
			.stream()
            .filter(tag -> tag.getTask().getId() ==2 )
			.collect(Collectors.toList());

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
			topicManager  = new RabbitMqQueueManager(tagList());
			//topicManager = new KafkaTopicManager(env, tagRepository);
			topicManager.create();
			simpleTaskLauncher.run(client, jobExecution);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
