package com.virnect.smic.server.data.dto.response;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.common.data.domain.Order;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;
import com.virnect.smic.server.service.api.ExecutionRestController;
import com.virnect.smic.server.service.api.OrderRestController;

@Component
@Getter @Setter
public class OrderModelAssembler extends
	RepresentationModelAssemblerSupport<Order, OrderResource> {

	@Autowired
	private  ModelMapper modelMapper;

	public OrderModelAssembler(Class<?> controllerClass
		, Class<OrderResource> resourceType) {
		super(controllerClass, resourceType);
	}

	public OrderModelAssembler() {
		super(OrderRestController.class, OrderResource.class);
	}


	@Override
	public OrderResource toModel(Order entity) {
		OrderResource resource = modelMapper.map(entity, OrderResource.class);
		resource.setExecutionId(entity.getExecution().getId());

		Link selfLink = linkTo(OrderRestController.class).slash(resource.getId()).withSelfRel();
		resource.add(selfLink);

		return resource;
	}

	public OrderResource withoutModel(Exception e){
		OrderResource resource = OrderResource.builder().build();

		if(e instanceof NoSuchExecutionException){
			//resource.add(linkTo(ExecutionRestController.class).withRel("search-list"));
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		} else if (e instanceof NoRunningExecutionException) {
			//resource.add(linkTo(ExecutionRestController.class).withRel("search-list"));
			resource.add(linkTo(ExecutionRestController.class).slash("start").withRel("start"));
		}

		return resource;
	}
}
