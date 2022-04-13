package com.virnect.smic.server.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.smic.common.data.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
