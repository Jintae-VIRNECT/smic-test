package com.virnect.smic.server.data.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.common.data.domain.ExecutionStatus;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {

	Optional<Execution> findFirstByOrderByCreatedDateDesc();

	Optional<Execution> findByIdAndExecutionStatus(long executionId, ExecutionStatus started);

	List<Execution> findByExecutionStatus(ExecutionStatus started);
}
