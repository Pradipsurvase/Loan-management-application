package com.loanmanagement.exception;

import com.loanmanagement.entity.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        BindingResult result = ex.getBindingResult();
        List<ApiError.FieldError> fieldErrors = result.getFieldErrors().stream()
                .map(fe -> ApiError.FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .rejectedValue(fe.getRejectedValue())
                        .build())
                .collect(Collectors.toList());
        log.error("Validation failed | path={} errors={}",
                request.getRequestURI(), fieldErrors);

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more fields failed validation",
                request,
                fieldErrors
        );
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found | path={} message={}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request,
                null
        );
    }
    @ExceptionHandler(LoanBusinessException.class)
    public ResponseEntity<ApiError> handleBusinessRule(LoanBusinessException ex, HttpServletRequest request) {
        log.error("Business rule violation | path={} message={}", request.getRequestURI(), ex.getMessage());
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Business Rule Violation",
                ex.getMessage(),
                request,
                null
        );
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidInput(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "Invalid request";
        Throwable cause = ex.getCause();
        while (cause != null) {
            String className = cause.getClass().getName();
            if (className.contains("InvalidFormatException")
                    && cause.getMessage().contains("Enum")) {
                message = "Enum type is incorrect";
                break;
            }
            else if (className.contains("InvalidFormatException")
                    || className.contains("MismatchedInputException")) {
                message = "Invalid data type";
                break;
            }
            cause = cause.getCause();
        }
        log.error("Invalid input error | path={} message={}",
                request.getRequestURI(), message, ex);
        return buildError(
                HttpStatus.BAD_REQUEST,
                "Invalid Request",
                message,
                request,
                null
        );
    }
    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeNotSupported(org.springframework.web.HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String supportedTypes = ex.getSupportedMediaTypes()
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        log.error("Unsupported media type | path={} provided={} supported={}", request.getRequestURI(), ex.getContentType(), supportedTypes);

        return buildError(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported Media Type",
                "Content-Type " + ex.getContentType() + " is not supported. Supported: " + supportedTypes,
                request,
                null
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred | path={}", request.getRequestURI(), ex);

        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Something went wrong",
                request,
                null
        );
    }
    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            List<ApiError.FieldError> fieldErrors) {

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}
