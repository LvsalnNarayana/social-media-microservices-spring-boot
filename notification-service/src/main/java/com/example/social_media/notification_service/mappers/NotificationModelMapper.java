package com.example.social_media.notification_service.mappers;

import com.example.social_media.notification_service.entity.NotificationEntity;
import com.example.social_media.notification_service.models.NotificationModel;
import com.example.social_media.notification_service.models.NotificationRequestModel;
import com.example.social_media.notification_service.models.NotificationResponseModel;
import org.springframework.stereotype.Component;

@Component
public class NotificationModelMapper {

    // =========================================================================
    // REQUEST → ENTITY
    // =========================================================================
    public NotificationEntity toEntity(NotificationRequestModel request) {
        if (request == null) return null;

        return NotificationEntity.builder()
                .recipientId(request.getRecipientId())
                .senderId(request.getSenderId())
                .message(request.getMessage())
                .type(request.getType())
                .metadata(request.getMetadata()) // optional
                .read(false) // always unread on creation
                .build();
    }

    // =========================================================================
    // ENTITY → RESPONSE
    // =========================================================================
    public NotificationResponseModel toResponse(NotificationEntity entity) {
        if (entity == null) return null;

        return NotificationResponseModel.builder()
                .id(entity.getId())
                .recipientId(entity.getRecipientId())
                .senderId(entity.getSenderId())
                .message(entity.getMessage())
                .type(entity.getType())
                .metadata(entity.getMetadata())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // =========================================================================
    // ENTITY → MODEL (internal representation)
    // =========================================================================
    public NotificationModel toModel(NotificationEntity entity) {
        if (entity == null) return null;

        return NotificationModel.builder()
                .id(entity.getId())
                .recipientId(entity.getRecipientId())
                .senderId(entity.getSenderId())
                .message(entity.getMessage())
                .type(entity.getType())
                .metadata(entity.getMetadata())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // =========================================================================
    // MODEL → ENTITY (internal use)
    // =========================================================================
    public NotificationEntity toEntity(NotificationModel model) {
        if (model == null) return null;

        return NotificationEntity.builder()
                .id(model.getId())
                .recipientId(model.getRecipientId())
                .senderId(model.getSenderId())
                .message(model.getMessage())
                .type(model.getType())
                .metadata(model.getMetadata())
                .read(model.isRead())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
