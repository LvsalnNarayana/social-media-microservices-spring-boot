package com.example.social_media.user_service.models;

import com.example.social_media.shared_libs.models.BaseModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseModel extends BaseModel {

  @NotNull(message = "User ID cannot be null")
  private final UUID id;

  @NotNull(message = "Username cannot be null")
  private final String userName;

  @Email(message = "Invalid email format")
  @NotNull(message = "Email cannot be null")
  private final String email;

  @NotNull(message = "First name cannot be null")
  private final String firstName;

  @NotNull(message = "Last name cannot be null")
  private final String lastName;

  private final String bio;
  private final String profilePictureUrl;
  private final String coverPhotoUrl;

  @Past(message = "Date of birth must be in the past")
  private final LocalDate dateOfBirth;

  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
}
