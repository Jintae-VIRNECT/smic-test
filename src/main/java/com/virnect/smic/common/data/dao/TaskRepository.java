package com.virnect.smic.common.data.dao;

import com.virnect.smic.common.data.domain.Task;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long>  {
}
