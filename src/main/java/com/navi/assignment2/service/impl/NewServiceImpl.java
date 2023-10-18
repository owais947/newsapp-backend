package com.navi.assignment2.service.impl;
import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.Exception.NewsApiErrorException;
import com.navi.assignment2.contract.response.NewsResponse;
import com.navi.assignment2.entity.Endpoint;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.NewsCache;
import com.navi.assignment2.repository.RecordRepository;
import com.navi.assignment2.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.min;

@Slf4j
@Service
public class NewServiceImpl implements NewsService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private EndpointRepository endpointRepository;

    @Value("${news.base.url}")
    private String newsBaseUrl;

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private NewsCache newsCache;

    @Override
    public Optional<NewsResponse> getTopHeadlines(User user) throws Exception {
        if(user.getPreferredSources().isEmpty()) {
            return getTopHeadlinesFromCountryAndCategory(user);
        }
        else {
            return getTopHeadlinesFromSource(user);
        }
    }

    @Override
    public NewsResponse getLimitedNews(NewsResponse response, int limit) {
        var articles = response.getArticles();
        if(limit == -1) {
            return response;
        }
        else {
            var limitedArticles = new ArrayList<>(articles.subList(0, min(limit, articles.size())));
            return new NewsResponse(limitedArticles);
        }
    }

    @Override
    public Optional<NewsResponse> getTopHeadlinesFromSource(User user) throws Exception {
        String[] sources = user.getPreferredSources().split(",");
        List<String> selectedSources = new ArrayList<>();
        for(String source : sources) {
            selectedSources.add(source);
        }

        Record record = new Record();
        String url = newsBaseUrl + "/v2/top-headlines?sources=" + user.getPreferredSources() + "&" + apiKey;
        record.setRequest("sources=" + user.getPreferredSources() + "&" + apiKey);

        LocalTime timeSent = LocalTime.now();

        Optional<NewsResponse> response = Optional.ofNullable(restTemplate.getForObject(url, NewsResponse.class));

        if (response.isEmpty()) {
            log.error("Unable to access News API");
            throw new NewsApiErrorException("Unable to access News API");
        }

        log.info("Top headlines fetched from preferred sources: {}", user.getPreferredSources());

        LocalTime timeReceived = LocalTime.now();
        Long timeElapsed = ChronoUnit.MILLIS.between(timeSent, timeReceived);

        record.setUrl("/v2/top-headlines");
        record.setResponse(response.toString());
        record.setTimeInMillis(timeElapsed);
        try{
            endpointRepository.save(Endpoint.builder().url("/v2/top-headlines").user(user).createdAt(LocalDateTime.now()).build());
            recordRepository.save(record);
        }
        catch (Exception e) {
            log.error("Unable to save record for url: {}", url);
            throw new DatabaseErrorException("Unable to save record");
        }
        return response;
    }

    @Override
    public Optional<NewsResponse> getTopHeadlinesFromCountryAndCategory(User user) throws Exception {
        String url = newsBaseUrl + "/v2/top-headlines?country=" + user.getSelectedCountry() + "&category=" + user.getSelectedCategory() +"&"+ apiKey;

        LocalTime timeSent = LocalTime.now();

        if(newsCache.containsKey(user.getSelectedCategory() + user.getSelectedCountry())) {
            log.info("Fetching news from cache");
            return Optional.of(newsCache.get(user.getSelectedCategory() + user.getSelectedCountry()));
        }

        Optional<NewsResponse> response = Optional.ofNullable(restTemplate.getForObject(url, NewsResponse.class));

        if (response.isEmpty()) {
            throw new NewsApiErrorException("Unable to access News API");
        }

        log.info("Top headlines fetched from country: {} and category: {}", user.getSelectedCountry(), user.getSelectedCategory());
        newsCache.add(user.getSelectedCategory() + user.getSelectedCountry(), response.get());

        LocalTime timeReceived = LocalTime.now();
        Long timeElapsed = ChronoUnit.MILLIS.between(timeSent, timeReceived);

        try{
            endpointRepository.save(Endpoint.builder().url("/v2/top-headlines").user(user).createdAt(LocalDateTime.now()).build());
            recordRepository.save(Record.builder().url("/v2/top-headlines").request("country=" + user.getSelectedCountry() + "&category=" + user.getSelectedCategory() +"&"+ apiKey).response(response.toString()).timeInMillis(timeElapsed).build());
        }
        catch (Exception e) {
            log.error("Unable to save record for url: {}", url);
            throw new DatabaseErrorException("Unable to save record");
        }
        return response;
    }
}
