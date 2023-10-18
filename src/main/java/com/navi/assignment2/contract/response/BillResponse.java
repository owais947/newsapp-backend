package com.navi.assignment2.contract.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    private double everything;
    private double topHeadlines;
    private double sources;
}
