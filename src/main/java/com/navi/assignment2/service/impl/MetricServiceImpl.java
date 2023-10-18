package com.navi.assignment2.service.impl;
import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.contract.response.BillResponse;
import com.navi.assignment2.contract.response.ReportResponse;
import com.navi.assignment2.entity.*;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.RecordRepository;
import com.navi.assignment2.repository.UserRepository;
import com.navi.assignment2.service.MetricService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import static java.util.Optional.*;
import static org.springframework.core.OrderComparator.sort;

@Slf4j
@Service
public class MetricServiceImpl implements MetricService {
    public static final double EVERYTHING_COST = 10.5;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private EndpointRepository endpointRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<BillResponse> getBill() throws Exception {
        Optional<List<Record>> everythingResponse = recordRepository.findByUrl("/v2/everything");
        Optional<List<Record>> topHeadlinesResponse = recordRepository.findByUrl("/v2/top-headlines");
        Optional<List<Record>> topHeadlinesFromSourceResponse = recordRepository.findByUrl("/v2/top-headlines/sources");

        if(everythingResponse.isEmpty() || topHeadlinesResponse.isEmpty() || topHeadlinesFromSourceResponse.isEmpty()) {
            throw new DatabaseErrorException("Unable to fetch records");
        }

        Optional<BillResponse> billResponse = of(new BillResponse(everythingResponse.get().size() * (double) EVERYTHING_COST, topHeadlinesResponse.get().size() * (double) 5.5, topHeadlinesFromSourceResponse.get().size() * (double) 200));
        log.info("Bill generated successfully: {}", billResponse.get());
        return billResponse;

    }

    @Override
    public Optional<ReportResponse> getReport() throws Exception {
        Optional<List<Record>> everythingResponse = recordRepository.findByUrl("/v2/everything");
        Optional<List<Record>> topHeadlinesResponse = recordRepository.findByUrl("/v2/top-headlines");
        Optional<List<Record>> topHeadlinesFromSourceResponse = recordRepository.findByUrl("/v2/top-headlines/sources");

        if(everythingResponse.isEmpty() || topHeadlinesResponse.isEmpty() || topHeadlinesFromSourceResponse.isEmpty()) {
            throw new DatabaseErrorException("Unable to fetch records");
        }

        List<Report> reportList = new ArrayList<>();

        reportList.add(createReport(everythingResponse));
        reportList.add(createReport(topHeadlinesResponse));
        reportList.add(createReport(topHeadlinesFromSourceResponse));

        Optional<ReportResponse> reportResponse = of(new ReportResponse(reportList));
        log.info("Report generated successfully: {}", reportResponse.toString());
        return reportResponse;
    }

    @Override
    public Report createReport(Optional<List<Record>> recordList) {
        if(recordList.get().isEmpty()) {
            return null;
        }

        List<Record> records = recordList.get();
        sort(records);
        double p99 = 0;
        double averageTimeTake = 0;
        String url = records.get(0).getUrl();
        Long totalRequests = (long) records.size();

        for(Record record: records) {
            averageTimeTake += record.getTimeInMillis();
        }
        averageTimeTake /= totalRequests;

        p99 = records.get((int) (totalRequests * 0.99)).getTimeInMillis();

        Report report = new Report(url, p99, averageTimeTake,totalRequests);
        log.info("Report created successfully: {}", report);
        return report;
    }

    @Override
    public List<Map<Long, Integer>> getLeaderBoard() throws Exception {
        List<Map<Long, Integer>> leaderboard = endpointRepository.getTopUsers();
        log.info("Leaderboard fetched successfully: {}", leaderboard.toString());
        return leaderboard;
    }
}
