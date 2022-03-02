package com.virnect.smic.server.data.dto.response;

import com.virnect.smic.server.data.error.ErrorCode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
@RequiredArgsConstructor
public class ApiResponse<T> {
    @ApiModelProperty(value = "API 응답 데이터를 갖고 있는 객체.", dataType = "object")
    T data;
    ///Map<String, Object> data = new HashMap<>();
    @ApiModelProperty(value = "API 처리 결과 상태 코드 값, 200이면 정상 처리 완료.", dataType = "int", example = "200")
    int code;
    @ApiModelProperty(value = "API 처리 결과에 대한 메시지", dataType = "string", example = "success")
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
