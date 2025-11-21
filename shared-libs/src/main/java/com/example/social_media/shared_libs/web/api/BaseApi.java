package com.example.social_media.shared_libs.web.api;

import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.shared_libs.models.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseApi {

    public void validateHeaders() {
        // Logic to validate headers
    }

    public ResponseEntity<MasterResponse<Object>> buildSuccessResponse() {
        Status status = Status.builder().statusCode(HttpStatus.OK.value()).message(HttpStatus.OK.getReasonPhrase()).build();
        MasterResponse<Object> response = MasterResponse.builder().status(status).build();
        return ResponseEntity.ok(response);
    }
}
