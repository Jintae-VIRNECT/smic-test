package com.virnect.smic.server.data.dto.response;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.error.DuplicatedRunningExecutionException;
import com.virnect.smic.server.service.api.ExecutionRestController;

@Component
@Getter @Setter
public class ExecutionModelAssembler extends
	RepresentationModelAssemblerSupport<Execution, StartExecutionResource> {

	public ExecutionModelAssembler(Class<?> controllerClass, Class<StartExecutionResource> resourceType) {
		super(controllerClass, resourceType);
	}

	public ExecutionModelAssembler() {
		super(ExecutionRestController.class, StartExecutionResource.class);
	}

	@Override
	public StartExecutionResource toModel(Execution entity) {
		StartExecutionResource resource = StartExecutionResource.builder()
			.executionId(entity.getId())
			.executionStatus(entity.getExecutionStatus())
			.createdDate(entity.getCreatedDate())
			.build();
		Link selfLink = linkTo(ExecutionRestController.class).slash(resource.getExecutionId()).withSelfRel();
		resource.add(selfLink);
		resource.add(linkTo(ExecutionRestController.class)
			.slash(resource.getExecutionId()).slash("stop")
			.withRel("stop"));
		return resource;
	}

	public StartExecutionResource withoutModel(Exception e){
		StartExecutionResource resource = StartExecutionResource.builder().build();
		Link selfLink = linkTo(ExecutionRestController.class).slash("start").withSelfRel();
		resource.add(selfLink);

		if(e instanceof DuplicatedRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class)
				.slash(((DuplicatedRunningExecutionException)e).getExecutionId())
				.withRel("search"));
			resource.add(linkTo(ExecutionRestController.class)
				.slash(((DuplicatedRunningExecutionException)e).getExecutionId()).slash("stop")
				.withRel("stop"));
		}
		return resource;
	}
}