package com.virnect.smic.server.data.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema
public class PageMetadataResponse {
    @Schema(description = "Page number currently viewed", example = "0")
    private int currentPage;
    @Schema(description = "Current number of data per page", example = "2")
    private int currentSize;
    @Schema(description = "Number of data currently returned", example = "2")
    private int numberOfElements;
    @Schema(description = "Total number of page", example = "10")
    private int totalPage;
    @Schema(description = "Total number of data", example = "20")
    private long totalElements;
    @Schema(description = "Whether the last page", example = "false")
    private boolean last;

    @Override
    public String toString() {
        return "PageMetadataResponse{" +
            "currentPage='" + currentPage + '\'' +
            ", currentSize='" + currentSize + '\'' +
            ", numberOfElements='" + numberOfElements + '\'' +
            ", totalPage='" + totalPage + '\'' +
            ", totalElements='" + totalElements + '\'' +
            ", last='" + last + '\'' +
            '}';
    }
}
