package com.virnect.smic.common.data.dao;

import com.virnect.smic.common.data.domain.TaskExecution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskExecutionRepository extends JpaRepository<TaskExecution, Long> {
}
