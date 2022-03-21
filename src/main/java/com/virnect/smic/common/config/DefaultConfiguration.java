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
import com.virnect.smic.common.data.dao.ModelLineRepository;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.data.domain.ModelLine;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.launch.JobLauncher;
import com.virnect.smic.common.util.LogTrace;
import com.virnect.smic.common.util.TimeLogTraceUtil;

@Slf4j
@Configuration
public class DefaultConfiguration {
	private final SimpleJobLauncher jobLauncher;
	private final ModelLineRepository taskRepository;
	private final TagRepository tagRepository;
	
	private JobExecution jobExecution;

	public DefaultConfiguration(SimpleJobLauncher jobLauncher,// @Lazy SimpleTaskLauncher simpleTaskLauncher,
	ModelLineRepository taskRepository, TagRepository tagRepository, Environment env) {
		this.jobLauncher = jobLauncher;
		this.taskRepository = taskRepository;
		this.tagRepository = tagRepository;	
	}


	@Bean
    public LogTrace logTrace(){
        return new TimeLogTraceUtil();
    }

	@Bean (name="models")
	public List<ModelLine> taskList(){
		List<ModelLine> models =  taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
		return Collections.synchronizedList(models);
	}

	@Bean (name="tagList")
	public List<Tag> tagList(){
		List<Tag> tags =  tagRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
			.stream()
            .filter(tag -> tag.getModelLine().getId() < 99 )
			.collect(Collectors.toList());
		return Collections.synchronizedList(tags);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initialize() {
		try {
			jobExecution =  jobLauncher.run();
			//launchTaskExecutor(jobExecution);
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	public JobLauncher getJobLauncher() {
		return jobLauncher;
	}
}
