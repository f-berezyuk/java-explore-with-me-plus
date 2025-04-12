package ru.practicum.EWM.stats.server.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.EWM.stats.dto.EndpointHit;
import ru.practicum.EWM.stats.dto.ViewStats;
import ru.practicum.EWM.stats.server.service.StatsService;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Valid EndpointHit hit) {
        statsService.saveHit(hit);
    }
}
