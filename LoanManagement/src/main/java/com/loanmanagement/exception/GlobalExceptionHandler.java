package com.loanmanagement.exception;

import com.loanmanagement.dto.ApiResponseDto;
import com.loanmanagement.enums.Status;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiResponseDto> handleInvalid(InvalidAmountException e) {
        return ResponseEntity.badRequest().body(
                new ApiResponseDto(e.getMessage(), Status.FAILED, LocalDate.now())
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ApiResponseDto(e.getMessage(), Status.FAILED, LocalDate.now())
        );
    }

    @ExceptionHandler(LoanDataException.class)
    public ResponseEntity<ApiResponseDto> handleLoanData(LoanDataException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponseDto(e.getMessage(), Status.FAILED, LocalDate.now())
        );
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidationException(ConstraintViolationException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "message", "Validation failed",
                        "status", "FAILED",
                        "date", LocalDate.now()
                ));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleGlobal(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponseDto("Internal Server Error", Status.FAILED, LocalDate.now())
        );
    }
}