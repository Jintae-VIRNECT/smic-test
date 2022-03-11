package com.virnect.smic.daemon.config.support;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.domain.JobExecution;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.domain.Task;
import com.virnect.smic.common.data.domain.TaskExecution;
import com.virnect.smic.daemon.config.annotation.OpcUaConnection;
import com.virnect.smic.daemon.config.connection.ConnectionPoolImpl;
import com.virnect.smic.daemon.service.ReadServiceCallable;
import com.virnect.smic.daemon.service.ReadServiceRunnable;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;
import com.virnect.smic.daemon.thread.NamedExceptionHandlingThreadFactory;

@Slf4j
@Getter @Setter
@Component
public class SimpleTaskLauncher implements DisposableBean {

	private final ReadTasklet tasklet;

	@Autowired
	@Qualifier("taskList")
    private List<Task> tasks;

	@Autowired
	@Qualifier("tagList")
    private List<Tag> tags;

	private OpcUaClient client;

	public SimpleTaskLauncher( ReadTasklet tasklet) {//@Lazy ReadTasklet tasklet) {
		this.tasklet = tasklet;
	}
	

	private  ConnectionPoolImpl pool;

	@OpcUaConnection
	public TaskExecution run(OpcUaClient client, JobExecution jobExecution) {

		int numOfCores = Runtime.getRuntime().availableProcessors();
		log.debug("************** number of cors: "+ numOfCores);
		log.info("parallelism: "+ ForkJoinPool.getCommonPoolParallelism());
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "100");
		setClient(client);
		// runScheduledFixedDelay(tasks, client);
	    //runOneTimeWithTaskExec(tasks, client);
		
		return null;
	}

	private void runOneTimeWithTaskExec(List<Task> tasks, OpcUaClient client){
		
		//ExecutorService execService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		//List<Tag> tags = tagRepository.findAll();

		tasks.parallelStream().forEach(task -> { 

			List<Tag> targetTags = tags.stream().filter(tag -> tag.getTask().getId().equals(task.getId())).collect(Collectors.toList());
			ReadServiceCallable t = new ReadServiceCallable(tasklet);//, targetTags, client);
			t.setTags(targetTags);
			t.setClient(client);
			//log.info("task start: "+ task.getName());
			//StopWatch watcher = new StopWatch();
			//watcher.start();
			//Future<String> future = execService.submit(t);
			try {
				//future.get();
				t.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//log.info("task ends: "+ task.getName());
			//watcher.stop();
			//log.info("time taken: "+ watcher.getTime());
		});	
		
	}

	@Async
	@Scheduled(fixedDelay = 1000, initialDelay = 5000)
	void runScheduledFixedDelay(){
		tasks.stream().forEach(task -> {
			List<Tag> targetTagas = tags.stream().filter(tag -> tag.getTask().getId().equals(task.getId())).collect(Collectors.toList());
		
			targetTagas.parallelStream().forEach(tag->{
			
					tasklet.setTag(tag);
					tasklet.initiate(tag.getNodeId(), getClient());
					tasklet.readAndPublish();
				});
			});
	}

	@Override
	public void destroy() throws Exception {
		System.out.println(
			"Callback triggered at SimpleTaskLauncher - bean destroy method called.");
		pool.shutdown();
		System.out.println(
			"Callback triggered at SimpleTaskLauncher - bean destroy method ends");
	}

}
