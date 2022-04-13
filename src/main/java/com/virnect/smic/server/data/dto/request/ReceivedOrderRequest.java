package com.virnect.smic.server.data.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ReceivedOrderRequest {

	@NotNull
	private long execution_id;
	@NotNull
	private int customerAgeValue;
	@Min(1) @Max(2)
	private int font;
	@Size(min=2, max=2)
	private String productCDValue;
	@Size(min=1, max=5)
	private String customerNameValue;
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
