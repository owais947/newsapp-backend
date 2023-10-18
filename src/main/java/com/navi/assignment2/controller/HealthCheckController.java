package com.navi.assignment2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {
    @GetMapping("/ping")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("pong");
    }
}
