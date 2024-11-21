package ru.practicum.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @Autowired
    public AdminEventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users, @RequestParam(required = false) List<String> states, @RequestParam(required = false) List<Long> categories, @RequestParam(required = false) String rangeStart, @RequestParam(required = false) String rangeEnd, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {

        LocalDateTime start = rangeStart != null ? LocalDateTime.parse(rangeStart) : null;
        LocalDateTime end = rangeEnd != null ? LocalDateTime.parse(rangeEnd) : null;

        return eventService.getAllEvents(users, states, categories, start, end, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        try {
            return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
        } catch (NotFoundException e) {
            throw new NotFoundException("Event with id " + eventId + " not found");
        } catch (ConflictException e) {
            throw new ConflictException("Event cannot be updated: " + e.getMessage());
        }
    }
}
