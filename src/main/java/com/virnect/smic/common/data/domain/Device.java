package com.virnect.smic.common.data.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@NoArgsConstructor
public class Device extends BaseTimeEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "device_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private ExecutionStatus executionStatus;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "execution_id")
	private Execution execution;

	//@JsonIgnore
	//@OneToMany(mappedBy = "device")
	//private List<Order> orders;

	private String macAddress;

	private int sequenceNumber;

	public Device(String macAddress, Execution execution){
		this.macAddress = macAddress;
		this.execution = execution;
		this.executionStatus = ExecutionStatus.STARTED;
	}

}
