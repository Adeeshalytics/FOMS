package com.adeeshalytics.foms.api;

import org.springframework.http.*;
import com.adeeshalytics.foms.service.ResourceNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
        var fields = new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", fields);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> badRequest(IllegalArgumentException exception) { return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", exception.getMessage(), Map.of()); }
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ApiError> conflict(IllegalStateException exception) { return response(HttpStatus.CONFLICT, "INVALID_STATE", exception.getMessage(), Map.of()); }
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException exception) { return response(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), Map.of()); }
    private ResponseEntity<ApiError> response(HttpStatus status, String code, String message, java.util.Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), code, message, fields));
    }
}
