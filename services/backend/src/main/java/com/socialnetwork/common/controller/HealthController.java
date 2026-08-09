package com.socialnetwork.common.controller;

import com.socialnetwork.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {

        log.info("Kiểm tra trạng thái hoạt động của hệ thống");

        Map<String, String> data = Map.of(
                "status", "UP",
                "service", "social-network-backend"
        );

        return ResponseEntity.ok(
                ApiResponse.success(data)
        );
    }
}