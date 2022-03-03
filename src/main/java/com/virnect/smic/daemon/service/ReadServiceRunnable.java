package com.virnect.smic.daemon.service;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.data.domain.Task;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;
import com.virnect.smic.daemon.stream.mq.topic.ProducerManager;

@Slf4j
//@Service
@RequiredArgsConstructor
@Getter @Setter
public class ReadServiceRunnable implements Runnable{

	private final ReadTasklet tasklet;
	private final List<Tag> tags;
	private final List<Task> tasks;
	private final OpcUaClient client;
	private final ProducerManager producer;


	@Override
	public void run() {

		log.info("******************** batch run starts");
		tasks.parallelStream().forEach(task -> {

			List<Tag> targetTagas = tags.stream().filter(tag -> tag.getTask().getId().equals(task.getId())).collect(Collectors.toList());
			
			log.info("task starts : "  + targetTagas.get(0).getTask().getName() );
			StopWatch watcher = new StopWatch();
			watcher.start();

			targetTagas.forEach(tag->{
			
					tasklet.setTag(tag);
					tasklet.setNodeId(tag.getNodeId());
					tasklet.setClient(client);
					tasklet.setProducerManager(producer);
					tasklet.run();
				});

			log.info("task ends: "+ targetTagas.get(0).getTask().getName());
			watcher.stop();
			log.info("time taken: "+ watcher.getTime());
		});
		log.info("******************** batch run stops");
	}

}
