package com.example.api_gateway.user;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder WebClientBuilder(){

        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder lbBuilder) {
        return lbBuilder
                .baseUrl("lb://user-service")
                .build();
    }
}
