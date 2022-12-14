package com.virnect.smic.common.data.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
public class Execution extends  BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "execution_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private ExecutionStatus executionStatus;

	@JsonIgnore
	@OneToMany(mappedBy = "execution")
	private List<Order> orders;

	@JsonIgnore
	@OneToMany(mappedBy = "execution")
	private List<Device> devices;

	public Execution() {
		executionStatus = ExecutionStatus.STARTED;
	}

	@Override
	public String toString() {
		return "Execution{" +
			"id=" + id +
			", executionStatus=" + executionStatus +
			", orders=" + orders +
			", devices=" + devices +
			'}';
	}
}
