package com.virnect.smic.server.data.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.common.data.domain.ExecutionStatus;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findByExecutionIdAndMacAddress(long executionId, String macAddress);

	List<Device> findByExecutionIdAndExecutionStatus(long id, ExecutionStatus started);

	Optional<Device> findByIdAndExecutionId(long id, long executionId);
}
