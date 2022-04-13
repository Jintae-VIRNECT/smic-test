package com.virnect.smic.server.data.dto.response;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.smic.common.data.domain.Order;

@Getter
@NoArgsConstructor
public class OrderResource extends RepresentationModel {
	@JsonUnwrapped
	private Order order;

	public OrderResource(Order order){
		this.order = order;
	}
}
