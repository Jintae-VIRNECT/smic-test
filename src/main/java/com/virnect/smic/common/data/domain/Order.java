package com.virnect.smic.common.data.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;

@Entity
@Getter
@Table(name="supply_order")
public class Order extends BaseTimeEntity{
	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
	private int pageNum;

	private String userID;
	private String planCDValue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "execution_id")
	private Execution execution;

}
