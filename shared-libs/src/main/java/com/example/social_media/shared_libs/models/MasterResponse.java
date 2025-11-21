package com.example.social_media.shared_libs.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * Master Response Model
 * @author Mobily Info Tech (MIT)
 *
 */
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterResponse <T> extends BaseModel{
	/** status  **/
	@JsonProperty(
		  value="status",
		  required=true,
		  defaultValue="",
		  access= Access.READ_WRITE)
	private Status status;

	/** status  **/
	@JsonProperty(
		  value="data",
		  required=false,
		  defaultValue="",
		  access= Access.READ_WRITE)
	private T data;
}
