package com.navi.assignment2.controller;

import com.navi.assignment2.contract.request.UserRequest;
import com.navi.assignment2.contract.response.ErrorResponse;
import com.navi.assignment2.entity.User;
import com.navi.assignment2.service.EmailService;
import com.navi.assignment2.service.UserService;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/v1/user")
    @Timed(value = "api_add_user.time", description = "Time taken to add user", extraTags = {"method", "POST"})
    public ResponseEntity<?> addUser(@RequestBody UserRequest userRequest) throws Exception {
        Metrics.counter("create_user_api_counter").increment();
        userService.validateUser(userRequest);
        userService.validateSources(userRequest);

        User user = userService.save(userRequest);

        emailService.sendWelcomeEmail(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/unsubscribe")
    @Timed(value = "api_unsubscribe.time", description = "Time taken to unsubscribe", extraTags = {"method", "PUT"})
    public ResponseEntity<?> unsubscribe(@RequestParam String email) throws Exception {
        emailService.unsubscribe(email);
        return new ResponseEntity<>(new ErrorResponse("Unsubscribed successfully"), HttpStatus.OK);
    }
}
