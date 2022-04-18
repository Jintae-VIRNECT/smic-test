package com.virnect.smic.server.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.validation.ObjectError;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name="orderResponse", description = "주문 응답")
public class OrderResource extends RepresentationModel<OrderResource> {
	private Long id;

	@Schema(name="executionId", description = "현재 진행 중인 작업 id", example="1", type="long")
	private long executionId;

	@Schema(name="customerNameValue", description = "고객 이름", example="홍길동", type="string"
		, maxLength = 20)
	private String customerNameValue;

	@Schema(name="customerAgeValue", description = "고객 나이", type="int", example="27")
	private int customerAgeValue;

	@Schema(name="font", description = "폰트 정보, 1: 제주 명조체, 2: 나눔 고딕체", type="int"
		, example = "1", allowableValues = {"1","2"})
	private int font;

	@Schema(name="productCDValue", description = "제품 코드", type = "string", example = "A1"
		,maxLength = 2)
	private String productCDValue;

	@Schema(name="customerGenderValue", description = "성별, 1: 남자, 2: 여자", type="int"
		, example = "1", allowableValues = {"1","2"})
	private String customerGenderValue;

	@Schema(name="customerGroupValue", description = "고객 소속", example="버넥트", type="string"
		, maxLength = 8)
	private String customerGroupValue;

	@Schema(name="customerMailValue", description = "고객 이메일", example="test@virnect.com"
		, type="string", maxLength = 16)
	private String customerMailValue;

	@Schema(name="customerFirstCallValue", description = "전화번호 1/3", example="010"
		, type="string", maxLength = 4)
	private String customerFirstCallValue;

	@Schema(name="customerSecondCallValue", description = "전화번호 2/3", example="1234"
		, type="string", maxLength = 4)
	private String customerSecondCallValue;

	@Schema(name="customerThirdCallValue", description = "전화번호 3/3", example="5678"
		, type="string", maxLength = 4)
	private String customerThirdCallValue;

	@Schema(name="adv_agree", description = "광고 정보 수신 동의", example="false"
		, type="boolean")
	private Boolean adv_agree = false;

	@Schema(name="pageNum", description = "페이지 번호", example="2"
		, type="int", defaultValue = "2")
	private int pageNum = 2;

	@Schema(name="userID", description = "사용자 id", example="user1", type="string")
	private String userID;

	@Schema(name="planCDValue", description = "생산계획코드", example="012345", type="string")
	private String planCDValue;

	@Schema(name="createdDate", description = "생성 일시", example="2022-04-12 16:11:36", type="datetime")
	private LocalDateTime createdDate;

	@Schema(name="responseStatus", description = "SMIC 응답 상태 코드", example="200", type="int")
	private int responseStatus;

	@Schema(name="requestErrors", description = "주문 요청 에러", example="", type="list")
	private List<ObjectError> errors;

}
