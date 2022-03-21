package com.virnect.smic.server.data.error;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public enum ErrorCode {
    ERR_SUCCESS(200, "complete"),

	// error code for server process
	ERR_SERVICE_PROCESS(3001, "Remote Service Server Process error, Please try again or re-start the server."),
	ERR_LICENSE_SERVICE_PROCESS(3002, "License Server Process error, Please try again or contact."),
	ERR_WORKSPACE_PROCESS(3003, "Workspace Service Server Process error, Please try again or contact."),
	ERR_USER_PROCESS(3004, "User Service Server Process error, Please try again or contact."),
	ERR_MESSAGE_PROCESS(3005, "Message Service Server Process error, Please try again or contact."),

	// 공통 에러
	ERR_INVALID_REQUEST_PARAMETER(8001, "Invalid request parameter cause api errors"),
	ERR_ACCESS_AUTHORITY(8003, "Access authority Error"),
	ERR_INVALID_VALUE(8004, "Invalid Value"),
	ERR_AUTHORIZATION_EXPIRED(8005, "Authorization token is expired"),
	ERR_UNSUPPORTED_DATA_TYPE_EXCEPTION(8006, "Unsupported DataType Exception occured"),
	ERR_IO_EXCEPTION(8007, "IOException occured"),
	ERR_MEMBER_LOGOUT_OR_JOIN(8008, "Members are already logout or join"),
	ERR_SESSION_CLIENT_METADATA_NULL(8010, "Client metadata error"),
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
