package com.example.social_media.notification_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications", schema = "post")
@EntityListeners(AuditingEntityListener.class)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "recipient_id", nullable = false)
    private String recipientId;       // who receives notification

    @NotNull
    @Column(name = "sender_id", nullable = false)
    private String senderId;          // who triggers notification (post author, commenter, etc.)

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;           // notification text

    @NotNull
    @Column(name = "type", nullable = false)
    private String type;              // LIKE, COMMENT, FOLLOW, MENTION, SYSTEM, etc.

    @Column(name = "is_read", nullable = false)
    private boolean read;             // unread flag

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    private Map<String, Object> metadata; // extra info: {"postId":"...", "commentId":"..."}

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
