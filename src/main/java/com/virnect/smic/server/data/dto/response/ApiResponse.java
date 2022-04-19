package com.virnect.smic.server.data.dto.response;

import java.util.Arrays;
import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.core.EmbeddedWrapper;
import org.springframework.hateoas.server.core.EmbeddedWrappers;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import com.virnect.smic.server.data.error.ErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
@Schema(name = "api response", description = "기본 api 응답 구조")
public class ApiResponse<T> {

    @Schema(name="data", description = "API 응답 데이터를 갖고 있는 객체.", type = "object")
    T data;

    @Schema(name="code", description = "API 처리 결과 상태 코드 값, 200이면 정상 처리 완료.", type = "int", example = "200")
    int code;

    @Schema(name="message", description = "API 처리 결과에 대한 메시지", type = "string", example = "success")
    String message;

    public ApiResponse(T data) {
        this.data = data;
        this.code = ErrorCode.ERR_SUCCESS.getCode();
        this.message = ErrorCode.ERR_SUCCESS.getMessage();
    }

    public ApiResponse(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public ApiResponse(T data, ErrorCode errorCode) {
        this.data = data;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ApiResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public void setErrorResponseData(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public void setErrorResponseData(T data, int code) {
        this.data = data;
        this.code = code;
    }

    public void setErrorResponseData(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public void setErrorResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setErrorResponseData(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.data = null;
    }

}
