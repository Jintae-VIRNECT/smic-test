package com.virnect.smic.server.data.dto.response;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.service.api.DeviceRestController;
import com.virnect.smic.server.service.api.ExecutionRestController;

@Component
@Getter @Setter
public class StopDeviceModelAssembler {

	public ExecutionResource withModel(ExecutionResource execution){

		execution.add(linkTo((methodOn(DeviceRestController.class))
			.getAllDevices(execution.getExecutionId()))
			.withRel("devices-in-execution"));

		execution.add(linkTo((methodOn(ExecutionRestController.class))
			.startExecution("all"))
			.withRel("start-execution"));


		return execution;
	}

	public ExecutionResource withoutModel(Exception e){
		ExecutionResource resource = ExecutionResource.builder().build();

		if(e instanceof NoRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		}
		return resource;
	}
}
