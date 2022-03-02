package com.virnect.smic.common.data.dao;

import com.virnect.smic.common.data.domain.JobExecution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {
}
