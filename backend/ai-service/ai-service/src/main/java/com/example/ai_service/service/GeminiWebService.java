package com.example.ai_service.service;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GeminiWebService {

    @Qualifier("geminiWebClient")          // plain WebClient bean (no @LoadBalanced)
    private final WebClient webClient;

    @Value("${gemini.api.endpoint-path}") // <--- Inject the specific endpoint path
    private String geminiApiEndpointPath;

    @Value("${gemini.api.key}")            // injected from env/IDE
    private String geminiKey;

    public String getAnswer(String prompt) {

        /* ✔ body in the exact JSON structure Gemini expects */
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        return webClient.post()
                .uri(b -> b.path(geminiApiEndpointPath)          // base path
                        .queryParam("key", geminiKey)
                        .build())
                .bodyValue(body)
                .retrieve()
                .onStatus(s -> s.isError(),
                        r -> r.bodyToMono(String.class)
                                .flatMap(msg -> Mono.error(
                                        new RuntimeException("Gemini error " + r.statusCode() + " : " + msg))))
                .bodyToMono(String.class)
                .block();           // return Mono<String> if you prefer non-blocking
    }
}
