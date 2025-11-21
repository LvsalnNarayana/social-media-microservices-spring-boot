package com.example.social_media.post_service.repository;

import com.example.social_media.post_service.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, UUID> {

    // Fetch all posts by a specific author
    List<PostEntity> findAllByAuthorId(String authorId);
}
