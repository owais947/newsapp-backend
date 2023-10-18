package com.navi.assignment2.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.navi.assignment2.Exception.NewsApiErrorException;
import com.navi.assignment2.contract.response.NewsResponse;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.UserRepository;
import com.navi.assignment2.service.NewsService;

import java.util.ArrayList;
import java.util.Optional;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {EmailServiceImpl.class})
@ExtendWith(SpringExtension.class)
class EmailServiceImplTest {
    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private NewsService newsService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldBeAbleToSendWelcomeEmail() throws MailException {
        doNothing().when(javaMailSender).send(Mockito.<SimpleMailMessage>any());

        User user = new User();
        user.setEmail("abcd@example.com");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        emailServiceImpl.sendWelcomeEmail(user);
        verify(javaMailSender).send(Mockito.<SimpleMailMessage>any());
    }

    @Test
    void shouldBeAbleToSendTopHeadlinesEmail() throws Exception {
        when(newsService.getTopHeadlines(Mockito.<User>any())).thenReturn(Optional.of(new NewsResponse()));

        User user = new User();
        user.setEmail("abcd@example.com");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        assertThrows(NewsApiErrorException.class, () -> emailServiceImpl.sendTopHeadlinesEmail(user));
        verify(newsService).getTopHeadlines(Mockito.<User>any());
    }

    @Test
    void shouldThrowErrorForTopHeadlinesEmail() throws Exception {
        doThrow(new NewsApiErrorException("An error occurred")).when(javaMailSender).send(Mockito.<MimeMessage>any());
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        when(newsService.getTopHeadlines(Mockito.<User>any()))
                .thenReturn(Optional.of(new NewsResponse(new ArrayList<>())));

        User user = new User();
        user.setEmail("abcd@example.com");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        assertThrows(NewsApiErrorException.class, () -> emailServiceImpl.sendTopHeadlinesEmail(user));
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(Mockito.<MimeMessage>any());
        verify(newsService).getTopHeadlines(Mockito.<User>any());
    }

    @Test
    void shouldBeAbleToUnsubscribe() throws Exception {
        doNothing().when(javaMailSender).send(Mockito.<MimeMessage>any());
        when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        User user = new User();
        user.setEmail("abcd@example.com");
        user.setEndpoints(new ArrayList<>());
        user.setId(1L);
        user.setPreferredSources("Preferred Sources");
        user.setSelectedCategory("Selected Category");
        user.setSelectedCountry("GB");
        user.setSubscribed(true);
        Optional<User> ofResult = Optional.of(user);

        User user2 = new User();
        user2.setEmail("jane.doe@example.org");
        user2.setEndpoints(new ArrayList<>());
        user2.setId(1L);
        user2.setPreferredSources("Preferred Sources");
        user2.setSelectedCategory("Selected Category");
        user2.setSelectedCountry("GB");
        user2.setSubscribed(true);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user2);
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        emailServiceImpl.unsubscribe("abcd@example.com");
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(Mockito.<MimeMessage>any());
        verify(userRepository).save(Mockito.<User>any());
        verify(userRepository).findByEmail(Mockito.<String>any());
    }
}

