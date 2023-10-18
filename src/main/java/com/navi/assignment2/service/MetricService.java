package com.navi.assignment2.service;

import com.navi.assignment2.contract.response.BillResponse;
import com.navi.assignment2.contract.response.ReportResponse;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.entity.Report;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MetricService {
    public Optional<BillResponse> getBill() throws Exception;
    public Optional<ReportResponse> getReport() throws Exception;
    public Report createReport(Optional<List<Record>> recordList);
    public List<Map<Long, Integer>> getLeaderBoard() throws Exception;
}
