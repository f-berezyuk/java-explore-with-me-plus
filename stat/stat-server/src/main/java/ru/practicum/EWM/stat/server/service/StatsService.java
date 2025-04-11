package ru.practicum.EWM.stat.server.service;

import ru.practicum.EWM.stat.dto.EndpointHit;
import ru.practicum.EWM.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStats> getStats(LocalDateTime start,
                             LocalDateTime end,
                             List<String> uris,
                             boolean unique);

    EndpointHit saveHit(EndpointHit hit);
}
