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

	// error code for company information
	ERR_COMPANY_CREATE_FAIL(3500, "Company information create is failed"),
	ERR_COMPANY_INVALID_CODE(3501, "Company information request is invalid"),
	ERR_COMPANY_NOT_EXIST(3502, "company in workspace is not exist"),
	ERR_COMPANY_UPDATE_FAIL(3503, "Company information update is failed"),

	// error code for remote session
	ERR_ROOM_INFO(4000, "Room Info Error"),
	ERR_ROOM_INFO_ACCESS(4001, "Room info access fail"),
	ERR_ROOM_NOT_FOUND(4002, "Room Not found maybe session is not active"),
	ERR_ROOM_INFO_UPDATE(4003, "Room info update fail"),
	ERR_ROOM_PROFILE_IMAGE_UPLOAD(4004, "Room profile upload fail"),
	ERR_ROOM_PROFILE_IMAGE_EXTENSION(4005, "Room profile image extension error, not support"),
	ERR_ROOM_PROFILE_IMAGE_SIZE_LIMIT(4006, "Room profile image size is exceeded"),
	ERR_ROOM_MEMBER_INFO_EMPTY(4007, "Room Member information is empty"),
	ERR_ROOM_MEMBER_NOT_FOUND(4008, "Specific room member can not found"),
	ERR_ROOM_MEMBER_IS_OVER(4009, "Room cannot join"),
	ERR_ROOM_LICENSE_TYPE(4010, "Room license type is not allowed"),
	ERR_ROOM_INVALID_PERMISSION(4011, "Invalid permission to the Room"),
	ERR_ROOM_STATUS_NOT_ACTIVE(4012, "Room is not active"),
	ERR_ROOM_MEMBER_NOT_ALLOWED(4013, "Not allowed member cannot join the room"),
	ERR_ROOM_MEMBER_NOT_ASSIGNED(4014, "any member not assigned, cannot join the room"),
	ERR_ROOM_LEADER_INVALID_EXIT(4015, "Room leader can not exit the room, must delete."),
	ERR_ROOM_MEMBER_ALREADY_JOINED(4016, "This member is already joined, Duplicate participation has occurred."),
	ERR_ROOM_MEMBER_STATUS_INVALID(4017, "This member status is invalid"),
	ERR_ROOM_MEMBER_KICK_FAIL(4018, "This member cannot enforce disconnect."),
	ERR_ROOM_CREATE_FAIL(4019, "Room Create fail"),
	ERR_ROOM_PROCESS_FAIL(4020, "Current Room request is failed"),
	ERR_ROOM_MEMBER_MAX_COUNT(4021, "Current Room member is over the limit"),
	ERR_ROOM_LICENSE_COMPANY_CODE(4022, "Can not find license for company code"),
	ERR_ROOM_MEMBER_NOT_EVICTED(4023, "This member need to force disconnect."),
	ERR_ROOM_CLOSE_FAIL(4024, "Current Room was not removed beforehand."),
	ERR_ROOM_MEMBER_EVICTED_STATUS(4025, "This member is evicted status"),

	// Group 관련 에러
	ERR_GROUP_NOT_FOUND(4026, "remote group can not found"),
	ERR_GROUP_COUNT_OVER(4027, "remote group over count"),
	ERR_GROUP_MEMBER_COUNT_OVER(4027, "remote group member over count"),
	ERR_GROUP_MEMBER_ALREADY_JOINED(4030, "There are already joined member"),
	ERR_MEMBER_INVALID(4031, "This member uuid is invalid"),

	// Guest 관련 에러
	ERR_GUEST_USER_NOT_ENOUGH(4028, "Not enough unassigned guest users"),
	ERR_GUEST_USER_NOT_FOUND(4029, " Guest user not found"),
	
	// error code for service validation
	ERR_LICENSE_NOT_VALIDITY(5001, "License has no validity"),
	ERR_LICENSE_TYPE_VALIDITY(5002, "License is not the license type"),
	ERR_LICENSE_PRODUCT_VALIDITY(5003, "License has no product"),
	ERR_LICENSE_UNEXPECTED_TYPE(5004, "This license is not unexpected type"),

	// error code for remote session history
	ERR_HISTORY_NOT_VALIDITY(6001, "License has no validity"),
	ERR_HISTORY_ROOM_NOT_FOUND(6001, "Room History can not found"),
	ERR_HISTORY_ROOM_MEMBER_NOT_FOUND(6002, "Room History member not found"),
	ERR_HISTORY_TYPE_VALIDITY(6003, "License is not the license type"),
	ERR_HISTORY_PRODUCT_VALIDITY(6004, "License has no product"),
	ERR_HISTORY_UNEXPECTED_TYPE(6005, "This license is not unexpected type"),

	// error code for file upload and download
	ERR_FILE_UPLOAD_FAILED(7001, "File upload has failed"),
	ERR_FILE_ASSUME_DUMMY(7002, "File is assumed to dummy"),
	ERR_FILE_UNSUPPORTED_EXTENSION(7003, "This file extension is not supported"),
	ERR_FILE_SIZE_LIMIT(7004, "This file size is exceeded"),
	ERR_FILE_DOWNLOAD_FAILED(7005, "File download has failed"),
	ERR_FILE_DELETE_FAILED(7006, "File delete has failed"),
	ERR_FILE_DELETE_EXCEPTION(7007, "File delete has an exception"),
	ERR_FILE_UPLOAD_EXCEPTION(7008, "File upload has an exception"),
	ERR_FILE_GET_SIGNED_EXCEPTION(7009, "Get Pre signed url has an exception"),
	ERR_FILE_DOWNLOAD_EXCEPTION(7010, "File download has an exception"),
	ERR_FILE_COVERT_FAILED(7011, "File converting is failed"),
	ERR_FILE_NOT_FOUND(7013, "This file can not found."),
	ERR_PROFILE_UPLOAD_FAILED(7015, "Profile upload has failed"),

	// 스토리지 에러,
	ERR_STORAGE_NOT_SUPPORTED(7012, "Storage service is not supported. Please check storage server url."),
	ERR_STORAGE_LIMIT_REACHED(7016, "The storage limit is about to be reached"),
	ERR_STORAGE_CAPACITY_FULL(7017, "The storage capacity is full"),
	
	ERR_PDF_PASSWORD(7018, "The pdf file is locked"),
	ERR_MAKE_THUMBNAIL(7019, "Failed to convert the thumbnail file"),

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
