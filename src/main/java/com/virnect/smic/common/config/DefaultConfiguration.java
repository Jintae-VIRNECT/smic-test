package com.virnect.smic.common.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.config.support.SimpleJobLauncher;
import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.data.dto.TagDto;
import com.virnect.smic.common.launch.JobLauncher;
import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TimeLogTraceUtil;

@Slf4j
@Configuration
public class DefaultConfiguration {
	private final SimpleJobLauncher jobLauncher;
	private final TagRepository tagRepository;
	private final Environment env;

	public DefaultConfiguration(SimpleJobLauncher jobLauncher,// @Lazy SimpleTaskLauncher simpleTaskLauncher,
	 TagRepository tagRepository,
		Environment env) {
		this.jobLauncher = jobLauncher;
		this.tagRepository = tagRepository;
		this.env = env;
	}


	@Bean
    public LogTrace logTrace(){
		TimeLogTraceUtil logtrace = new TimeLogTraceUtil();
        return logtrace;
    }

	@Bean (name="queueNameMap")
	public ConcurrentHashMap<String, String> queueNameMap(){
		ConcurrentHashMap<String, String> queueNameMap = new ConcurrentHashMap<>();
		List<TagDto> tags = tagList();
		tags.forEach(tag->queueNameMap.put(tag.getQueueName(), tag.getNodeId()));
		return queueNameMap;
	}

	@Bean (name="tagList")
	public List<TagDto> tagList(){
		// List<Tag> tags =  tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
		// 	.stream()
        //     .filter(tag -> tag.isActivated())
		// 	.collect(Collectors.toList());
		List<TagDto> tags = tagRepository.findNodeIdQueueNameModelLineIdByActivated();
		return Collections.synchronizedList(tags);//getQueueNamedTag(tags));
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		try {
			jobLauncher.run(getExecutionMode());
			//launchTaskExecutor(jobExecution);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	public JobLauncher getJobLauncher() {
		return jobLauncher;
	}

	@Bean (name = "executionMode")
	public ExecutionMode getExecutionMode() {
		if(env.getProperty("server.daemon").equalsIgnoreCase("true")){
			return ExecutionMode.DAEMON;
		}else{
			return ExecutionMode.SERVER;
		}
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
