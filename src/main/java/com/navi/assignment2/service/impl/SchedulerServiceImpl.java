package com.navi.assignment2.service.impl;

import com.navi.assignment2.entity.User;
import com.navi.assignment2.repository.NewsCache;
import com.navi.assignment2.repository.UserRepository;
import com.navi.assignment2.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchedulerServiceImpl {
    @Autowired
    private NewsCache newsCache;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 0 * * *")
    public void clearCache() {
        newsCache.clear();
    }

    @Async
    @Scheduled(cron = "0 0 09 * * ?")
    public void sendDailyUpdate() throws Exception {
        List<User> users = userRepository.findAll();
        for(User user: users) {
            if(user.isSubscribed()) {
                emailService.sendTopHeadlinesEmail(user);
            }
        }
    }
}
