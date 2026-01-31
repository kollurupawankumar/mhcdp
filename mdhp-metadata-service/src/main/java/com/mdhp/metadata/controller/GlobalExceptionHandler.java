package com.mdhp.metadata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ handles @Valid validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 400);
        body.put("error", "VALIDATION_ERROR");
        body.put("path", request.getRequestURI());
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    // ✅ bad request (duplicate or invalid inputs)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return getResponseEntity(400, "BAD_REQUEST", ex, request, HttpStatus.BAD_REQUEST);

    }

    // ✅ default fallback
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return getResponseEntity(404, "NOT_FOUND", ex, request, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return getResponseEntity(500, "INTERNAL_SERVER_ERROR", ex, request, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    private static ResponseEntity<Map<String, Object>> getResponseEntity(int value, String INTERNAL_SERVER_ERROR, Exception ex, HttpServletRequest request, HttpStatus internalServerError) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("timestamp", Instant.now());
        requestMap.put("status", value);
        requestMap.put("error", INTERNAL_SERVER_ERROR);
        requestMap.put("message", ex.getMessage());
        requestMap.put("path", request.getRequestURI());
        return ResponseEntity.status(internalServerError).body(requestMap);
    }
}
