package com.perftest.vthreads.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${app.self-base-url:http://localhost:8080}")
    private String selfBaseUrl;

    /**
     * RestClient configured with self base URL for blocking HTTP calls.
     * Used by IoSimulationService for fan-out and HTTP simulation.
     */
    @Bean("selfRestClient")
    public RestClient selfRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(selfBaseUrl)
                .build();
    }

    /**
     * WebClient configured for reactive (non-blocking) calls.
     * Available for scenarios comparing reactive vs virtual thread approaches.
     */
    @Bean("selfWebClient")
    public WebClient selfWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(selfBaseUrl)
                .build();
    }

    /**
     * Generic RestClient without a fixed base URL, for external calls.
     */
    @Bean("genericRestClient")
    public RestClient genericRestClient(RestClient.Builder builder) {
        return builder.build();
    }
}
