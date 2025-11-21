package com.example.social_media.post_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
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
@Table(name = "post", schema = "post")
@EntityListeners(AuditingEntityListener.class)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "author_id")
    private String authorId;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media_urls", columnDefinition = "json")
    private List<String> mediaUrls;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hashtags", columnDefinition = "json")
    private List<String> hashtags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "mentions", columnDefinition = "json")
    private List<String> mentions;

    // The IDs of comments from comment microservice
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "comment_ids", columnDefinition = "json")
    private List<UUID> commentIds;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "comments_count")
    private int commentsCount;

    @Column(name = "shares_count")
    private int sharesCount;

    @Column(name = "is_edited")
    private boolean edited;

    @Column(name = "is_pinned")
    private boolean pinned;

    @Column(name = "is_flagged")
    private boolean flagged;

    @Column(name = "flagged_reason")
    private String flaggedReason;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "location")
    private String location;

    @Column(name = "post_type")
    private String postType;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
}
