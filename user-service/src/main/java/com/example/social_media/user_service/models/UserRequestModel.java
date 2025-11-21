package com.example.social_media.user_service.models;

import com.example.social_media.shared_libs.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserRequestModel extends BaseModel {

  @NotNull(message = "Username cannot be null")
  private String userName;

  @Email(message = "Invalid email format")
  @NotNull(message = "Email cannot be null")
  private String email;

  @NotNull(message = "Password cannot be null")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @NotNull(message = "First name cannot be null")
  private String firstName;

  @NotNull(message = "Last name cannot be null")
  private String lastName;

  @Past(message = "Date of birth must be in the past")
  @JsonFormat(pattern = "dd-MM-yyyy")
  private LocalDate dateOfBirth;
}
