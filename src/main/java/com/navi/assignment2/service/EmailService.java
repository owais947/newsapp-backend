package com.navi.assignment2.service;

import com.navi.assignment2.entity.User;

public interface EmailService {
    void sendWelcomeEmail(User user) throws Exception;
    void sendTopHeadlinesEmail(User user) throws Exception;
    void unsubscribe(String email) throws Exception;
}
