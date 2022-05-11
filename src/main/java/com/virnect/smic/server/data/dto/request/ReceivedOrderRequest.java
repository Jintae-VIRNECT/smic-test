package com.virnect.smic.server.data.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Data;

@Data
@Schema(name="orderRequest", description = "주문 요청")
public class ReceivedOrderRequest {

	@NotNull
	@Schema(name="executionId", required = true, description = "현재 진행 중인 작업 id", example="1", type="long")
	private long executionId;

	@NotNull
	@Schema(name="deviceId", required = true, description = "요청 장비 id", example="1", type="long")
	private long deviceId;

	@Size(min=1, max=20)
	@Schema(name="customerNameValue", required = true, description = "고객 이름", example="홍길동", type="string"
		, maxLength = 20)
	private String customerNameValue;

	@NotNull
	@Schema(name="customerAgeValue", required = true, description = "고객 나이", type="int", example="27")
	private int customerAgeValue;

	@Min(1) @Max(2)
	@Schema(name="font", required = true, description = "폰트 정보, 1: 제주 명조체, 2: 나눔 고딕체", type="int"
		, example = "1", allowableValues = {"1","2"})
	private int font;

	@Size(min=2, max=2)
	@Schema(name="productCDValue", required = true, description = "제품 코드", type = "string", example = "A1"
		,maxLength = 2)
	private String productCDValue;

	@Pattern(regexp="^1$|^2$")
	@Schema(name="customerGenderValue", required = true, description = "성별, 1: 남자, 2: 여자", type="int"
		, example = "1", allowableValues = {"1","2"})
	private String customerGenderValue;

	@Size(min=1, max=8)
	@Schema(name="customerGroupValue", required = true, description = "고객 소속", example="버넥트", type="string"
		, maxLength = 8)
	private String customerGroupValue;

	@Size(min=1, max=16)
	@Schema(name="customerMailValue", required = true, description = "고객 이메일", example="test@virnect.com"
		, type="string", maxLength = 16)
	private String customerMailValue;

	@Size(min=1, max=4)
	@Schema(name="customerFirstCallValue", required = true, description = "전화번호 1/3", example="010"
		, type="string", maxLength = 4)
	private String customerFirstCallValue;

	@Size(min=1, max=4)
	@Schema(name="customerSecondCallValue", required = true, description = "전화번호 2/3", example="1234"
		, type="string", maxLength = 4)
	private String customerSecondCallValue;

	@Size(min=1, max=4)
	@Schema(name="customerThirdCallValue", required = true, description = "전화번호 3/3", example="5678"
		, type="string", maxLength = 4)
	private String customerThirdCallValue;

	@NotNull
	@Schema(name="adv_agree", required = false, description = "광고 정보 수신 동의", example="false"
		, type="boolean", defaultValue = "false")
	private Boolean adv_agree = false;

	@Schema(name="pageNum", required = false, description = "페이지 번호", example="2"
		, type="int", defaultValue = "2")
	private int pageNum = 2;

	@Override
	public String toString() {
		return "ReceivedOrderRequest{" +
			"executionId=" + executionId +
			", deviceId=" + deviceId +
			", customerNameValue='" + customerNameValue + '\'' +
			", customerAgeValue=" + customerAgeValue +
			", font=" + font +
			", productCDValue='" + productCDValue + '\'' +
			", customerGenderValue='" + customerGenderValue + '\'' +
			", customerGroupValue='" + customerGroupValue + '\'' +
			", customerMailValue='" + customerMailValue + '\'' +
			", customerFirstCallValue='" + customerFirstCallValue + '\'' +
			", customerSecondCallValue='" + customerSecondCallValue + '\'' +
			", customerThirdCallValue='" + customerThirdCallValue + '\'' +
			", adv_agree=" + adv_agree +
			", pageNum=" + pageNum +
			'}';
	}
}
