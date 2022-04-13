package com.virnect.smic.server.data.dto.request.smic;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendOrderRequest {

	private int customerAgeValue;
	private int font;
	private String productCDValue;
	private String customerNameValue;
	private String customerGenderValue;
	private String customerGroupValue;
	private String customerMailValue;
	private String customerFirstCallValue;
	private String customerSecondCallValue;
	private String customerThirdCallValue;
	private Boolean adv_agree;
	private int pageNum = 2;

	@NotBlank
	private String userID;
	@NotBlank
	private String planCDValue;
}
