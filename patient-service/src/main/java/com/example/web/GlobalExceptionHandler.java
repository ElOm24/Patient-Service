package com.example.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), ex, req, false);
    }

    // Ошибки валидации @Valid (например пустое имя, неверный email и т.д.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Validation failed", req);
        body.put("details", fieldErrors);

        log.warn("Validation error on {} {}: {}", req.getMethod(), req.getRequestURI(), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Ловим всё остальное
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        // ВАЖНО: логируем stacktrace, иначе ты никогда не узнаешь причину 500
        log.error("Unhandled error on {} {}", req.getMethod(), req.getRequestURI(), ex);

        // На время отладки полезно вернуть реальное сообщение:
        // потом можно заменить message на "Unexpected error"
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex, req, true);
    }

    private ResponseEntity<Map<String, Object>> build(
            HttpStatus status,
            String message,
            Exception ex,
            HttpServletRequest req,
            boolean includeExceptionClass) {
        Map<String, Object> body = baseBody(status, message, req);
        if (includeExceptionClass) {
            body.put("exception", ex.getClass().getName());
        }
        return ResponseEntity.status(status).body(body);
    }

    private Map<String, Object> baseBody(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return body;
    }
}
