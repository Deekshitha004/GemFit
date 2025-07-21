package com.example.activity_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationService {

    @Autowired
    private final WebClient userServiceWebClient;

    public boolean validateUser(String userId) {
        log.info("Calling User Validation API for userId: {}", userId);
        try {
            return Boolean.TRUE.equals(
                    userServiceWebClient.get()
                            .uri("/users/{userId}/validate", userId)  // ✅ correctly injecting keycloakId
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block()
            );
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User not found: " + userId);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid UserId: " + userId);
            }
            throw new RuntimeException("Unexpected error from user-service: " + e.getMessage());
        }
    }
}
