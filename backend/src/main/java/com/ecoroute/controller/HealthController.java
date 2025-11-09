package com.ecoroute.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Small health endpoint to allow the frontend or orchestration to check
 * whether the backend is up without invoking heavier application logic.
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body(java.util.Map.of("status", "UP"));
    }
}
