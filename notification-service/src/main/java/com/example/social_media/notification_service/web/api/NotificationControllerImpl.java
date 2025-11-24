package com.example.social_media.notification_service.web.api;

import com.example.social_media.notification_service.models.NotificationRequestModel;
import com.example.social_media.notification_service.models.NotificationResponseModel;
import com.example.social_media.notification_service.services.interfaces.INotificationService;
import com.example.social_media.notification_service.web.api.interfaces.INotificationApi;
import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.shared_libs.models.Status;
import com.example.social_media.shared_libs.web.api.BaseApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl extends BaseApi implements INotificationApi {

    private final INotificationService notificationService;

    private <T> ResponseEntity<MasterResponse<T>> buildResponse(T data, HttpStatus status) {
        Status s = Status.builder()
                .statusCode(status.value())
                .message(status.getReasonPhrase())
                .build();

        MasterResponse<T> response = MasterResponse.<T>builder()
                .status(s)
                .data(data)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // =========================================================================
    // CREATE
    // =========================================================================
    @PostMapping
    @Override
    public ResponseEntity<MasterResponse<NotificationResponseModel>> createNotification(
            @RequestBody NotificationRequestModel request
    ) {
        NotificationResponseModel result = notificationService.createNotification(request);
        return buildResponse(result, HttpStatus.CREATED);
    }

    @PostMapping("/event")
    @Override
    public ResponseEntity<MasterResponse<NotificationResponseModel>> createNotificationFromEvent(
            @RequestBody Object event
    ) {
        NotificationResponseModel result = notificationService.createNotificationFromEvent(event);
        return buildResponse(result, HttpStatus.CREATED);
    }

    // =========================================================================
    // GET
    // =========================================================================
    @GetMapping("/user/{userId}")
    @Override
    public ResponseEntity<MasterResponse<List<NotificationResponseModel>>> getNotificationsForUser(
            @PathVariable String userId
    ) {
        List<NotificationResponseModel> result = notificationService.getNotificationsForUser(userId);
        return buildResponse(result, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/unread")
    @Override
    public ResponseEntity<MasterResponse<List<NotificationResponseModel>>> getUnreadNotificationsForUser(
            @PathVariable String userId
    ) {
        List<NotificationResponseModel> result =
                notificationService.getUnreadNotificationsForUser(userId);
        return buildResponse(result, HttpStatus.OK);
    }

    @GetMapping("/{notificationId}")
    @Override
    public ResponseEntity<MasterResponse<NotificationResponseModel>> getNotificationById(
            @PathVariable String notificationId
    ) {
        NotificationResponseModel result =
                notificationService.getNotificationById(notificationId);
        return buildResponse(result, HttpStatus.OK);
    }

    // =========================================================================
    // UPDATE
    // =========================================================================
    @PutMapping("/{notificationId}/read")
    @Override
    public ResponseEntity<MasterResponse<Void>> markAsRead(
            @PathVariable String notificationId
    ) {
        notificationService.markAsRead(notificationId);
        return buildResponse(null, HttpStatus.OK);
    }

    @PutMapping("/user/{userId}/read-all")
    @Override
    public ResponseEntity<MasterResponse<Void>> markAllAsRead(
            @PathVariable String userId
    ) {
        notificationService.markAllAsRead(userId);
        return buildResponse(null, HttpStatus.OK);
    }

    // =========================================================================
    // DELETE
    // =========================================================================
    @DeleteMapping("/{notificationId}")
    @Override
    public ResponseEntity<MasterResponse<Void>> deleteNotification(
            @PathVariable String notificationId
    ) {
        notificationService.deleteNotification(notificationId);
        return buildResponse(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user/{userId}")
    @Override
    public ResponseEntity<MasterResponse<Void>> deleteAllForUser(
            @PathVariable String userId
    ) {
        notificationService.deleteAllForUser(userId);
        return buildResponse(null, HttpStatus.NO_CONTENT);
    }

    // =========================================================================
    // COUNTS
    // =========================================================================
    @GetMapping("/user/{userId}/count/unread")
    @Override
    public ResponseEntity<MasterResponse<Long>> getUnreadCount(
            @PathVariable String userId
    ) {
        long count = notificationService.getUnreadCount(userId);
        return buildResponse(count, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/count")
    @Override
    public ResponseEntity<MasterResponse<Long>> getTotalCount(
            @PathVariable String userId
    ) {
        long count = notificationService.getTotalCount(userId);
        return buildResponse(count, HttpStatus.OK);
    }
}
