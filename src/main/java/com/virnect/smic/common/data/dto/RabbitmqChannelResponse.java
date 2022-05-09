package com.virnect.smic.common.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitmqChannelResponse {
	private  String name;

	@JsonProperty("idle_since")
	private  String idleSince;

	//@JsonIgnore
	//private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

	//public void setIdleSince(String idleSince) {
	//	this.idleSince = LocalDateTime.parse(idleSince, formatter);
	//}

}
