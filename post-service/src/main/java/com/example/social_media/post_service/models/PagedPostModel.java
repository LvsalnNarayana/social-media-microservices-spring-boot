package com.example.social_media.post_service.models;

import com.example.social_media.shared_libs.schema.PagedResponseSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@Schema(description = "Generic paginated response")
public class PagedPostModel<T> extends PagedResponseSchema<T> {

}
