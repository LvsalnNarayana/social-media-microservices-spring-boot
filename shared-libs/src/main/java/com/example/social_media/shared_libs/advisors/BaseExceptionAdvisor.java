package com.example.social_media.shared_libs.advisors;

import com.example.social_media.shared_libs.exceptions.*;
import com.example.social_media.shared_libs.models.ErrorResponseModel;
import com.example.social_media.shared_libs.models.MasterResponse;
import com.example.social_media.shared_libs.models.Status;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class BaseExceptionAdvisor {

    private static final Logger log = LoggerFactory.getLogger(BaseExceptionAdvisor.class);

    // -----------------------------------------------------------
    // BadRequest (400)
    // -----------------------------------------------------------
    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleBadRequest(BadRequest ex) {

        log.warn("Bad Request: {}", ex.getMessage());

        Status status = Status.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(HttpStatus.BAD_REQUEST.getReasonPhrase()).helperMessage("").build();

        return buildErrorResponse(status, ex.getMessage());
    }

    // -----------------------------------------------------------
    // UnAuthorized (401)
    // -----------------------------------------------------------
    @ExceptionHandler(UnAuthorized.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleUnAuthorized(UnAuthorized ex) {

        log.warn("UnAuthorized: {}", ex.getMessage());

        Status status = Status.builder().statusCode(HttpStatus.UNAUTHORIZED.value()).message(HttpStatus.UNAUTHORIZED.getReasonPhrase()).helperMessage("").build();

        return buildErrorResponse(status, ex.getMessage());
    }

    // -----------------------------------------------------------
    // Not Found (404)
    // -----------------------------------------------------------
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleNotFound(NotFound ex) {

        log.warn("Not Found: {}", ex.getMessage());

        Status status = Status.builder().statusCode(HttpStatus.NOT_FOUND.value()).message(HttpStatus.NOT_FOUND.getReasonPhrase()).helperMessage("").build();

        return buildErrorResponse(status, ex.getMessage());
    }

    // -----------------------------------------------------------
    // Conflict (409) — includes DB unique constraint violations
    // -----------------------------------------------------------
    @ExceptionHandler({Conflict.class, DataIntegrityViolationException.class})
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleConflict(Exception ex) {

        String message = ex.getMessage();

        String root = ex.getMessage().toLowerCase();

        // Clean readable messages for DB errors
        if (root.contains("email")) {
            message = "Email already exists";
        } else if (root.contains("username")) {
            message = "Username already exists";
        } else if (root.contains("unique") || root.contains("duplicate")) {
            message = "Duplicate value found";
        }

        log.warn("Conflict: {}", message);

        Status status = Status.builder().statusCode(HttpStatus.CONFLICT.value()).message(HttpStatus.CONFLICT.getReasonPhrase()).helperMessage("").build();

        return buildErrorResponse(status, message);
    }

    // -----------------------------------------------------------
// Validation Errors (400) — For @Valid/@Validated on @RequestBody
// -----------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage())
        );

        log.warn("Validation failed: {}", fieldErrors);

        Status status = Status.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .helperMessage("Review field constraints (e.g., @NotNull, @NotBlank)").build();

        return buildErrorResponse(status, "Validation failed", fieldErrors);
    }

    // Optional Companion: For service-layer IllegalArgument (e.g., null payloads)
// -----------------------------------------------------------
// Argument Errors (400) — Generic IllegalArgument from business logic
// -----------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Argument error: {}", ex.getMessage());

        Status status = Status.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .helperMessage("Invalid input—check parameters").build();

        return buildErrorResponse(status, ex.getMessage());
    }

    // -----------------------------------------------------------
    // Service Exception (500)
    // -----------------------------------------------------------
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleServiceException(ServiceException ex) {

        Status status = Status.builder().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).helperMessage("").build();

        // Change here
        return buildErrorResponse(status, ex.getMessage());
    }

    // -----------------------------------------------------------
    // Unrecognized Property Exception (400)
    // -----------------------------------------------------------
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {
        String field = ex.getPropertyName();
        Status status = Status.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(HttpStatus.BAD_REQUEST.getReasonPhrase()).helperMessage("").build();

        // Change here
        return buildErrorResponse(status, "Unknown field: " + field);
    }


    // -----------------------------------------------------------
    // JsonMappingException (400) — Broader than UnrecognizedProperty; catches type mismatches or invalid formats
    // Why: Prevents generic 500s from deserialization failures; common in social APIs with variable payloads (e.g., nested comments).
    // When: Thrown during @RequestBody binding if JSON structure doesn't match model (e.g., string where object expected).
    // Status: 400 (client error); customize with path details for debuggability.
    // -----------------------------------------------------------
    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleJsonMappingException(JsonMappingException ex) {
        String path = ex.getPath() != null ? ex.getPath().toString() : "unknown field";
        String message = String.format("Invalid JSON structure at %s: %s", path, ex.getOriginalMessage());

        log.warn("JSON Mapping failed: {}", message);

        Status status = Status.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .helperMessage("Check payload format against API schema").build();

        return buildErrorResponse(status, message);
    }

    // -----------------------------------------------------------
    // InvalidFormatException (400) — Subtype of JsonMapping; for date/number format mismatches
    // Why: Enhances UX in date-heavy ops (e.g., UserModel.dateOfBirth parsing); avoids exposing raw Jackson errors.
    // When: During deserialization of @JsonFormat fields (e.g., "2025-13-01" invalid month).
    // Status: 400; include expected vs. actual for self-healing clients.
    // -----------------------------------------------------------
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleInvalidFormatException(InvalidFormatException ex) {
        String message = String.format("Invalid format for %s: expected %s, got '%s'",
                ex.getTargetType(), ex.getMessage(), ex.getValue());

        log.warn("Invalid format: {}", message);

        Status status = Status.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .helperMessage("Use ISO-8601 for dates (e.g., 2025-11-19)").build();

        return buildErrorResponse(status, message);
    }


    // -----------------------------------------------------------
    // PSQLException (500) — Postgres-specific; for SQL state errors
    // Why: Exposes Postgres quirks (e.g., lock timeouts); masks SQL for security while logging full trace.
    // When: Query execution fails (e.g., deadlock in concurrent user updates).
    // Status: 500; categorize by SQLState for ops dashboards.
    // -----------------------------------------------------------
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handlePSQLException(PSQLException ex) {
        String sqlState = ex.getSQLState();
        String message = switch (sqlState) {
            case "40P01" -> "Deadlock detected—retry operation";  // Postgres deadlock
            case "08006" -> "Database connection lost";  // Connection failure
            default -> "PostgreSQL query failed: " + ex.getMessage();
        };

        log.error("PSQL error (state {}): {}", sqlState, message, ex);

        Status status = Status.builder()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .helperMessage("Transient DB issue; exponential backoff recommended").build();

        return buildErrorResponse(status, message);
    }

    // -----------------------------------------------------------
    // ConstraintViolationException (409) — JPA validation on persist
    // Why: Handles @Column constraints beyond your current integrity handler; prevents partial saves.
    // When: Entity violates DB rules (e.g., bio > 255 chars in Postgres).
    // Status: 409; collect violations for detailed feedback.
    // -----------------------------------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getPropertyPath().toString(), v.getMessage()), HashMap::putAll);

        log.warn("DB constraint violation: {}", violations);

        Status status = Status.builder()
                .statusCode(HttpStatus.CONFLICT.value())
                .message(HttpStatus.CONFLICT.getReasonPhrase())
                .helperMessage("Adjust entity to meet DB constraints").build();

        return buildErrorResponse(status, "Entity constraints violated", violations);
    }


    // -----------------------------------------------------------
    // AsyncRequestTimeoutException (408) — For @Async or WebFlux timeouts
    // Why: Handles non-blocking ops (e.g., background bio analysis); promotes resilience.
    // When: Long-running request exceeds timeout (e.g., heavy user query).
    // Status: 408; add retry-after header.
    // -----------------------------------------------------------
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public ResponseEntity<MasterResponse<ErrorResponseModel>> handleAsyncTimeout(AsyncRequestTimeoutException ex) {
        String message = "Request timed out—operation may still be processing asynchronously";

        log.warn("Async timeout: {}", message);

        Status status = Status.builder()
                .statusCode(HttpStatus.REQUEST_TIMEOUT.value())
                .message(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase())
                .helperMessage("Retry in 30s or poll status endpoint").build();

        // New: Inject custom headers for client guidance (e.g., RFC 7231 retry semantics)
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", "30");  // Seconds; could dynamize via config

        return buildErrorResponse(status, message, null /* no details */, headers);
    }

    // -----------------------------------------------------------
    // Unified Utility: Core builder for error responses with headers & details
    // Why: Centralizes flexibility—handlers specify headers/details without ResponseEntity churn.
    // Params: details nullable (for simple messages); headers nullable (defaults to empty).
    // -----------------------------------------------------------
    private ResponseEntity<MasterResponse<ErrorResponseModel>> buildErrorResponse(
            Status status,
            String message,
            Object details,
            HttpHeaders headers) {

        ErrorResponseModel errorResponse = ErrorResponseModel.builder()
                .message(message)
                .timestamp(Instant.now())
                .details(details != null ? details : new HashMap<>())  // Default empty map if null
                .build();

        MasterResponse<ErrorResponseModel> response = MasterResponse.<ErrorResponseModel>builder()
                .status(status)
                .data(errorResponse)
                .build();

        // New: Merge provided headers with defaults; ensures extensibility
        if (headers == null) {
            headers = new HttpHeaders();
        }

        return ResponseEntity.status(status.getStatusCode())
                .headers(headers)
                .body(response);
    }

    // -----------------------------------------------------------
    // Convenience Overload: Simple message (no details, no headers)
    // -----------------------------------------------------------
    private ResponseEntity<MasterResponse<ErrorResponseModel>> buildErrorResponse(Status status, String message) {
        return buildErrorResponse(status, message, null, null);
    }

    // -----------------------------------------------------------
    // Convenience Overload: With details (no headers)
    // -----------------------------------------------------------
    private ResponseEntity<MasterResponse<ErrorResponseModel>> buildErrorResponse(Status status, String message, Object details) {
        return buildErrorResponse(status, message, details, null);
    }
}
