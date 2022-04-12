package com.virnect.smic.server.data.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ErrorCode {
    ERR_SUCCESS(200, "complete"),

	// error code for server process
	ERR_SERVICE_PROCESS(3001, "Service Process error, Please try again or re-start the server."),
	ERR_EXECUTION_DATA_NULL(3002, "No such execution data error"),

	// 공통 에러
	ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
	ERR_ACCESS_AUTHORITY(8003, "Access authority Error"),
	ERR_INVALID_VALUE(8004, "Invalid Value"),
	ERR_UNSUPPORTED_DATA_TYPE_EXCEPTION(8006, "Unsupported DataType Exception occured"),
	ERR_IO_EXCEPTION(8007, "IOException occured"),

	ERR_SESSION_CLIENT_METADATA_EXCEPTION(8009, "Client metadata error"),
	ERR_DATA_SAVE_EXCEPTION(8010, "Data save error"),
	ERR_UNEXPECTED_SERVER_ERROR(9999, "Unexpected Server Error, Please contact Administrator");

    @Schema(description = "에러코드")
	private int code;
	@Schema(description = "에러 메시지")
	private String message;

	ErrorCode(final int code, final String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
