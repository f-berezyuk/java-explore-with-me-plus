package ru.practicum.ewm.client;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

@Component
public class StatClient {
    private static final String VIRTUAL_HOSTNAME = "stat-server";
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    StatClient(DiscoveryClient discClient) {
        this.discoveryClient = discClient;
        this.restClient = RestClient.create(getServiceUrl());
    }

    private String getServiceUrl() {
        var instances = discoveryClient.getInstances(VIRTUAL_HOSTNAME);
        if (instances != null && !instances.isEmpty()) {
            var instance = instances.getFirst();
            return "http://" + instance.getHost() + ":" + instance.getPort();
        }
        throw new RuntimeException("Service not found: " + VIRTUAL_HOSTNAME);
    }

    public void hit(@Valid EndpointHit hitDto) {
        restClient.post().uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStats> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    Boolean unique) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve().body(new ParameterizedTypeReference<>() {
                });
    }
}