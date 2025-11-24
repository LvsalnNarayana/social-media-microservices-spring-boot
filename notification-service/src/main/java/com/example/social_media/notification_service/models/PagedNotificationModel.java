package com.example.social_media.notification_service.models;

import com.example.social_media.shared_libs.schema.PagedResponseSchema;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
public class PagedNotificationModel<T> extends PagedResponseSchema<T> {

}
