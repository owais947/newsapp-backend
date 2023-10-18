package com.navi.assignment2.controller;

import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.service.MetricService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MetricController {
    @Autowired
    private MetricService metricService;


    @GetMapping("v1/bill")
    @Timed(value = "api_get_bill.time", description = "Time taken to get bill", extraTags = {"method", "GET"})
    public ResponseEntity<?> getBill() throws Exception {
    return new ResponseEntity<>(metricService.getBill(), HttpStatus.OK);
    }

    @GetMapping("v1/report")
    @Timed(value = "api_get_report.time", description = "Time taken to get report", extraTags = {"method", "GET"})
    public ResponseEntity<?> getReport() throws Exception {
    return new ResponseEntity<>(metricService.getReport(), HttpStatus.OK);
    }

    @GetMapping("v1/leaderboard")
    @Timed(value = "api_get_leaderboard.time", description = "Time taken to get leaderboard", extraTags = {"method", "GET"})
    public ResponseEntity<?> getLeaderBoard() throws Exception {
        return new ResponseEntity<>(metricService.getLeaderBoard(), HttpStatus.OK);
    }
}
