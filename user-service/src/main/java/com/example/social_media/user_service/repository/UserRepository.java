package com.example.social_media.user_service.repository;

import com.example.social_media.user_service.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  Page<UserEntity> findByUserNameContainingIgnoreCase(String userName, Pageable pageable);
}
