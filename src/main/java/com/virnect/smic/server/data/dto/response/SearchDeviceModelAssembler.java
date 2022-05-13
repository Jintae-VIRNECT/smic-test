package com.virnect.smic.server.data.dto.response;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import com.virnect.smic.common.data.domain.Device;
import com.virnect.smic.server.data.error.NoRunningExecutionException;
import com.virnect.smic.server.data.error.NoSuchDeviceException;
import com.virnect.smic.server.service.api.DeviceRestController;
import com.virnect.smic.server.service.api.ExecutionRestController;

@Component
@Getter @Setter
public class SearchDeviceModelAssembler extends
	RepresentationModelAssemblerSupport<Device, DeviceResource> {

	private final ModelMapper modelMapper;

	public SearchDeviceModelAssembler(ModelMapper modelMapper){
		super(DeviceRestController.class, DeviceResource.class);
		this.modelMapper = modelMapper;
	}

	// @Autowired
	// public SearchDeviceModelAssembler(
	// 	Class<?> controllerClass, Class<DeviceResource> resourceType,
	// 	ModelMapper modelMapper
	// ) {
	// 	super(controllerClass, resourceType);
	// 	this.modelMapper = modelMapper;
	// }

	@Override
	public DeviceResource toModel(Device device) {
		DeviceResource resource = modelMapper.map(device, DeviceResource.class);

		resource.add(linkTo(DeviceRestController.class)
			.withSelfRel());

		return resource;
	}

	public DeviceResource withoutModel(Exception e){
		DeviceResource resource = DeviceResource.builder().build();

		if(e instanceof NoSuchDeviceException){
			resource.add(linkTo(DeviceRestController.class).slash("all").withRel("devices-in-execution"));
		}
		return resource;
	}

	public ExecutionResource withExecution(ExecutionResource execution){

		execution.add(linkTo(DeviceRestController.class)
			.withSelfRel());

		return execution;
	}

	public ExecutionResource withoutExecution(Exception e){
		ExecutionResource resource = ExecutionResource.builder().build();

		if(e instanceof NoSuchElementException){
			resource.add(linkTo(ExecutionRestController.class).slash("latest").withRel("search-latest"));
		}
		return resource;
	}


}
