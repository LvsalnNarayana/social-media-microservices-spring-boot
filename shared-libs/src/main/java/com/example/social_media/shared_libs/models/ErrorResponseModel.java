package com.example.social_media.shared_libs.models;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseModel extends BaseModel {
    private String message;
    private Instant timestamp;
    private Object details;
}
