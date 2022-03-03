package com.virnect.smic.server.data.dto.response;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagValueListResponse {
    private final ConcurrentHashMap<String, String> tagValueList;
    private final PageMetadataResponse pageMeta;
}
