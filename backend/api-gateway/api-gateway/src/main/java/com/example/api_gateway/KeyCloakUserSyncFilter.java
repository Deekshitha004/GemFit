package com.example.api_gateway;

import com.example.api_gateway.user.RegisterRequest;
import com.example.api_gateway.user.UserValidationService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserValidationService userValidationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 0.  Prefer a constant for the header
        final String USER_ID_HEADER = "X-User-Id";   // <- match what clients send

        String userId    = exchange.getRequest().getHeaders().getFirst(USER_ID_HEADER);
        String authToken = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authToken == null) {
            log.warn("Missing Authorization header, skipping user sync.");
            return chain.filter(exchange);
        }

        RegisterRequest registerRequest = getUserDetails(authToken);

        if (userId == null && registerRequest != null) {
            userId = registerRequest.getKeyCloakId();   // take it from JWT
        }

        log.info("RegisterRequest built: {}", registerRequest);
        log.info("Resolved keycloakId for this call: {}", userId);     // ✅ placeholder

        if (userId == null) return chain.filter(exchange);             // still nothing

        final String finalUserId = userId;

        return userValidationService.validateUser(finalUserId)
                .flatMap(exists -> {
                    if (!exists && registerRequest != null) {
                        return userValidationService.registerUser(registerRequest);
                    }
                    return Mono.empty();
                })
                .then(Mono.defer(() ->
                        chain.filter(
                                exchange.mutate()
                                        .request(b -> b.header(USER_ID_HEADER, finalUserId))   // overwrite / add
                                        .build()
                        )
                ));
    }



    private RegisterRequest getUserDetails(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            log.warn("Authorization token is missing");
            return null;
        }

        try {
            String tokenWithoutBearer = authToken.replace("Bearer", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            log.info("Decoded JWT Claims: {}", claimsSet.toJSONObject());

            String keycloakId = claimsSet.getStringClaim("sub");  // ✅ This should not be nul
            log.info("Parsed keycloakId from JWT: {}", keycloakId);
            if (keycloakId == null) {
                keycloakId = claimsSet.getSubject();              // fallback
            }

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claimsSet.getStringClaim("email"));
            registerRequest.setKeyCloakId(keycloakId);            // ✅ Proper assignment
            registerRequest.setPassword("dummy@123");

            registerRequest.setFirstName(claimsSet.getStringClaim("given_name"));
            registerRequest.setLastName(claimsSet.getStringClaim("family_name"));

            log.info("RegisterRequest prepared with keyCloakId={}", keycloakId);

            return registerRequest;

        } catch (Exception e) {
            log.error("Failed to parse JWT", e);
            return null;
        }
    }




}
