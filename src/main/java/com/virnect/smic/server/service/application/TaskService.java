package com.virnect.smic.server.service.application;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.virnect.smic.common.config.annotation.OpcUaConnection;
import com.virnect.smic.common.data.dao.TagRepository;
import com.virnect.smic.common.data.domain.Tag;
import com.virnect.smic.common.service.tasklet.ReadTasklet;
import com.virnect.smic.common.util.PagingUtils;
import com.virnect.smic.server.data.dto.response.PageMetadataResponse;
import com.virnect.smic.server.data.dto.response.TagValueListResponse;

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

		List<Tag> tags = tagRepository.findByModelLineId(taskId);
        ConcurrentHashMap<String, String> result = readTasklet.readAndPublishAsync(tags, client, false);

		PageMetadataResponse pageMeta = PagingUtils.pagingBuilder(
                                                        false, null, result.size(), 1, result.size(), true);
        
        return new TagValueListResponse(result, pageMeta);
    }
}
