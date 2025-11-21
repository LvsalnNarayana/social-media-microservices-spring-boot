package com.example.social_media.comment_service.repository;

import com.example.social_media.comment_service.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, UUID> {

    // 🔹 Fetch all comments under a post
    List<CommentEntity> findByPostIdOrderByCreatedAtAsc(UUID postId);

    @Query(value = """
        SELECT *
        FROM comment.comment 
        WHERE reply_comment_ids @> CAST(('["' || CAST(:parentCommentId AS text) || '"]') AS jsonb)
        ORDER BY created_at ASC
        """,
            nativeQuery = true)
    List<CommentEntity> findReplies(UUID parentCommentId);


    // 🔹 Fetch comments by author
    List<CommentEntity> findByAuthorId(String authorId);

    // 🔹 Count comments for a post
    int countByPostId(UUID postId);
}
