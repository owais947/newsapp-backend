package com.navi.assignment2.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private String url;
    private double p99;
    private double averageTimeTaken;
    private Long totalRequests;
}
