package com.navi.assignment2.service;

import com.navi.assignment2.contract.response.BillResponse;
import com.navi.assignment2.contract.response.NewsResponse;
import com.navi.assignment2.entity.User;

import java.util.Optional;

public interface NewsService {
    public Optional<NewsResponse> getTopHeadlines(User user) throws Exception;
    public Optional<NewsResponse> getTopHeadlinesFromCountryAndCategory(User user) throws Exception;

    public NewsResponse getLimitedNews(NewsResponse response, int limit);

    public Optional<NewsResponse> getTopHeadlinesFromSource(User user) throws Exception;
}
