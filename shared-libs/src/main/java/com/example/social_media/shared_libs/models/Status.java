package com.example.social_media.shared_libs.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Status extends BaseModel {
	@JsonProperty(
		  value = "status_code",
		  required = true
	)
	private int statusCode;
	@JsonProperty(
		  value = "message",
		  defaultValue = "",
		  required = true
	)
	private String message;
	@JsonProperty(
		  value = "helper_message",
		  required = false
	)
	private String helperMessage;
}
