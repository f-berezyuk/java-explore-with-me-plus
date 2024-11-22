package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserService userService;
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final EventMapper mapper;
    private final ReqMapper requestMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        log.info("Fetching events for user with ID: {}", userId);
        List<EventShortDto> events = eventRepository.findByUser_Id(userId);
        log.debug("Total events fetched for user ID {}: {}", userId, events.size());
        int endIndex = Math.min(from + size, events.size());
        if (from > events.size()) {
            log.warn("Requested starting index {} is greater than total events size: {}", from, events.size());
            return new ArrayList<>();
        }
        return events.subList(from, endIndex);
    }

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.info("Adding new event for user ID: {}", userId);
        Event event = mapper.toEntity(newEventDto);

        UserDto userDto = userService.getUser(userId);
        event.setUser(userMapper.toEntity(userDto));

        final Location requestLocation = event.getLocation();
        Location mayBeExistingLocation = null;
        if (requestLocation.getId() == null) {
            mayBeExistingLocation = locationRepository.findByLatAndLon(requestLocation.getLat(), requestLocation.getLon()).orElseGet(() -> {
                log.debug("Saving new location for event: {}", requestLocation);
                return locationRepository.save(requestLocation);
            });
        }
        event.setLocation(mayBeExistingLocation);
        event = eventRepository.save(event);
        log.info("New event added with ID: {}", event.getId());

        return mapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(Long userId, Long eventId) {
        log.info("Fetching event with ID {} for user ID: {}", eventId, userId);
        Event event = eventRepository.findByIdAndUser_Id(eventId, userId).orElseThrow(() -> {
            log.error("Event with ID {} and user ID {} not found", eventId, userId);
            return new NotFoundException("Event with id " + eventId + " and user id " + userId + " was not found");
        });
        return mapper.toFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.info("Updating event with ID {} for user ID: {}", eventId, userId);
        Event event = eventRepository.findByIdAndUser_Id(eventId, userId).orElseThrow(() -> {
            log.error("Event with ID {} not found for user ID {}", eventId, userId);
            return new NotFoundException("Event with id " + eventId + " was not found");
        });

        if (event.getState() == EventState.PUBLISHED) {
            log.error("Event with ID {} is already published", eventId);
            throw new ConflictException("Event with id " + eventId + " has already been published");
        }

        if (request.getLocation() != null) {
            log.debug("Updating location for event ID: {}", eventId);
            final Location newLocation = request.getLocation();
            Location updatedLocation = null;
            if (newLocation.getId() == null) {
                updatedLocation = locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon()).orElseGet(() -> {
                    log.debug("Saving new location: {}", newLocation);
                    return locationRepository.save(newLocation);
                });
            }
            event.setLocation(updatedLocation);
        }

        mapper.updateFromUserRequest(request, event);
        log.info("Event with ID {} updated successfully", eventId);
        return mapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        log.info("Fetching requests for event ID {} and user ID: {}", eventId, userId);
        return requestService.getRequests(userId, eventId);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("Updating requests for event ID {} and user ID: {}", eventId, userId);
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
        log.info("Requests updated for event ID {}: confirmed = {}, rejected = {}", eventId, confirmedRequests.size(), rejectedRequests.size());

        return EventRequestStatusUpdateResult.builder().confirmedRequests(confirmedRequests).rejectedRequests(rejectedRequests).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        log.info("Fetching public events with filters: text = {}, categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        if (rangeStart == null) rangeStart = LocalDateTime.now();
        if (rangeEnd == null) rangeEnd = LocalDateTime.MAX;

        List<Event> events = eventRepository.findPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        log.debug("Total public events fetched: {}", events.size());
        return events.stream().skip(from).limit(size).map(mapper::toShortDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEvent(Long id) {
        log.info("Fetching public event with ID: {}", id);
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> {
            log.error("Public event with ID {} not found or not published", id);
            return new NotFoundException("Event with id " + id + " not found or not published");
        });
        return mapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        log.info("Fetching all events with filters: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}", users, states, categories, rangeStart, rangeEnd);
        if (rangeStart == null) rangeStart = LocalDateTime.MIN;
        if (rangeEnd == null) rangeEnd = LocalDateTime.MAX;

        List<Event> events = eventRepository.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd);
        log.debug("Total events fetched for admin: {}", events.size());
        return events.stream().skip(from).limit(size).map(mapper::toFullDto).toList();
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        log.info("Admin updating event with ID: {}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID {} not found", eventId);
            return new NotFoundException("Event with id " + eventId + " not found");
        });

        mapper.updateFromAdminRequest(request, event);
        log.info("Event with ID {} updated by admin successfully", eventId);
        return mapper.toFullDto(eventRepository.save(event));
    }
}
