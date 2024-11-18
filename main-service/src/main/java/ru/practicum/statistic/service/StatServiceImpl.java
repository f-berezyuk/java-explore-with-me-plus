package ru.practicum.statistic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.stat.dto.EndpointHit;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatClient statClient;
    private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String APP_NAME = "ewm";

    @Override
    public void createStats(String uri, String ip) {
        log.info("Creat stats for URI: {}, IP: {}", uri, ip);
        EndpointHit hitDto = EndpointHit.builder()
                .uri(uri)
                .ip(ip)
                .app(APP_NAME)
                .build();

        statClient.hit(hitDto);
    }
}
