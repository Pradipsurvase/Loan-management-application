package com.loanmanagement.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loanmanagement.dto.LoanDto;
import com.loanmanagement.exception.InvalidAmountException;
import com.loanmanagement.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.InputStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LoanClientTest {

    @InjectMocks
    private LoanClient loanClient;

    @Mock
    private ObjectMapper mapper;

    @Test
    void getLoan_nullLoanId() {
        assertThrows(InvalidAmountException.class,
                () -> loanClient.getLoan(null));
    }

    @Test
    void getLoan_notFound() throws Exception {
        List<LoanDto> list = List.of();

        when(mapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(list);

        assertThrows(ResourceNotFoundException.class,
                () -> loanClient.getLoan(1L));
    }
}