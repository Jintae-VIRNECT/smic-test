package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.util.PagingUtils;
import com.virnect.smic.daemon.config.annotation.OpcUaConnection;
import com.virnect.smic.daemon.service.tasklet.ReadTasklet;
import com.virnect.smic.server.data.dto.response.PageMetadataResponse;
import com.virnect.smic.server.data.dto.response.TagValueListResponse;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    @Resource
    private TaskService self = this;

    private final TagRepository tagRepository;
    private final ReadTasklet readTasklet;

    @OpcUaConnection
    public TagValueListResponse getTagValues(OpcUaClient client, Long taskId) {

		List<Tag> tags = tagRepository.findByTaskId(taskId);
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
       
       tags.parallelStream().forEach(tag->{
           readTasklet.initiate(tag.getNodeId(), client);
           String result = readTasklet.readOnly();
           map.put(tag.getNodeId(), result);
        });

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(false, null, map.size(), 1, map.size(), true);
        
        return new TagValueListResponse(map, pageMeta);
    }
}
