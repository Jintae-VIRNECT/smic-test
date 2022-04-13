package com.virnect.smic.server.data.error;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

@JsonComponent
public class ErrorSerializer extends JsonSerializer<Errors> {
	@Override
	public void serialize(Errors errors, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartArray();
		errors.getFieldErrors().forEach(e->{
			try {
				gen.writeStartObject();
				gen.writeStringField("filed", e.getField());
				gen.writeStringField("objectName", e.getObjectName());
				gen.writeStringField("code", e.getCode());
				gen.writeStringField("defaultMessage", e.getDefaultMessage());
				Object rejectedValue = e.getRejectedValue();
				if(rejectedValue != null){
					gen.writeStringField("rejectedValue", (String)rejectedValue);
				}
				gen.writeEndObject();
			} catch (IOException el) {
				el.printStackTrace();
			}
		});

		errors.getGlobalErrors().forEach(e->{
			try {
				gen.writeStartObject();
				gen.writeStringField("objectName", e.getObjectName());
				gen.writeStringField("code", e.getCode());
				gen.writeStringField("defaultMessage", e.getDefaultMessage());

				gen.writeEndObject();
			} catch (IOException el) {
				el.printStackTrace();
			}
		});
		gen.writeEndArray();
	}
}
