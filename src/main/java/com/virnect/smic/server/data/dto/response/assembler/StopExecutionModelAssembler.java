package com.virnect.smic.server.data.dto.response.assembler;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Collections;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchExecutionException;
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
		resource.setDevices(Collections.EMPTY_LIST);
		if(e instanceof NoRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
			resource.add(linkTo(ExecutionRestController.class).slash(id).withRel("search"));
		} else if(e instanceof NoSuchExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		}
		return resource;
	}
}
