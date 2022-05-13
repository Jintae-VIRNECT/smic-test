package com.virnect.smic.server.data.dto.response;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchExecutionException;
import com.virnect.smic.server.service.api.DeviceRestController;
import com.virnect.smic.server.service.api.ExecutionRestController;

@Component
@Getter @Setter
public class StopExecutionModelAssembler {

	public ExecutionResource withModel(ExecutionResource resource){

		resource.add(linkTo(ExecutionRestController.class)
			.withRel("execution"));
		resource.add(linkTo(DeviceRestController.class)
			.withRel("device"));
		resource.add(linkTo(ExecutionRestController.class).slash(resource.getExecutionId()).withSelfRel());

		return resource;
	}

	public ExecutionResource withoutModel(Long id, Exception e){
		ExecutionResource resource = ExecutionResource.builder().build();

		if(e instanceof NoRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
			resource.add(linkTo(ExecutionRestController.class).slash(id).withRel("search"));
		} else if(e instanceof NoSuchExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		}
		return resource;
	}
}
