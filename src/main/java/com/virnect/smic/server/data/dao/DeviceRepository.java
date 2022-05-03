package com.virnect.smic.server.data.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.smic.common.data.domain.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
	Optional<Device> findByExecutionIdAndMacAddress(long executionId, String macAddress);
}
