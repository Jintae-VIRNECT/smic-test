package com.virnect.smic.server.data.dto.response;

import java.time.LocalDateTime;

import org.springframework.hateoas.RepresentationModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.smic.common.data.domain.ExecutionStatus;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name="deviceResponse", description = "장비 응답")
public class DeviceResource extends RepresentationModel<DeviceResource> {
	@Schema(name="id", description = "장비 id", example = "1", type = "long")
	private Long id;
	@Schema(name="execution status", description = "작업 상태", example = "STARTED", type="enum")
	private ExecutionStatus executionStatus;
	@Schema(name="mac address", description = "장비 mac address", example="E0D55EA24FC3", type = "string")
	private String macAddress;
	@Schema(name="execution id", description = "작업 id", example = "1", type = "long")
	private Long executionId;
	@Schema(name="sequence number", description = "큐 할당 번호", example = "1", type = "int")
	private int sequenceNumber;

	@Schema(name="createdDate", description = "생성 일시", example="2022-04-12 16:11:36", type="datetime")
	private LocalDateTime createdDate;
	@Schema(name="updatedDate", description = "갱신 일시", example="2022-04-12 17:11:36", type="datetime")
	private LocalDateTime updatedDate;
}
