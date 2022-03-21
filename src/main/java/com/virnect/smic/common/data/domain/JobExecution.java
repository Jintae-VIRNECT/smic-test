package com.virnect.smic.common.data.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "job_execution")
public class JobExecution extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "job_execution_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private ExecutionMode mode;

	@Enumerated(EnumType.STRING)
	private ExecutionStatus status;

	@OneToMany(mappedBy = "id")
	private List<TaskExecution> taskExecutions = new ArrayList<>();


	@Builder
	public JobExecution(ExecutionMode executionMode){
		this.status = ExecutionStatus.STARTED;
		this.mode = executionMode;
	}

	public boolean isRunning() {
		return super.getCreatedDate() != null && super.getStopedDate() == null;
	}

	public boolean isStopping() {
		return status == status.STOPPING;
	}

	@Override
	public String toString() {
		return "JobExecution{" +
			"id=" + id +
			", status=" + status +
			", taskExecutions=" + taskExecutions +
			'}';
	}
}
