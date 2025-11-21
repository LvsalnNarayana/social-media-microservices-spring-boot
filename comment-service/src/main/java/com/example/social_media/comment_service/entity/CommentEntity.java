package com.example.social_media.comment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "comment",
        indexes = {
                @Index(name = "idx_comment_post_id", columnList = "post_id"),
                @Index(name = "idx_comment_author_id", columnList = "author_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("is_deleted = false")  // Soft-delete filter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Linking comment to the post service
    @NotNull
    @Column(name = "post_id")
    private UUID postId;

    @NotNull
    @Column(name = "author_id")
    private String authorId;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    // Optional: list of media attachments in comments
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media_urls", columnDefinition = "json")
    private List<String> mediaUrls;

    // Replies stored as IDs of other comments (recursive structure)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reply_ids", columnDefinition = "json")
    private List<UUID> replyIds;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "is_edited")
    private boolean edited;

    // Moderation flags
    @Column(name = "is_flagged")
    private boolean flagged;

    @Column(name = "flagged_reason")
    private String flaggedReason;

    @Column(name = "is_pinned")
    private boolean pinned;

    // Soft delete for GDPR + audit logs
    @Column(name = "is_deleted")
    private boolean deleted;

    // Visibility: PUBLIC, FOLLOWERS, PRIVATE, RESTRICTED
    @Column(name = "visibility")
    private String visibility;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
