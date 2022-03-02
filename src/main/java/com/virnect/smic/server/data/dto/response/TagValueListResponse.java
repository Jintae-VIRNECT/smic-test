package com.virnect.smic.server.data.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TagValueListResponse {
    private final List<TagValueResponse> tagValueList;
    private final PageMetadataResponse pageMeta;
}
