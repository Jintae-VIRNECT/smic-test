package com.virnect.smic.server.data.error;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema
public class ErrorResponseMessage {
    @Schema(description = "에러 응답 코드")
    private int code;
    @Schema(description = "서비스명")
    private String service;
    @Schema(description = "에러 응답 메시지")
    private String message;
    @Schema(description = "에러 응답 데이터")
    private Map<String, Object> data;

    public ErrorResponseMessage(final ErrorCode error) {
        this.code = error.getCode();
        this.message = error.getMessage();
        this.service = "remote";
        data = new HashMap<>();
    }
}
