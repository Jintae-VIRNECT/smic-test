package com.virnect.smic.server.data.dto.response.assembler;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.common.data.domain.Execution;
import com.virnect.smic.server.data.dto.response.SearchExecutionResource;
import com.virnect.smic.server.data.error.exception.NoRunningExecutionException;
import com.virnect.smic.server.data.error.exception.NoSuchExecutionException;
import com.virnect.smic.server.service.api.ExecutionRestController;

@Component
@Getter @Setter
public class SearchExecutionModelAssembler extends
	RepresentationModelAssemblerSupport<Execution, SearchExecutionResource> {

	public SearchExecutionModelAssembler(Class<?> controllerClass, Class<SearchExecutionResource> resourceType) {
		super(controllerClass, resourceType);
	}

	public SearchExecutionModelAssembler() {
		super(ExecutionRestController.class, SearchExecutionResource.class);
	}

	@Override
	public SearchExecutionResource toModel(Execution entity) {
		return SearchExecutionResource.builder()
											.executionId(entity.getId())
											.executionStatus(entity.getExecutionStatus())
											.createdDate(entity.getUpdatedDate())
											.updatedDate(entity.getUpdatedDate())
											.build();
	}

	public SearchExecutionResource withoutModel(long executionId, Exception e){
		SearchExecutionResource resource = SearchExecutionResource.builder().build();

		//resource.add(linkTo(ExecutionRestController.class).withRel("search-list"));

		if(e instanceof NoRunningExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
			resource.add(linkTo(ExecutionRestController.class).slash( executionId).withRel("search"));
		} else if(e instanceof NoSuchExecutionException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		}
		return resource;
	}

	@Override
	public CollectionModel<SearchExecutionResource> toCollectionModel(
		Iterable<? extends Execution> entities
	) {
		return super.toCollectionModel(entities);
	}
}
