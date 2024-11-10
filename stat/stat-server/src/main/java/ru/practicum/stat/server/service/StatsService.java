package ru.practicum.stat.server.service;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.model.EndpointHitEntity;

public interface StatsService {
    List<ViewStats> getStats(@Valid @Past LocalDateTime start,
                             @Valid @PastOrPresent LocalDateTime end,
                             List<String> uris,
                             boolean unique);

    EndpointHitEntity hit(EndpointHitEntity hit);
}
