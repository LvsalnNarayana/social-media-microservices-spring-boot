package com.example.social_media.notification_service.web.api.interfaces;

import com.example.social_media.notification_service.models.NotificationRequestModel;
import com.example.social_media.notification_service.models.NotificationResponseModel;
import com.example.social_media.shared_libs.constants.BaseUrlConstants;
import com.example.social_media.shared_libs.models.MasterResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(BaseUrlConstants.BASE_URL + "/notifications")
public interface INotificationApi {

    // Create a new notification
    ResponseEntity<MasterResponse<NotificationResponseModel>> createNotification(
            NotificationRequestModel request
    );

    // Create notification from an event
    ResponseEntity<MasterResponse<NotificationResponseModel>> createNotificationFromEvent(
            Object event
    );

    // Fetch all notifications for a user
    ResponseEntity<MasterResponse<List<NotificationResponseModel>>> getNotificationsForUser(
            String userId
    );

    // Fetch unread notifications for a user
    ResponseEntity<MasterResponse<List<NotificationResponseModel>>> getUnreadNotificationsForUser(
            String userId
    );

    // Get a specific notification by ID
    ResponseEntity<MasterResponse<NotificationResponseModel>> getNotificationById(
            String notificationId
    );

    // Mark a specific notification as read
    ResponseEntity<MasterResponse<Void>> markAsRead(
            String notificationId
    );

    // Mark all notifications for a user as read
    ResponseEntity<MasterResponse<Void>> markAllAsRead(
            String userId
    );

    // Delete a single notification
    ResponseEntity<MasterResponse<Void>> deleteNotification(
            String notificationId
    );

    // Delete all notifications for a user
    ResponseEntity<MasterResponse<Void>> deleteAllForUser(
            String userId
    );

    // Count unread notifications for a user
    ResponseEntity<MasterResponse<Long>> getUnreadCount(
            String userId
    );

    // Count ALL notifications for a user
    ResponseEntity<MasterResponse<Long>> getTotalCount(
            String userId
    );
}
