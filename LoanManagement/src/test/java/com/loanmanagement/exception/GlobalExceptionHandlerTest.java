package com.loanmanagement.exception;

import com.loanmanagement.dto.LoanApplicationRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ExceptionTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Handle ResourceNotFoundException -> 404")
    void handleNotFound_Success() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Loan not found"));
    }

    @Test
    @DisplayName("Handle LoanBusinessException -> 400")
    void handleBusinessRule_Success() throws Exception {
        mockMvc.perform(get("/test/business-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Business Rule Violation"))
                .andExpect(jsonPath("$.message").value("Invalid business rule"));
    }

    @Test
    @DisplayName("Handle MethodArgumentNotValidException -> 400 with Field Errors")
    void handleValidation_Success() throws Exception {
        mockMvc.perform(get("/test/validation-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("courseFee"));
    }

    @Test
    @DisplayName("Handle HttpMessageNotReadableException (Enum Error) -> 400")
    void handleInvalidInput_EnumError() throws Exception {
        mockMvc.perform(get("/test/enum-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enum type is incorrect"));    }

    @Test
    @DisplayName("Handle HttpMessageNotReadableException (Generic) -> 400")
    void handleInvalidInput_GenericError() throws Exception {
        mockMvc.perform(get("/test/readable-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid request"));

    }
    @Test
    void handleGeneric_Success() throws Exception {
        mockMvc.perform(get("/test/generic-error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }
    @Test
    @DisplayName("Handle HttpMediaTypeNotSupportedException -> 415")
    void handleMediaType_Success() throws Exception {
        mockMvc.perform(get("/test/media-type-error"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.error").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.message", containsString("text/plain")));
    }

    @RestController
    static class ExceptionTestController {

        @GetMapping("/test/not-found")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Loan not found");
        }

        @GetMapping("/test/business-error")
        public void throwBusiness() {
            throw new LoanBusinessException("Invalid business rule");
        }

        @GetMapping("/test/validation-error")
        public void throwValidation() throws MethodArgumentNotValidException {
            LoanApplicationRequestDTO request = new LoanApplicationRequestDTO();
            BeanPropertyBindingResult errors = new BeanPropertyBindingResult(request, "loanRequest");
            errors.rejectValue("courseFee", "invalid", "Must be positive");

            throw new MethodArgumentNotValidException(
                    new MethodParameter(this.getClass().getDeclaredMethods()[2], -1), errors);
        }

        @GetMapping("/test/readable-error")
        public void throwReadable() {
            throw new HttpMessageNotReadableException("Required request body is missing",
                    new MockHttpInputMessage("".getBytes()));
        }

        @GetMapping("/test/enum-error")
        public void throwEnumError() {
            Exception cause = new com.fasterxml.jackson.databind.exc.InvalidFormatException(
                    null,
                    "Cannot deserialize Enum",
                    "INVALID_VALUE",
                    com.loanmanagement.enums.LoanType.class
            );

            throw new HttpMessageNotReadableException(
                    "Error",
                    cause,
                    new org.springframework.mock.http.MockHttpInputMessage("".getBytes())
            );
        }
        @GetMapping("/test/generic-error")
        public void throwGeneric() {
            throw new RuntimeException("Unexpected crash");
        }
        @GetMapping("/test/media-type-error")
        public void throwMediaType() throws org.springframework.web.HttpMediaTypeNotSupportedException {
            throw new org.springframework.web.HttpMediaTypeNotSupportedException(
                    org.springframework.http.MediaType.TEXT_PLAIN,
                    List.of(org.springframework.http.MediaType.APPLICATION_JSON)
            );
        }
    }
}