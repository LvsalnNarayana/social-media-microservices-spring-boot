package com.example.social_media.reply_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
        name = "reply",
        indexes = {
                @Index(name = "idx_reply_post_id", columnList = "post_id"),
                @Index(name = "idx_reply_parent_comment_id", columnList = "parent_comment_id"),
                @Index(name = "idx_reply_author_id", columnList = "author_id")
        }
)
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("is_deleted = false")
public class ReplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ------------------------------
    // Parent Relationship
    // ------------------------------

    @NotNull(message = "postId is required")
    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @NotNull(message = "parentCommentId is required")
    @Column(name = "parent_comment_id", nullable = false)
    private UUID parentCommentId;

    // ------------------------------
    // Reply Attributes
    // ------------------------------

    @NotBlank(message = "authorId is required")
    @Column(name = "author_id", nullable = false)
    private String authorId;

    @NotBlank(message = "content cannot be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media_urls", columnDefinition = "json")
    private List<String> mediaUrls;

    // ------------------------------
    // Moderation + Engagement
    // ------------------------------

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "is_edited")
    private boolean edited;

    @Column(name = "is_flagged")
    private boolean flagged;

    @Column(name = "flagged_reason")
    private String flaggedReason;

    @Column(name = "is_deleted")
    private boolean deleted;

    // PUBLIC, FOLLOWERS, PRIVATE, etc.
    @Column(name = "visibility")
    private String visibility;

    // ------------------------------
    // Audit
    // ------------------------------

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
