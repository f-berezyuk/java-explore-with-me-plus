package ru.practicum.event.service;

import java.util.List;

import jakarta.validation.Valid;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.model.Request;

public interface EventService {
    List<EventShortDto> getAllByUserId(Long userId, int from, int size);

    EventFullDto addEvent(Long userId, @Valid NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, @Valid UpdateEventUserRequest request);

    List<Request> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request);
}
