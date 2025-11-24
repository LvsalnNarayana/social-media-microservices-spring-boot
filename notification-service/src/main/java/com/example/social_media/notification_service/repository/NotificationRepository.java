package com.example.social_media.notification_service.repository;

import com.example.social_media.notification_service.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    // Fetch notifications for a user (recipient)
    List<NotificationEntity> findAllByRecipientIdOrderByCreatedAtDesc(String recipientId);

    // Fetch unread notifications
    List<NotificationEntity> findAllByRecipientIdAndReadFalseOrderByCreatedAtDesc(String recipientId);

    // Count unread notifications
    long countByRecipientIdAndReadFalse(String recipientId);

    // Count all notifications
    long countByRecipientId(String recipientId);

    // Delete all notifications for a user
    void deleteAllByRecipientId(String recipientId);
}
