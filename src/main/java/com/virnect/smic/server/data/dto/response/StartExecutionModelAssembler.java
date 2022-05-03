package com.virnect.smic.server.data.dto.response;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.service.api.DeviceRestController;
import com.virnect.smic.server.service.api.ExecutionRestController;

@Component
@Getter @Setter
public class StartExecutionModelAssembler {

	public StartExecutionResource withModel(StartExecutionResource param){
		StartExecutionResource resource = param;
		resource.add(linkTo(ExecutionRestController.class)
			.withRel("execution"));
		resource.add(linkTo(DeviceRestController.class)
			.withRel("device"));
		return resource;
	}

	public StartExecutionResource withoutModel(Exception e){
		StartExecutionResource resource = StartExecutionResource.builder().build();

		//resource.add(linkTo(ExecutionRestController.class).withRel("search-list"));

		if(e instanceof DuplicatedRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class)
				.slash("latest")
				.withRel("search-latest"));
			resource.add(linkTo(ExecutionRestController.class)
				.slash(((DuplicatedRunningExecutionException)e).getExecutionId())
				.withRel("search"));
			// resource.add(linkTo(ExecutionRestController.class)
			// 	.slash(((DuplicatedRunningExecutionException)e).getExecutionId())
			// 	.withRel("stop"));
		}
		return resource;
	}
}
