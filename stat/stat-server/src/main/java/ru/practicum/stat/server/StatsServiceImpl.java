package ru.practicum.stat.server;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.dto.ViewStats;

@Service
public class StatsServiceImpl implements StatsService {
    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return List.of();
    }

    @Override
    public void hit(EndpointHit hit) {

    }
}
