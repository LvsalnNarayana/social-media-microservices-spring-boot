package com.example.social_media.reply_service.repository;

import com.example.social_media.reply_service.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, UUID> {

    // ---------------------------------------------------------
    // Fetch Queries
    // ---------------------------------------------------------

    // Get all replies under a comment
    List<ReplyEntity> findByParentCommentId(UUID parentCommentId);

    // Get all replies under a post
    List<ReplyEntity> findByPostId(UUID postId);

    // Get all replies by author
    List<ReplyEntity> findByAuthorId(String authorId);

    // ---------------------------------------------------------
    // Count Queries
    // ---------------------------------------------------------

    int countByParentCommentId(UUID parentCommentId);

    int countByPostId(UUID postId);

    int countByAuthorId(String authorId);
}
