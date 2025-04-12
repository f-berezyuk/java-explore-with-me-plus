package ru.practicum.ewm.stats.client;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.EWM.stats.dto.EndpointHit;
import ru.practicum.EWM.stats.dto.ViewStats;

@Component
public class StatsClient {
    private static final String VIRTUAL_HOSTNAME = "stats-server";
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;
    private final RetryTemplate retryTemplate;

    StatsClient(DiscoveryClient discClient) {
        retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        MaxAttemptsRetryPolicy retryPolicy = new MaxAttemptsRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        this.discoveryClient = discClient;
        this.restClient = RestClient.create(getServiceUrl());
    }

    private String getServiceUrl() {
        var instances = retryTemplate.execute(ctx -> discoveryClient.getInstances(VIRTUAL_HOSTNAME));
        if (instances != null && !instances.isEmpty()) {
            var instance = instances.getFirst();
            return "http://" + instance.getHost() + ":" + instance.getPort();
        }
        throw new RuntimeException("Service not found: " + VIRTUAL_HOSTNAME);
    }

    public void hit(@Valid EndpointHit hitDto) {
        retryTemplate.execute(ctx -> restClient.post().uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity());
    }

    public List<ViewStats> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    Boolean unique) {
        return retryTemplate.execute(ctx -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve().body(new ParameterizedTypeReference<>() {
                }));
    }
}