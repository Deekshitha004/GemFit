package com.example.ai_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiClientConfig {

    // IMPORTANT: Make sure this @Value annotation points to the *NEW* base-url property
    @Value("${gemini.api.base-url}")
    private String geminiApiBaseUrl; // This variable will now hold "https://generativelanguage.googleapis.com"

    @Bean
    @Qualifier("geminiWebClient")
    public WebClient geminiWebClient() {
        System.out.println("Configuring WebClient with Base URL: " + geminiApiBaseUrl); // Add this for debugging
        return WebClient.builder()
                .baseUrl(geminiApiBaseUrl) // DIRECTLY USE the base URL, NO substring needed here
                .build();
    }
}
