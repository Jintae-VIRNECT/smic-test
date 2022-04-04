package com.virnect.smic.common.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.config.support.SimpleJobLauncher;
import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.ExecutionMode;
import com.virnect.smic.common.data.domain.Tag;
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
	 TagRepository tagRepository, Environment env) {
		this.jobLauncher = jobLauncher;
		this.tagRepository = tagRepository;	
		this.env = env;
	}


	@Bean
    public LogTrace logTrace(){
		TimeLogTraceUtil logtrace = new TimeLogTraceUtil();
        return logtrace;
    }

	// @Bean (name="models")
	// public List<ModelLine> taskList(){
	// 	List<ModelLine> models =  taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
	// 	return Collections.synchronizedList(models);
	// }

	@Bean (name="tagList")
	public List<Tag> tagList(){
		List<Tag> tags =  tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
			.stream()
            .filter(tag -> tag.isActivated())
			.collect(Collectors.toList());
		return Collections.synchronizedList(getQueueNamedTag(tags));
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

	private List<Tag> getQueueNamedTag(List<Tag> tags){
		tags.forEach(t -> t.setQueueName(makeQueueName(t)));
		return tags;
	}

	private String makeQueueName(Tag tag){
		StringBuilder sb = new StringBuilder();
		sb.append(tag.getModelLine().getName());
		sb.append(".");
		sb.append(tag.getMainCategory());
		sb.append(".");
		sb.append(tag.getSubCategory());

		if(tag.getSub2Category() != null && !tag.getSub2Category().isBlank()){
			sb.append(".");
			sb.append(tag.getSub2Category());
		}

		if(tag.getEtc() != null && !tag.getEtc().isBlank()){
			sb.append(".");
			sb.append(tag.getEtc());
		}
		return sb.toString();
	}
}
