package com.virnect.smic.server.service.api;

import com.virnect.smic.server.data.dto.response.ApiResponse;
import com.virnect.smic.server.data.dto.response.TagValueListResponse;
import com.virnect.smic.server.service.application.TaskService;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
// @PropertySource("classpath:application.yml")
// @ConditionalOnProperty(prefix="server",name="daemon", havingValue= "false")
public class TaskRestController {
    private final TaskService taskService;
    private static final String TAG = TaskRestController.class.getSimpleName();

    private OpcUaClient client;
    @Operation(hidden = true)
    @GetMapping("{id}/tags/values")
   // @Operation(summary="lookup tag value of task", description = "lookup tag values which belongs to provided task id")
    public ResponseEntity<EntityModel<ApiResponse<TagValueListResponse>>> getTaskTagValues(
        @PathVariable(name = "id") Long id
    ){

        TagValueListResponse responseData = taskService.getTagValues(client, id);

        EntityModel<ApiResponse<TagValueListResponse>> model
			= EntityModel.of(new ApiResponse<>(responseData))
			    .add(linkTo(methodOn(this.getClass()).getTaskTagValues(id)).withRel("tag-values"));

        return ResponseEntity.ok(model);
    }
}
