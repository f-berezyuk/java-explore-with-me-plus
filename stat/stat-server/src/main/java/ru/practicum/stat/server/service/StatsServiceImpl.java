package ru.practicum.stat.server.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.model.EndpointHitEntity;
import ru.practicum.stat.server.repository.EndpointHitEntityRepository;

@Service
public class StatsServiceImpl implements StatsService {
    private final EndpointHitEntityRepository repository;

    @Autowired
    public StatsServiceImpl(EndpointHitEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return List.of();
    }

    @Override
    public EndpointHitEntity hit(EndpointHitEntity endpointHit) {
        return repository.save(endpointHit);
    }
}
