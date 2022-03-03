package com.virnect.smic.daemon.config.support;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.dao.TaskRepository;
import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.domain.Task;
import com.virnect.smic.common.data.domain.TaskExecution;
import com.virnect.smic.daemon.config.annotation.OpcUaConnection;
import com.virnect.smic.daemon.config.connection.ConnectionPoolImpl;
import com.virnect.smic.daemon.service.ReadServiceCallable;
import com.virnect.smic.daemon.service.ReadServiceRunnable;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;
import com.virnect.smic.daemon.stream.mq.topic.ProducerManager;
import com.virnect.smic.daemon.thread.NamedExceptionHandlingThreadFactory;

@Slf4j
@RequiredArgsConstructor
@Component
public class SimpleTaskLauncher implements DisposableBean {

	private final TaskRepository taskRepository;
	private final TagRepository tagRepository;
	private final ReadTasklet tasklet;
	private final ProducerManager producerManager;

	private  ConnectionPoolImpl pool;

	@OpcUaConnection
	public TaskExecution run(OpcUaClient client, JobExecution jobExecution) {

		List<Task> tasks = taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

		int numOfCores = Runtime.getRuntime().availableProcessors();
		log.debug("************** number of cors: "+ numOfCores);
		//  tasks = tasks.stream()
		//  	.filter(task -> task.getId() < numOfCores+1)
		//  	.collect(Collectors.toList());

		runScheduledFixedDelay(tasks, client);
		// runOneTimeWithTaskExec(tasks, client);
		
		return null;
	}

	private void runOneTimeWithTaskExec(List<Task> tasks, OpcUaClient client){
		ExecutorService execService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Tag> tags = tagRepository.findAll();

		tasks.parallelStream().forEach(task -> {
			//List<Tag> tags = tagRepository.findByTaskId(task.getId());
			List<Tag> targetTags = tags.stream().filter(tag -> tag.getTask().getId().equals(task.getId())).collect(Collectors.toList());
			ReadServiceCallable t = new ReadServiceCallable(tasklet, targetTags, client, producerManager);
			log.info("task start: "+ task.getName());
			StopWatch watcher = new StopWatch();
			watcher.start();
			Future<String> future = execService.submit(t);
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info("task ends: "+ task.getName());
			watcher.stop();
			log.info("taime taken: "+ watcher.getTime());
			});
	
		
		
	}

	private void runScheduledFixedDelay(List<Task> tasks, OpcUaClient client){
		
		ScheduledExecutorService execService
			= Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()
						,new NamedExceptionHandlingThreadFactory("read-task1"));

		List<Tag> tags = tagRepository.findAll();

		ReadServiceRunnable t = new ReadServiceRunnable(tasklet, tags, tasks, client, producerManager);
		execService.scheduleWithFixedDelay(
			t
			, 0
			, 1
			, TimeUnit.SECONDS);
	}

	@Override
	public void destroy() throws Exception {
		System.out.println(
			"Callback triggered at SimpleTaskLauncher - bean destroy method called.");

		producerManager.getProducer().flush();
		producerManager.getProducer().close();

		pool.shutdown();
		System.out.println(
			"Callback triggered at SimpleTaskLauncher - bean destroy method ends");
	}

}
