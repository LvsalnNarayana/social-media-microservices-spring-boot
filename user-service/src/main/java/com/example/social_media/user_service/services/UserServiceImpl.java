package com.example.social_media.user_service.services;

import com.example.social_media.shared_libs.exceptions.BadRequest;
import com.example.social_media.shared_libs.exceptions.InternalServerException;
import com.example.social_media.shared_libs.exceptions.NotFound;
import com.example.social_media.shared_libs.services.BaseService;
import com.example.social_media.shared_libs.utils.RestClientUtility;
import com.example.social_media.user_service.entity.UserEntity;
import com.example.social_media.user_service.models.PagedUserResponseModel;
import com.example.social_media.user_service.models.UserModel;
import com.example.social_media.user_service.models.UserRequestModel;
import com.example.social_media.user_service.models.UserResponseModel;
import com.example.social_media.user_service.repository.UserRepository;
import com.example.social_media.user_service.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl extends BaseService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    protected UserServiceImpl(RestClientUtility restClientUtility) {
        super(restClientUtility);
    }


    /**
     * Get user by ID
     */
    @Override
    public UserResponseModel getUserById(String userId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Invalid UUID format: " + userId);
        }

        UserEntity
                userEntity =
                userRepository.findById(uuid)
                        .orElseThrow(() -> new NotFound("User not found with id: " + userId));
        return UserResponseModel.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUserName())
                .email(userEntity.getEmail())
                .bio(userEntity.getBio())
                .profilePictureUrl(userEntity.getProfilePictureUrl())
                .coverPhotoUrl(userEntity.getCoverPhotoUrl())
                .dateOfBirth(userEntity.getDateOfBirth())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getCreatedAt())
                .build();
    }

    /**
     * Create a new user
     */
    @Override
    @Transactional
    public UserResponseModel createUser(UserRequestModel userData) {
        try {
            UserEntity
                    userEntity =
                    UserEntity.builder()
                            .userName(userData.getUserName())
                            .firstName(userData.getFirstName())
                            .lastName(userData.getLastName())
                            .email(userData.getEmail())
                            .password(userData.getPassword())
                            .dateOfBirth(userData.getDateOfBirth())
                            .build();

            userRepository.save(userEntity);
            return UserResponseModel.builder()
                    .id(userEntity.getId())
                    .firstName(userEntity.getFirstName())
                    .lastName(userEntity.getLastName())
                    .userName(userEntity.getUserName())
                    .email(userEntity.getEmail())
                    .bio(userEntity.getBio())
                    .profilePictureUrl(userEntity.getProfilePictureUrl())
                    .coverPhotoUrl(userEntity.getCoverPhotoUrl())
                    .dateOfBirth(userEntity.getDateOfBirth())
                    .createdAt(userEntity.getCreatedAt())
                    .updatedAt(userEntity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            throw new InternalServerException("Failed to create user: " + e.getMessage());
        }
    }

    /**
     * Update an existing user
     */
    @Override
    @Transactional
    public UserResponseModel updateUser(
            String userId,
            UserModel userData
    ) {
        UUID uuid;
        try {
            uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Invalid UUID format: " + userId);
        }

        UserEntity
                existingUser =
                userRepository.findById(uuid)
                        .orElseThrow(() -> new NotFound("User not found with id: " + userId));

        UserEntity
                updatedUser =
                existingUser.toBuilder()
                        .userName(userData.getUserName() != null
                                ? userData.getUserName()
                                : existingUser.getUserName())
                        .firstName(userData.getFirstName() != null
                                ? userData.getFirstName()
                                : existingUser.getFirstName())
                        .lastName(userData.getLastName() != null
                                ? userData.getLastName()
                                : existingUser.getLastName())
                        .bio(userData.getBio() != null
                                ? userData.getBio()
                                : existingUser.getBio())
                        .profilePictureUrl(userData.getProfilePictureUrl() != null
                                ? userData.getProfilePictureUrl()
                                : existingUser.getProfilePictureUrl())
                        .coverPhotoUrl(userData.getCoverPhotoUrl() != null
                                ? userData.getCoverPhotoUrl()
                                : existingUser.getCoverPhotoUrl())
                        .dateOfBirth(userData.getDateOfBirth() != null
                                ? userData.getDateOfBirth()
                                : existingUser.getDateOfBirth())
                        .updatedAt(LocalDateTime.now())              // update timestamp
                        .id(existingUser.getId())                    // preserve ID
                        .createdAt(existingUser.getCreatedAt())      // preserve creation time
                        .build();

        updatedUser = userRepository.save(updatedUser);

        return UserResponseModel.builder()
                .id(updatedUser.getId())
                .userName(updatedUser.getUserName())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .email(updatedUser.getEmail())
                .bio(updatedUser.getBio())
                .profilePictureUrl(updatedUser.getProfilePictureUrl())
                .coverPhotoUrl(updatedUser.getCoverPhotoUrl())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();
    }

    /**
     * Delete a user
     */
    @Transactional
    @Override
    public void deleteUser(String userId) {

        UUID uuid;
        try {
            uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Invalid UUID format: " + userId);
        }

        UserEntity user = userRepository.findById(uuid)
                .orElseThrow(() -> new NotFound("User not found with id: " + userId));

        userRepository.delete(user);
    }


    /**
     * List users with pagination
     */
    @Override
    public PagedUserResponseModel listUsers(
            String page,
            String size
    ) {
        Pageable pageable = PageRequest.of(
                Integer.parseInt(page),
                Integer.parseInt(size)
        );
        Page<UserEntity> userPage = userRepository.findAll(pageable);

        List<UserResponseModel>
                mappedUsers =
                userPage.stream()
                        .map(user -> UserResponseModel.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .bio(user.getBio())
                                .profilePictureUrl(user.getProfilePictureUrl())
                                .coverPhotoUrl((user.getCoverPhotoUrl()))
                                .dateOfBirth(user.getDateOfBirth())
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getUpdatedAt())
                                .build())
                        .toList();
        return PagedUserResponseModel.builder()
                .content(mappedUsers)
                .totalPages(userPage.getTotalPages())
                .totalElements(userPage.getTotalElements())
                .size(Integer.parseInt(size))
                .page(Integer.parseInt(page))
                .last(userPage.isLast())
                .first(userPage.isFirst())
                .build();
    }


    /**
     * Search users by name with pagination
     */
    @Override
    public List<UserModel> searchUserByName(
            String query,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size
        );
        Page<UserEntity> userPage = userRepository.findByUserNameContainingIgnoreCase(
                query,
                pageable
        );

        // TODO: map UserEntity -> UserModel
        userPage.stream()
                .map(user -> null) // Replace null with actual mapping
                .toList();

        return null;
    }
}
