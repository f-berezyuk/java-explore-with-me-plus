package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text, @RequestParam(required = false) List<Long> categories, @RequestParam(required = false) Boolean paid, @RequestParam(required = false) LocalDateTime rangeStart, @RequestParam(required = false) LocalDateTime rangeEnd, @RequestParam(defaultValue = "false") Boolean onlyAvailable, @RequestParam(required = false) String sort, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return eventService.getPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id) {
        return eventService.getPublicEvent(id);
    }
}