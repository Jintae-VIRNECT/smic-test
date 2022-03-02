package com.virnect.smic.server.service.application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.daemon.config.connection.ConnectionPoolImpl;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;
import com.virnect.smic.server.data.dto.response.TagValueListResponse;
import com.virnect.smic.server.data.dto.response.TagValueResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TagRepository tagRepository;
    
    private  ConnectionPoolImpl pool;
    
    public TagValueListResponse getTagValues(Long taskId) {

        pool = ConnectionPoolImpl.getInstance();
		OpcUaClient client = pool.getConnection();
		
		List<Tag> tags = tagRepository.findByTaskId(taskId);
        List<TagValueResponse> tagValueResponses = new ArrayList<>();
       // ExecutorService execService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
        
        log.info("task start ");
        StopWatch watcher = new StopWatch();
        watcher.start();
       
        tags.parallelStream().forEach(tag->{
           ReadTasklet readTasklet = new ReadTasklet();
           readTasklet.setClient(client);
           readTasklet.setNodeId(tag.getNodeId());
           String result = readTasklet.run();
           tagValueResponses.add(new TagValueResponse(tag.getNodeId(), result));
        });
        log.info("task ends ");
        watcher.stop();
        log.info("taime taken: "+ watcher.getTime());

		pool.releaseConnection(client);
        return new TagValueListResponse(tagValueResponses, null);
    }
    
}
