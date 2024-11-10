package ru.practicum.stat.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.model.mapper.EndpointHitMapper;
import ru.practicum.stat.server.repository.EndpointHitEntityRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final EndpointHitEntityRepository repository;
    private final EndpointHitMapper mapper;

    @Autowired
    public StatsServiceImpl(EndpointHitEntityRepository repository, EndpointHitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return List.of();
    }

    @Override
    public EndpointHit hit(EndpointHit endpointHit) {
        return mapper.toDto(repository.save(mapper.toEntity(endpointHit)));
    }
}
