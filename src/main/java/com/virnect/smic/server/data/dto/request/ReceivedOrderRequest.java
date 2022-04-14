package com.virnect.smic.server.data.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name="order request", description = "주문 요청")
public class ReceivedOrderRequest {

	@NotNull
	@Schema(name="execution id", required = true, description = "현재 진행 중인 작업 id", example="1", type="long")
	private long execution_id;

	@Size(min=1, max=4)
	@Schema(name="customer name", required = true, description = "고객 이름", example="홍길동", type="string", maxLength = 4)
	private String customerNameValue;

	@NotNull
	@Schema(name="customer age", required = true, description = "고객 나이", type="int", example="27")
	private int customerAgeValue;

	@Min(1) @Max(2)
	@Schema(name="font", required = true, description = "폰트 정보, 1: 한글, 2: 영문", type="int", example = "1", allowableValues = {"1","2"})
	private int font;

	@Size(min=2, max=2)
	private String productCDValue;

	@Pattern(regexp="^1$|^2$")
	private String customerGenderValue;
	@Size(min=1, max=8)
	private String customerGroupValue;
	@Size(min=1, max=16)
	private String customerMailValue;
	@Size(min=1, max=4)
	private String customerFirstCallValue;
	@Size(min=1, max=4)
	private String customerSecondCallValue;
	@Size(min=1, max=4)
	private String customerThirdCallValue;
	@NotNull
	private Boolean adv_agree;
	private int pageNum = 2;

}
