package com.navi.assignment2.contract.response;

import com.navi.assignment2.entity.Source;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidSourceResponse {
    private List<Source> sources;
}
