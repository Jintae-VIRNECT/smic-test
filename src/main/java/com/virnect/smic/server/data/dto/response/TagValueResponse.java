package com.virnect.smic.server.data.dto.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TagValueResponse {
    
    private String nodeId;
    private String dataValue;

    public TagValueResponse(String nodeId, String dataValue) {
        this.nodeId= nodeId;
        this.dataValue = dataValue;
    }
}
