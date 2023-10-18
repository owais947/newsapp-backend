package com.navi.assignment2.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.navi.assignment2.Exception.DatabaseErrorException;
import com.navi.assignment2.Exception.NewsApiErrorException;
import com.navi.assignment2.contract.response.NewsResponse;
import com.navi.assignment2.entity.Article;
import com.navi.assignment2.entity.Endpoint;
import com.navi.assignment2.entity.Record;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.EndpointRepository;
import com.navi.assignment2.repository.NewsCache;
import com.navi.assignment2.repository.RecordRepository;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

//@ContextConfiguration(classes = {NewServiceImpl.class})
@ExtendWith(MockitoExtension.class)
class NewServiceImplTest {
//    @MockBean
    @Mock
    private EndpointRepository endpointRepository;

    @InjectMocks
    private NewServiceImpl newServiceImpl;
    @Mock
//    @MockBean
    private RecordRepository recordRepository;

//    @MockBean
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private NewsCache newsCache;

    @Test
    void shouldBeAbleToGetTopHeadlines() throws Exception {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);

        Endpoint endpoint = new Endpoint();
        endpoint.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        endpoint.setId(1L);
        endpoint.setUrl("https://example.org/example");
        endpoint.setUser(user);
        when(endpointRepository.save(Mockito.<Endpoint>any())).thenReturn(endpoint);

        Record record = new Record();
        record.setId(1L);
        record.setRequest("Request");
        record.setResponse("Response");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");
        when(recordRepository.save(Mockito.<Record>any())).thenReturn(record);
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(new NewsResponse());

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setEndpoints(new ArrayList<>());
        user2.setId(1L);
        user2.setPreferredSources("Preferred Sources");
        user2.setSelectedCategory("Selected Category");
        user2.setSelectedCountry("GB");
        user2.setSubscribed(true);
        assertTrue(newServiceImpl.getTopHeadlines(user2).isPresent());
        verify(endpointRepository).save(Mockito.<Endpoint>any());
        verify(recordRepository).save(Mockito.<Record>any());
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any());
    }

    @Test
    void shouldThrowDatabaseErrorForGetTopHeadlines() throws Exception {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);

        Endpoint endpoint = new Endpoint();
        endpoint.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        endpoint.setId(1L);
        endpoint.setUrl("https://example.org/example");
        endpoint.setUser(user);
        when(endpointRepository.save(Mockito.<Endpoint>any())).thenReturn(endpoint);
        when(recordRepository.save(Mockito.<Record>any())).thenThrow(new NewsApiErrorException("An error occurred"));
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(new NewsResponse());

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setEndpoints(new ArrayList<>());
        user2.setId(1L);
        user2.setPreferredSources("Preferred Sources");
        user2.setSelectedCategory("Selected Category");
        user2.setSelectedCountry("GB");
        user2.setSubscribed(true);
        assertThrows(DatabaseErrorException.class, () -> newServiceImpl.getTopHeadlines(user2));
        verify(endpointRepository).save(Mockito.<Endpoint>any());
        verify(recordRepository).save(Mockito.<Record>any());
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any());
    }

    @Test
    void shouldThrowNewsApiErrorForGetTopHeadlines() throws Exception {
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(null);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        assertThrows(NewsApiErrorException.class, () -> newServiceImpl.getTopHeadlines(user));
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any());
    }

    @Test
    void shouldBeAbleToGetLimitedNews() {
        NewsResponse response = mock(NewsResponse.class);
        ArrayList<Article> articleList = new ArrayList<>();
        when(response.getArticles()).thenReturn(articleList);
        assertEquals(articleList, newServiceImpl.getLimitedNews(response, 1).getArticles());
        verify(response).getArticles();
    }

    @Test
    void shouldBeAbleToGetTopHeadlinesFromSource() throws Exception {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);

        Endpoint endpoint = new Endpoint();
        endpoint.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        endpoint.setId(1L);
        endpoint.setUrl("https://example.org/example");
        endpoint.setUser(user);
        when(endpointRepository.save(Mockito.<Endpoint>any())).thenReturn(endpoint);

        Record record = new Record();
        record.setId(1L);
        record.setRequest("Request");
        record.setResponse("Response");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");
        when(recordRepository.save(Mockito.<Record>any())).thenReturn(record);
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(new NewsResponse());

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setEndpoints(new ArrayList<>());
        user2.setId(1L);
        user2.setPreferredSources("Preferred Sources");
        user2.setSelectedCategory("Selected Category");
        user2.setSelectedCountry("GB");
        user2.setSubscribed(true);
        assertTrue(newServiceImpl.getTopHeadlinesFromSource(user2).isPresent());
        verify(endpointRepository).save(Mockito.<Endpoint>any());
        verify(recordRepository).save(Mockito.<Record>any());
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any());
    }

    @Test
    void shouldThrowDatabaseErrorForGetTopHeadlinesFromSource() throws Exception {
        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);

        Endpoint endpoint = new Endpoint();
        endpoint.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        endpoint.setId(1L);
        endpoint.setUrl("https://example.org/example");
        endpoint.setUser(user);
        when(endpointRepository.save(Mockito.<Endpoint>any())).thenReturn(endpoint);
        when(recordRepository.save(Mockito.<Record>any())).thenThrow(new NewsApiErrorException("An error occurred"));
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(new NewsResponse());

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setEndpoints(new ArrayList<>());
        user2.setId(1L);
        user2.setPreferredSources("Preferred Sources");
        user2.setSelectedCategory("Selected Category");
        user2.setSelectedCountry("GB");
        user2.setSubscribed(true);
        assertThrows(DatabaseErrorException.class, () -> newServiceImpl.getTopHeadlinesFromSource(user2));
        verify(endpointRepository).save(Mockito.<Endpoint>any());
        verify(recordRepository).save(Mockito.<Record>any());
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any());
    }

    @Test
    void shouldThrowNewsApiErrorForGetTopHeadlinesFromSource() throws Exception {
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(null);

        User user = new User();
        user.setEmail("jane.doe@example.org");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        assertThrows(NewsApiErrorException.class, () -> newServiceImpl.getTopHeadlinesFromSource(user));
        verify(restTemplate).getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any());
    }


    @Test
    void shouldBeAbleToFetchNewsFromCountryAndCategory() throws Exception {
        User user = new User("abcd@example.com", "Selected Category", "GB", "Preferred Sources");
        when(newsCache.get("Selected CategoryGB")).thenReturn(new NewsResponse());
        when(newsCache.containsKey("Selected CategoryGB")).thenReturn(true);

        Optional<NewsResponse> actualFetchNewsFromCountryAndCategoryResult = newServiceImpl.getTopHeadlinesFromCountryAndCategory(user);
        assertTrue(actualFetchNewsFromCountryAndCategoryResult.isPresent());
        assertSame(newsCache.get("Selected CategoryGB"), actualFetchNewsFromCountryAndCategoryResult.get());
    }

    @Test
    void shouldBeAbleToFetchNewsFromCountryAndCategoryFromApi() throws Exception {
        User user = new User("abcd@example.com", "Selected Category", "GB", "Preferred Sources");
        when(newsCache.containsKey("Selected CategoryGB")).thenReturn(false);
        when(restTemplate.getForObject(Mockito.<String>any(), Mockito.<Class<NewsResponse>>any(), (Object[]) any()))
                .thenReturn(new NewsResponse());
        doNothing().when(newsCache).add(Mockito.<String>any(), Mockito.<NewsResponse>any());

        Record record = new Record();
        record.setId(1L);
        record.setRequest("Request");
        record.setResponse("Response");
        record.setTimeInMillis(10L);
        record.setUrl("https://example.org/example");
        when(recordRepository.save(Mockito.<Record>any())).thenReturn(record);

        Endpoint endpoint = new Endpoint();
        endpoint.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        endpoint.setId(1L);
        endpoint.setUrl("https://example.org/example");
        endpoint.setUser(user);
        when(endpointRepository.save(Mockito.<Endpoint>any())).thenReturn(endpoint);

        Optional<NewsResponse> actualFetchNewsFromCountryAndCategoryResult = newServiceImpl.getTopHeadlinesFromCountryAndCategory(user);
        assertTrue(actualFetchNewsFromCountryAndCategoryResult.isPresent());
        verify(newsCache).add(Mockito.<String>any(), Mockito.<NewsResponse>any());
        verify(endpointRepository).save(Mockito.<Endpoint>any());
        verify(recordRepository).save(Mockito.<Record>any());
    }
}

