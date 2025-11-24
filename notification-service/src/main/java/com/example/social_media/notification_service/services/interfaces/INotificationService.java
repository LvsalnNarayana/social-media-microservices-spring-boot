package com.example.social_media.notification_service.services.interfaces;

import com.example.social_media.notification_service.models.NotificationRequestModel;
import com.example.social_media.notification_service.models.NotificationResponseModel;

import java.util.List;

public interface INotificationService {

    // --------------------------------------------------------
    // Create notifications (manual or event-driven)
    // --------------------------------------------------------
    NotificationResponseModel createNotification(NotificationRequestModel request);

    // For Kafka listeners or other domain events
    NotificationResponseModel createNotificationFromEvent(Object kafkaEvent);

    // --------------------------------------------------------
    // Retrieval operations
    // --------------------------------------------------------
    List<NotificationResponseModel> getNotificationsForUser(String userId);

    NotificationResponseModel getNotificationById(String notificationId);

    List<NotificationResponseModel> getUnreadNotificationsForUser(String userId);

    // --------------------------------------------------------
    // State updates (read, delete)
    // --------------------------------------------------------
    void markAsRead(String notificationId);

    void markAllAsRead(String userId);

    void deleteNotification(String notificationId);

    void deleteAllForUser(String userId);

    // --------------------------------------------------------
    // Analytics / counts
    // --------------------------------------------------------
    long getUnreadCount(String userId);

    long getTotalCount(String userId);
}
