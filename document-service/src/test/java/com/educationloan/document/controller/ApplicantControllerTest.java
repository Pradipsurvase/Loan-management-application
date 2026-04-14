package com.educationloan.document.controller;
import com.educationloan.document.entity.ApplicantEntity;
import com.educationloan.document.repository.ApplicantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicantController.class)
class ApplicantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicantRepository applicantRepository;

    @Test
    void getApplicant_success() throws Exception {

        ApplicantEntity entity = new ApplicantEntity();
        entity.setId(1L);
        entity.setName("John");
        entity.setDob("1995-01-01");
        entity.setAadhaarNumber("123456789012");

        when(applicantRepository.findById(1L)).thenReturn(Optional.of(entity));
        mockMvc.perform(get("/applicants/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.dob").value("1995-01-01"))
                .andExpect(jsonPath("$.aadhaarNumber").value("123456789012"));
        verify(applicantRepository, times(1)).findById(1L);
    }

    @Test
    void getApplicant_notFound() throws Exception {
        when(applicantRepository.findById(1L))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/applicants/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Applicant not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
        verify(applicantRepository, times(1)).findById(1L);
    }

    @Test
    void getApplicant_repositoryCalledOnlyOnce() throws Exception {
        ApplicantEntity entity = new ApplicantEntity();
        entity.setId(2L);
        entity.setName("Test");

        when(applicantRepository.findById(2L)).thenReturn(Optional.of(entity));
        mockMvc.perform(get("/applicants/2")).andExpect(status().isOk());
        verify(applicantRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(applicantRepository);
    }
}