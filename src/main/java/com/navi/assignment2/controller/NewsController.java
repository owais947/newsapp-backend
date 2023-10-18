package com.navi.assignment2.controller;
import com.navi.assignment2.contract.response.ErrorResponse;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.service.*;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class NewsController {

    @Autowired
    private UserService userService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private SourceService sourceService;
    @Autowired
    private EmailService emailService;


    @GetMapping("/v1/top-headlines")
    @Timed(value = "api_get_top_headlines_from_country_and_category.time", description = "Time taken to get top headlines from country and category", extraTags = {"method", "GET"})
    public ResponseEntity<?> getTopHeadlinesFromCountryAndCategory(@RequestParam Long id) throws Exception {
        User user = userService.fetchUser(id);
        Metrics.counter("get_news_for_country_and_category_counter", "country", user.getSelectedCountry(), "category", user.getSelectedCategory(), "api", "v1/top-headlines").increment();
        return new ResponseEntity<>(newsService.getTopHeadlinesFromCountryAndCategory(user), HttpStatus.OK);
    }

    @GetMapping("/v2/top-headlines")
    @Timed(value = "api_get_limited_top_headlines.time", description = "Time taken to get limited top headlines", extraTags = {"method", "GET"})
    public ResponseEntity<?> getMaxLimitHeadLines(@RequestParam Long id, @RequestParam(name = "max-limit", required = false, defaultValue = "-1") int maxLimit) throws Exception {
        User user = userService.fetchUser(id);
        Metrics.counter("get_news_for_country_and_category_counter", "country", user.getSelectedCountry(), "category", user.getSelectedCategory(), "api", "v1/top-headlines").increment();
        return new ResponseEntity<>(newsService.getLimitedNews(newsService.getTopHeadlines(user).get(), maxLimit), HttpStatus.OK);
    }

    @GetMapping("/v1/sources")
    @Timed(value = "api_get_valid_sources.time", description = "Time taken to get valid sources", extraTags = {"method", "GET"})
    public ResponseEntity<?> getValidSources(@RequestParam Long id) throws Exception {
        User user = userService.fetchUser(id);
        return new ResponseEntity<>(sourceService.getValidSources(user.getSelectedCountry(), user.getSelectedCategory()), HttpStatus.OK);
    }

    @GetMapping("v3/top-headlines")
    @Timed(value = "api_get_top_headlines_from_sources.time", description = "Time taken to get top headlines from sources", extraTags = {"method", "GET"})
    public ResponseEntity<?> getTopHeadlinesFromSources(@RequestParam Long id) throws Exception {
        User user = userService.fetchUser(id);
        return new ResponseEntity<>(newsService.getTopHeadlines(user), HttpStatus.OK);
    }

    @GetMapping("/v1/email/top-headlines")
    @Timed(value = "api_email_top_headlines.time", description = "Time taken to email top headlines", extraTags = {"method", "GET"})
    public ResponseEntity<?> emailTopHeadlines(@RequestParam Long id) throws Exception {
        User user = userService.fetchUser(id);
        Metrics.counter("get_news_for_country_and_category_counter", "country", user.getSelectedCountry(), "category", user.getSelectedCategory(), "api", "v1/top-headlines").increment();
        emailService.sendTopHeadlinesEmail(user);
        return new ResponseEntity<>(new ErrorResponse("Email sent successfully"), HttpStatus.OK);
    }
}
