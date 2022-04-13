package com.virnect.smic.common.data.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name="supply_order")
public class Order {
	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "customer_age")
	private int customerAgeValue;
	private int font;
	@Column(name = "product_cd")
	private String productCDValue;
	@Column(name = "customer_name")
	private String customerNameValue;
	@Column(name = "customer_gender")
	private String customerGenderValue;
	@Column(name = "customer_group")
	private String customerGroupValue;
	@Column(name = "customer_mail")
	private String customerMailValue;
	@Column(name = "customer_first_call")
	private String customerFirstCallValue;
	@Column(name = "customer_second_call")
	private String customerSecondCallValue;
	@Column(name = "customer_third_call")
	private String customerThirdCallValue;

	private Boolean adv_agree;
	@Column(name = "page_num")
	private int pageNum;
	@Column(name = "user_id")
	private String userID;
	@Column(name = "plan_cd_value")
	private String planCDValue;

	@Column(name = "created_at", nullable = false)
	@CreatedDate
	private LocalDateTime createdDate;

	@Column(name="response_status")
	private int responseStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "execution_id")
	private Execution execution;

}
