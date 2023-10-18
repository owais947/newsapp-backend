package com.navi.assignment2.service.impl;

import com.navi.assignment2.Exception.NewsApiErrorException;
import com.navi.assignment2.Exception.UserNotFoundException;
import com.navi.assignment2.contract.request.UserRequest;
import com.navi.assignment2.entity.Article;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.UserRepository;
import com.navi.assignment2.service.EmailService;
import com.navi.assignment2.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendWelcomeEmail(User user) {
        String message = "Welcome to News API" + "\n" + "Your information is a follows: " + "\n"
                + "Email: " + user.getEmail() + "\n"
                + "Category: " + user.getSelectedCategory() + "\n"
                + "Country: " + user.getSelectedCountry() + "\n"
                + "Preferred Sources: " + user.getPreferredSources() + "\n"
                + "ID: " + user.getId() + "\n";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Welcome to News API");
        mailMessage.setText(message);
        mailMessage.setFrom(senderEmail);
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendTopHeadlinesEmail(User user) throws Exception {
        StringBuilder messageList = new StringBuilder();
        messageList.append("<html><body><h2>Top Headlines for ").append(user.getSelectedCountry()).append(" in ").append(user.getSelectedCategory()).append(" category</h2>");

        Optional<List<Article>> articleList = Optional.ofNullable(newsService.getTopHeadlines(user).get().getArticles());
        if(articleList.isEmpty()) {
            throw new NewsApiErrorException("Unable to fetch articles");
        }

        for(Article article: articleList.get()) {
            messageList.append("<p><a href= '").append(article.getUrl()).append("'>").append(article.getTitle()).append("</a></p><br>");
        }

        String html = "<p><a href= 'http://localhost:8080/api/unsubscribe'>unsubscribe</a></p></body></html>";

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);

        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject("Top Headlines for " + user.getSelectedCountry() + " in " + user.getSelectedCategory() + " category");
        messageHelper.setText(messageList + html, true);
        messageHelper.setFrom(senderEmail);
        javaMailSender.send(mailMessage);
    }

    @Async
    public void unsubscribe(String email) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        user.get().setSubscribed(false);
        userRepository.save(user.get());

        String html = "<html><body><p>You have been unsubscribed from News API</p></body></html>";

        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true);

        messageHelper.setTo(email);
        messageHelper.setSubject("Unsubscribed from News API");
        messageHelper.setText(html, true);
        messageHelper.setFrom(senderEmail);
        javaMailSender.send(mailMessage);
    }
}
