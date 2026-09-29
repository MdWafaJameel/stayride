package com.stayride.ride.client;

import com.stayride.ride.dto.UserSummary;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {

    private final RestClient.Builder restClientBuilder;

    public UserClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder
    ) {
        this.restClientBuilder = restClientBuilder;
    }

    @Retry(name = "userService")
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Bulkhead(name = "userService")
    public UserSummary getUser(Long userId) {

        return restClientBuilder
                .baseUrl("http://STAYRIDE-MONOLITH")
                .build()
                .get()
                .uri("/api/users/{id}", userId)
                .retrieve()
                .body(UserSummary.class);
    }

    private UserSummary getUserFallback(Long userId, Throwable throwable) {

        System.out.println(
                "User Service unavailable. Using fallback for userId: "
                        + userId
                        + ", reason: "
                        + throwable.getClass().getName()
        );

        return new UserSummary(
                userId,
                "Unknown User",
                "unknown@stayride.com",
                "0000000000",
                "TESTER"
        );
    }
}