package com.navi.assignment2.service;
import com.navi.assignment2.contract.response.ValidSourceResponse;

import java.util.Optional;

public interface SourceService {
    public Optional<ValidSourceResponse> getValidSources(String country, String category) throws Exception;
}
