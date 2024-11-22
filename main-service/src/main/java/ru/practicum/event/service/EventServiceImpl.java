package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.ReqMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper;
    private final ReqMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        List<EventShortDto> events = eventRepository.findByUser_Id(userId);
        int endIndex = Math.min(from + size, events.size());
        if (from > events.size()) {
            return new ArrayList<>();
        }
        return events.subList(from, endIndex);
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = mapper.toEntity(newEventDto);

        final Location requestLocation = event.getLocation();
        Location mayBeExistingLocation = null;
        if (requestLocation.getId() == null) {
            mayBeExistingLocation = locationRepository.findByLatAndLon(requestLocation.getLat(), requestLocation.getLon()).orElseGet(() -> locationRepository.save(requestLocation));
        }

        event.setLocation(mayBeExistingLocation);
        event = eventRepository.save(event);

        return mapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndUser_Id(eventId, userId).orElseThrow(() -> new NotFoundException("Event with id " + eventId + " and user id " + userId + " was not found"));
        return mapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Event event = eventRepository.findByIdAndUser_Id(eventId, userId).orElseThrow(() -> new NotFoundException("Event with id " + eventId + " was not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Event with id " + eventId + " has already been published");
        }

        if (request.getLocation() != null) {
            final Location newLocation = request.getLocation();
            Location updatedLocation = null;
            if (newLocation.getId() == null) {
                updatedLocation = locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon()).orElseGet(() -> locationRepository.save(newLocation));
            }

            event.setLocation(updatedLocation);

        }

        mapper.updateFromUserRequest(request, event);
        return mapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        return requestService.getRequests(userId, eventId);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        List<RequestDto> requests = requestService.getRequestsByUserIdAndEventIdAndRequestIds(userId, eventId, updateRequest.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (RequestDto request : requests) {
            if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(requestMapper.toParticipationRequestDto(request));
            } else if (updateRequest.getStatus() == RequestStatus.REJECTED) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toParticipationRequestDto(request));
            }
        }

        requestService.saveAll(requests);

        return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmedRequests).rejectedRequests(rejectedRequests).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd == null) rangeEnd = LocalDateTime.MAX;

        List<Event> events = eventRepository.findPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return events.stream().skip(from).limit(size).map(mapper::toShortDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEvent(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id " + id + " not found or not published"));
        return mapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart == null) rangeStart = LocalDateTime.MIN;
        if (rangeEnd == null) rangeEnd = LocalDateTime.MAX;

        List<Event> events = eventRepository.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd);
        return events.stream().skip(from).limit(size).map(mapper::toFullDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id " + eventId + " was not found"));

        validateEventStateForAdminUpdate(event, request.getStateAction());

        Location location = request.getLocation();
        if (location != null && location.getId() == null) {
            locationRepository.save(location);
        }

        mapper.updateFromAdminRequest(request, event);

        return mapper.toFullDto(eventRepository.save(event));
    }

    private void validateEventStateForAdminUpdate(Event event, String stateAction) {
        if ("PUBLISHED".equalsIgnoreCase(stateAction) && event.getState() != EventState.PENDING) {
            throw new ConflictException("Cannot publish the event because it's not in the right state");
        }
        if ("CANCELED".equalsIgnoreCase(stateAction) && event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot cancel the event because it has been published");
        }
    }
}
