package ru.practicum.stat.client;

import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.StatRequest;
import ru.practicum.stat.dto.ViewStats;

import java.util.List;

public interface StatClient {
    void hit(EndpointHit hitDto);

    List<ViewStats> get(StatRequest statsRequestParamsDto);
}