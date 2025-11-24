package com.example.social_media.notification_service.advisors;

import com.example.social_media.shared_libs.advisors.BaseExceptionAdvisor;
import com.example.social_media.shared_libs.exceptions.*;
import com.example.social_media.shared_libs.models.ErrorResponseModel;
import com.example.social_media.shared_libs.models.MasterResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestApiControllerAdvice extends BaseExceptionAdvisor {

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleBadRequest(BadRequest ex) {
        return super.handleBadRequest(ex);
    }

    @ExceptionHandler(UnAuthorized.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleUnAuthorized(UnAuthorized ex) {
        return super.handleUnAuthorized(ex);
    }

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleNotFound(NotFound ex) {
        return super.handleNotFound(ex);
    }

    // ---------- Conflict (409) ----------
    @ExceptionHandler({Conflict.class, DataIntegrityViolationException.class})
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleConflict(Exception ex) {
        return super.handleConflict(ex);
    }

    // ---------- Validation (400) ----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleValidationErrors(MethodArgumentNotValidException ex) {
        return super.handleValidationErrors(ex);
    }

    // ---------- Validation (400) ----------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleIllegalArgument(IllegalArgumentException ex) {
        return super.handleIllegalArgument(ex);
    }

    // ---------- Unrecognized Property Error (400) ----------
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {
        return super.handleUnrecognizedPropertyException(ex);
    }

    // ---------- Service Error (500) ----------
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleServiceException(ServiceException ex) {
        return super.handleServiceException(ex);
    }

    // ---------- Generic (500) ----------
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<MasterResponse<ErrorResponseModel>> handleGeneric(Exception ex) {
//		return super.handleGeneric(ex);
//	}
}
