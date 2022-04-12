package com.virnect.smic.common.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.smic.common.data.domain.Execution;

public interface ExecutionRepository extends JpaRepository<Execution, Long> {
}
