package com.example.social_media.user_service.models;

import com.example.social_media.shared_libs.schema.PagedResponseSchema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class PagedUserResponseModel extends PagedResponseSchema<List<UserResponseModel>> {

}
