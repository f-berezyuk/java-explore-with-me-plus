package ru.practicum.stat.server.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<ViewStats> getStats(@Valid @Past LocalDateTime start, @Valid @PastOrPresent LocalDateTime end, List<String> uris, boolean unique);

    EndpointHit hit(EndpointHit hit);
}
