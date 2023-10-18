package com.navi.assignment2.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.contract.response.BillResponse;
import com.navi.assignment2.contract.response.ReportResponse;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.entity.Report;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.RecordRepository;
import com.navi.assignment2.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {MetricServiceImpl.class})
@ExtendWith(SpringExtension.class)
class MetricServiceImplTest {
    @MockBean
    private EndpointRepository endpointRepository;

    @Autowired
    private MetricServiceImpl metricServiceImpl;

    @MockBean
    private RecordRepository recordRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldBeAbleToGetBill() throws Exception {
        when(recordRepository.findByUrl(Mockito.<String>any())).thenReturn(Optional.of(new ArrayList<>()));
        Optional<BillResponse> actualBill = metricServiceImpl.getBill();
        assertTrue(actualBill.isPresent());
        BillResponse getResult = actualBill.get();
        assertEquals(0.0d, getResult.getEverything());
        assertEquals(0.0d, getResult.getTopHeadlines());
        assertEquals(0.0d, getResult.getSources());
        verify(recordRepository, atLeast(1)).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldThrowUnableToFetchRecordsErrorForGetBill() throws Exception {
        when(recordRepository.findByUrl(Mockito.<String>any())).thenReturn(Optional.empty());
        assertThrows(DatabaseErrorException.class, () -> metricServiceImpl.getBill());
        verify(recordRepository, atLeast(1)).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldThrowDatabaseErrorExceptionForGetBill() throws Exception {
        when(recordRepository.findByUrl(Mockito.<String>any()))
                .thenThrow(new DatabaseErrorException("An error occurred"));
        assertThrows(DatabaseErrorException.class, () -> metricServiceImpl.getBill());
        verify(recordRepository).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldBeAbleToGetReportForEmptyDatabase() throws Exception {
        when(recordRepository.findByUrl(Mockito.<String>any())).thenReturn(Optional.of(new ArrayList<>()));
        Optional<ReportResponse> actualReport = metricServiceImpl.getReport();
        assertTrue(actualReport.isPresent());
        assertEquals(3, actualReport.get().getReportList().size());
        verify(recordRepository, atLeast(1)).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldBeAbleToGetReportForDatabase() throws Exception {
        Record record = new Record();
        record.setId(1L);
        record.setRequest("/v2/everything");
        record.setResponse("/v2/everything");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");

        ArrayList<Record> recordList = new ArrayList<>();
        recordList.add(record);
        Optional<List<Record>> ofResult = Optional.of(recordList);
        when(recordRepository.findByUrl(Mockito.<String>any())).thenReturn(ofResult);
        Optional<ReportResponse> actualReport = metricServiceImpl.getReport();
        assertTrue(actualReport.isPresent());
        List<Report> reportList = actualReport.get().getReportList();
        assertEquals(3, reportList.size());
        Report getResult = reportList.get(2);
        assertEquals("https://example.org/example", getResult.getUrl());
        Report getResult2 = reportList.get(1);
        assertEquals("https://example.org/example", getResult2.getUrl());
        assertEquals(1L, getResult2.getTotalRequests().longValue());
        assertEquals(10.0d, getResult2.getP99());
        assertEquals(10.0d, getResult2.getAverageTimeTaken());
        assertEquals(1L, getResult.getTotalRequests().longValue());
        assertEquals(10.0d, getResult.getP99());
        assertEquals(10.0d, getResult.getAverageTimeTaken());
        Report getResult3 = reportList.get(0);
        assertEquals(1L, getResult3.getTotalRequests().longValue());
        assertEquals(10.0d, getResult3.getP99());
        assertEquals(10.0d, getResult3.getAverageTimeTaken());
        assertEquals("https://example.org/example", getResult3.getUrl());
        verify(recordRepository, atLeast(1)).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldThrowUnableToFetchRecordsForGetReport() throws Exception {
        when(recordRepository.findByUrl(Mockito.<String>any())).thenReturn(Optional.empty());
        assertThrows(DatabaseErrorException.class, () -> metricServiceImpl.getReport());
        verify(recordRepository, atLeast(1)).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldThrowDatabaseErrorExceptionForGetReport() throws Exception {
        when(recordRepository.findByUrl(Mockito.<String>any()))
                .thenThrow(new DatabaseErrorException("An error occurred"));
        assertThrows(DatabaseErrorException.class, () -> metricServiceImpl.getReport());
        verify(recordRepository).findByUrl(Mockito.<String>any());
    }

    @Test
    void shouldBeAbleToCreateReportForSingleEntryInDatabase() {
        Record record = new Record();
        record.setId(1L);
        record.setRequest("Request");
        record.setResponse("Response");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");

        ArrayList<Record> recordList = new ArrayList<>();
        recordList.add(record);
        Report actualCreateReportResult = metricServiceImpl.createReport(Optional.of(recordList));
        assertEquals(10.0d, actualCreateReportResult.getAverageTimeTaken());
        assertEquals("https://example.org/example", actualCreateReportResult.getUrl());
        assertEquals(1L, actualCreateReportResult.getTotalRequests().longValue());
        assertEquals(10.0d, actualCreateReportResult.getP99());
    }

    @Test
    void shouldBeAbleToCreateReportForMultipleEntriesInDatabase() {
        Record record = mock(Record.class);
        when(record.getTimeInMillis()).thenReturn(10L);
        when(record.getUrl()).thenReturn("https://example.org/example");
        doNothing().when(record).setId(Mockito.<Long>any());
        doNothing().when(record).setRequest(Mockito.<String>any());
        doNothing().when(record).setResponse(Mockito.<String>any());
        doNothing().when(record).setTimeInMillis(Mockito.<Long>any());
        doNothing().when(record).setUrl(Mockito.<String>any());
        record.setId(1L);
        record.setRequest("Request");
        record.setResponse("Response");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");

        ArrayList<Record> recordList = new ArrayList<>();
        recordList.add(record);
        Report actualCreateReportResult = metricServiceImpl.createReport(Optional.of(recordList));
        assertEquals(10.0d, actualCreateReportResult.getAverageTimeTaken());
        assertEquals("https://example.org/example", actualCreateReportResult.getUrl());
        assertEquals(1L, actualCreateReportResult.getTotalRequests().longValue());
        assertEquals(10.0d, actualCreateReportResult.getP99());
        verify(record, atLeast(1)).getTimeInMillis();
        verify(record).getUrl();
        verify(record).setId(Mockito.<Long>any());
        verify(record).setRequest(Mockito.<String>any());
        verify(record).setResponse(Mockito.<String>any());
        verify(record).setTimeInMillis(Mockito.<Long>any());
        verify(record).setUrl(Mockito.<String>any());
    }
}

