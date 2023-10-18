package com.navi.assignment2.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.Exception.NewsApiErrorException;
import com.navi.assignment2.contract.response.ValidSourceResponse;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.RecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration(classes = {SourceServiceImpl.class})
@ExtendWith(SpringExtension.class)
class SourceServiceImplTest {
    @MockBean
    private EndpointRepository endpointRepository;

    @MockBean
    private RecordRepository recordRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private SourceServiceImpl sourceServiceImpl;

    @Test
    void shouldBeAbleToGetValidSources() throws Exception {
        Record record = new Record();
        record.setId(1L);
        record.setRequest("Request");
        record.setResponse("Response");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");
        when(recordRepository.save(Mockito.<Record>any())).thenReturn(record);
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<ValidSourceResponse>>any(), (Object[]) any()))
                .thenReturn(new ValidSourceResponse());
        assertTrue(sourceServiceImpl.getValidSources("GB", "Category").isPresent());
        verify(recordRepository).save(Mockito.<Record>any());
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<ValidSourceResponse>>any(),
                (Object[]) any());
    }

    @Test
    void shouldThrowUnableToSaveRecordErrorForGetValidSources() throws Exception {
        when(recordRepository.save(Mockito.<Record>any())).thenThrow(new NewsApiErrorException("An error occurred"));
        when(
                restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<ValidSourceResponse>>any(), (Object[]) any()))
                .thenReturn(new ValidSourceResponse());
        assertThrows(DatabaseErrorException.class, () -> sourceServiceImpl.getValidSources("GB", "Category"));
        verify(recordRepository).save(Mockito.<Record>any());
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<ValidSourceResponse>>any(),
                (Object[]) any());
    }

    @Test
    void shouldThrowNewsApiErrorForGetValidSources() throws Exception {
        when(
                restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<ValidSourceResponse>>any(), (Object[]) any()))
                .thenReturn(null);
        assertThrows(NewsApiErrorException.class, () -> sourceServiceImpl.getValidSources("GB", "Category"));
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<ValidSourceResponse>>any(),
                (Object[]) any());
    }
}

