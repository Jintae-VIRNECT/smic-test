package com.virnect.smic.common.data.domain;

import javax.persistence.CascadeType;
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
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "task_execution")
public class TaskExecution extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_execution_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private ExecutionStatus status;
	private long count;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "job_execution_id")
	private JobExecution jobExecution;

	// @OneToOne(cascade = CascadeType.ALL)
	// @JoinColumn(name="model_line_id")
	// private ModelLine modelLine;

	@Builder
	public TaskExecution (JobExecution jobExecution) {
		this.jobExecution = jobExecution;
		this.status = ExecutionStatus.STARTED;
	}

	@Override
	public String toString() {
		return "TaskExecution{" +
			"id=" + id +
			", status='" + status + '\'' +
			", count=" + count +
			", jobExecution=" + jobExecution +
			'}';
	}
}
