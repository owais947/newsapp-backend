package com.navi.assignment2.service.impl;
import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.Exception.NewsApiErrorException;
import com.navi.assignment2.contract.response.ValidSourceResponse;
import com.navi.assignment2.entity.Endpoint;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.RecordRepository;
import com.navi.assignment2.service.SourceService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Slf4j
@Service
public class SourceServiceImpl implements SourceService {

    public SourceServiceImpl() {
    }

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private EndpointRepository endpointRepository;

    @Value("${api.key}")
    private String apiKey;

    @Value("${news.base.url}")
    private String newsBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    public Optional<ValidSourceResponse> getValidSources(String country, String category) throws Exception{
        String url = newsBaseUrl + "/v2/top-headlines/sources?country=" + country + "&category=" + category + "&" + apiKey;

        LocalTime timeSent = LocalTime.now();

        Optional<ValidSourceResponse> response = Optional.ofNullable(restTemplate.getForObject(url, ValidSourceResponse.class));
        if (response.isEmpty()) {
            log.error("Unable to fetch news sources");
            throw new NewsApiErrorException("Unable to fetch news sources");
        }

        LocalTime timeReceived = LocalTime.now();
        Long timeElapsed = java.time.temporal.ChronoUnit.MILLIS.between(timeSent, timeReceived);

        try{
            recordRepository.save(Record.builder().request("country=" + country + "&category=" + category + "&" + apiKey).url("/v2/top-headlines/sources").response(response.toString()).timeInMillis(timeElapsed).build());
        }
        catch (Exception e) {
            log.error("Unable to save record for url: {}", url);
            throw new DatabaseErrorException("Unable to save record");
        }
        log.info("Record saved successfully for url: {}", url);
        return response;
    }
}
