package com.example.social_media.user_service.models;

import com.example.social_media.shared_libs.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserModel extends BaseModel {

	@NotNull(message = "User ID cannot be null")
	private UUID id;

	private String userName;

	@Email(message = "Invalid email format")
	private String email;

	@Size(
		  min = 8,
		  message = "Password must be at least 8 characters long"
	)
	private String password;

	private String firstName;

	private String lastName;

	@Size(
		  max = 255,
		  message = "Bio cannot exceed 255 characters"
	)
	private String bio;

	private String profilePictureUrl;

	private String coverPhotoUrl;

	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}

