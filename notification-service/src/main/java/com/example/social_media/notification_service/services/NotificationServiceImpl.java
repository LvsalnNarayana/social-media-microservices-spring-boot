package com.example.social_media.notification_service.services;

import com.example.social_media.notification_service.entity.NotificationEntity;
import com.example.social_media.notification_service.mappers.NotificationModelMapper;
import com.example.social_media.notification_service.models.NotificationRequestModel;
import com.example.social_media.notification_service.models.NotificationResponseModel;
import com.example.social_media.notification_service.repository.NotificationRepository;
import com.example.social_media.notification_service.services.interfaces.INotificationService;
import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationServiceImpl extends BaseService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationModelMapper mapper;

    public NotificationServiceImpl(
            RestClientUtility restClient,
            NotificationRepository notificationRepository,
            NotificationModelMapper mapper
    ) {
        super(restClient);
        this.notificationRepository = notificationRepository;
        this.mapper = mapper;
    }

    // =========================================================================
    // INTERNAL UTILITY: SAFE UUID PARSING
    // =========================================================================
    private UUID parseUuidOrThrow(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new BadRequest("Invalid UUID format: " + id);
        }
    }

    private NotificationEntity getOrThrow(UUID uuid) {
        return notificationRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found: " + uuid));
    }

    // =========================================================================
    // CREATE
    // =========================================================================
    @Override
    public NotificationResponseModel createNotification(NotificationRequestModel request) {

        NotificationEntity entity = mapper.toEntity(request);

        NotificationEntity saved = notificationRepository.save(entity);

        log.info("Notification created for user={}, type={}", saved.getRecipientId(), saved.getType());

        return mapper.toResponse(saved);
    }

    @Override
    public NotificationResponseModel createNotificationFromEvent(Object event) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // =========================================================================
    // GET
    // =========================================================================
    @Override
    public List<NotificationResponseModel> getNotificationsForUser(String userId) {
        List<NotificationEntity> list =
                notificationRepository.findAllByRecipientIdOrderByCreatedAtDesc(userId);

        return list.stream().map(mapper::toResponse).toList();
    }

    @Override
    public NotificationResponseModel getNotificationById(String notificationId) {

        UUID uuid = parseUuidOrThrow(notificationId);

        NotificationEntity entity = getOrThrow(uuid);

        return mapper.toResponse(entity);
    }

    @Override
    public List<NotificationResponseModel> getUnreadNotificationsForUser(String userId) {

        List<NotificationEntity> list =
                notificationRepository.findAllByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId);

        return list.stream().map(mapper::toResponse).toList();
    }

    // =========================================================================
    // UPDATE
    // =========================================================================
    @Override
    public void markAsRead(String notificationId) {

        UUID uuid = parseUuidOrThrow(notificationId);

        NotificationEntity entity = getOrThrow(uuid);

        if (!entity.isRead()) {
            entity.setRead(true);
            notificationRepository.save(entity);
        }

        log.info("Marked notification={} as READ", uuid);
    }

    @Override
    public void markAllAsRead(String userId) {

        List<NotificationEntity> unread =
                notificationRepository.findAllByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId);

        unread.forEach(n -> n.setRead(true));

        notificationRepository.saveAll(unread);

        log.info("Marked {} notifications as READ for user={}", unread.size(), userId);
    }

    // =========================================================================
    // DELETE
    // =========================================================================
    @Override
    public void deleteNotification(String notificationId) {

        UUID uuid = parseUuidOrThrow(notificationId);

        if (!notificationRepository.existsById(uuid)) {
            throw new EntityNotFoundException("Notification not found: " + uuid);
        }

        notificationRepository.deleteById(uuid);
    }

    @Override
    public void deleteAllForUser(String userId) {

        if (userId == null || userId.isBlank()) {
            throw new BadRequest("User ID cannot be empty");
        }

        notificationRepository.deleteAllByRecipientId(userId);

        log.info("Deleted all notifications for recipientId={}", userId);
    }

    // =========================================================================
    // COUNTS
    // =========================================================================
    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    public long getTotalCount(String userId) {
        return notificationRepository.countByRecipientId(userId);
    }
}
