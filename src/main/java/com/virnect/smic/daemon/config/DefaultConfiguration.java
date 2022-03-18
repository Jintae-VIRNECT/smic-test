package com.virnect.smic.daemon.config;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.dao.TaskRepository;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.domain.Task;
import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TimeLogTraceUtil;
import com.virnect.smic.daemon.config.support.SchedulingTaskLauncher;
import com.virnect.smic.daemon.config.support.SimpleJobLauncher;
import com.virnect.smic.daemon.config.support.SimpleTaskLauncher;
import com.virnect.smic.daemon.launch.JobLauncher;
import com.virnect.smic.daemon.mq.TopicManager;
import com.virnect.smic.daemon.mq.kafka.KafkaTopicManager;
import com.virnect.smic.daemon.mq.rabbitmq.RabbitMqQueueManager;

@Slf4j
@Configuration
public class DefaultConfiguration {
	private final SimpleJobLauncher jobLauncher;
	private final SimpleTaskLauncher simpleTaskLauncher;
	private final TaskRepository taskRepository;
	private final TagRepository tagRepository;
	private final Environment env;

	private TopicManager topicManager;

	@Autowired(required = false)
	private SchedulingTaskLauncher schedulingTaskLauncher;

	public DefaultConfiguration(SimpleJobLauncher jobLauncher, @Lazy SimpleTaskLauncher simpleTaskLauncher,
	TaskRepository taskRepository, TagRepository tagRepository, Environment env) {
		this.jobLauncher = jobLauncher;
		this.simpleTaskLauncher = simpleTaskLauncher;
		this.taskRepository = taskRepository;
		this.tagRepository = tagRepository;
		this.env = env;
		
	}

	private JobExecution jobExecution;
	private OpcUaClient client;

	@Bean
    public LogTrace logTrace(){
        return new TimeLogTraceUtil();
    }

	@Bean (name="taskList")
	public List<Task> taskList(){
		List<Task> tasks =  taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		return Collections.synchronizedList(tasks);
	}

	@Bean (name="tagList")
	public List<Tag> tagList(){
		List<Tag> tags =  tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
			.stream()
            .filter(tag -> tag.getTask().getId() < 99 )
			.collect(Collectors.toList());
		return Collections.synchronizedList(tags);
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
			if(env.getProperty("mq.queue-manager").equalsIgnoreCase("rabbitmq")){
				topicManager = new RabbitMqQueueManager(tagList(), env);
			}else{
				topicManager = new KafkaTopicManager(env, tagRepository);
			}

			topicManager.create();
			simpleTaskLauncher.run(client, jobExecution);
			if(schedulingTaskLauncher != null){
				schedulingTaskLauncher.run(client);
				log.info("********************** run scheduling task");
			}
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}
}
