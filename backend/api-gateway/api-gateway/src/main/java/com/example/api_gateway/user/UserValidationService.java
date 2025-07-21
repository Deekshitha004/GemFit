package com.example.api_gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserValidationService {

    @Autowired
    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUser(String userId ) {
        log.info("Calling User ValidationAPi for userId:{}", userId);
        return userServiceWebClient
                .get()
                .uri("/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    HttpStatus status = (HttpStatus) ex.getStatusCode();   // ← now compiles
                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.error(new RuntimeException("User not found: " + userId));
                    }
                    if (status == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Invalid userId: " + userId));
                    }
                    return Mono.error(ex); // fall-through
                });


    }

    public Mono<?> registerUser(RegisterRequest registerRequest) {
       log.info("Calling User Registration APi for UserId:",registerRequest.getEmail());
        return userServiceWebClient
                .post()
                .uri("/users/register")
                .bodyValue(registerRequest)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    HttpStatus status = (HttpStatus) ex.getStatusCode();   // ← now compiles
                    if (status == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("Bad Request"));
                    }
                    if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                        return Mono.error(new RuntimeException("Internal Server Error:"+ex.getMessage()));
                    }
                    return Mono.error(new RuntimeException("Unexpected Error"+ex.getMessage())); // fall-through
                });

    }
}
