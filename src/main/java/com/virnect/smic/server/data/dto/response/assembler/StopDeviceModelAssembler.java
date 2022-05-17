package com.virnect.smic.server.data.dto.response.assembler;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.Collections;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.server.data.dto.response.ExecutionResource;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
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
		resource.setDevices(Collections.EMPTY_LIST);
		if(e instanceof NoRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		}
		return resource;
	}
}
