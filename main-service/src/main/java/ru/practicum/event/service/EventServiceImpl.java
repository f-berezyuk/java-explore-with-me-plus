package ru.practicum.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.ReqMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.service.RequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {
    private final RequestService requestService;
    private final EventRepository eventRepository;
    private final EventMapper mapper;
    private final ReqMapper requestMapper;

    @Autowired
    public EventServiceImpl(RequestService requestService, EventRepository eventRepository, EventMapper mapper, ReqMapper requestMapper) {
        this.requestService = requestService;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
        this.requestMapper = requestMapper;
    }

    @Override
    public List<EventShortDto> getAllByUserId(Long userId, int from, int size) {
        List<EventShortDto> events = eventRepository.findByUser_Id(userId);
        return events.subList(from, from + size);
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = mapper.toEntity(newEventDto);
        return mapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Optional<Event> optional = eventRepository.findById(eventId, userId);
        return mapper.toFullDto(optional.orElseThrow(() -> new NotFoundException("Event with id " + eventId + " and user id " + userId + " was not found")));
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        Optional<Event> optional = eventRepository.findById(eventId);
        Event event = optional.orElseThrow(() -> new NotFoundException("Event with id " + eventId + "was not found"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event with id " + eventId + " has already been published");
        }
        mapper.updateFromUserRequest(request, event);
        return mapper.toFullDto(eventRepository.save(event));
    }

    @Override
    public List<RequestDto> getRequests(Long userId, Long eventId) {
        return requestService.getRequests(userId, eventId);
    }

    @Override
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
    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, int from, int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }

        List<Event> events = eventRepository.findPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return events.stream().skip(from).limit(size).map(mapper::toShortDto).toList();
    }

    @Override
    public EventFullDto getPublicEvent(Long id) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id " + id + " not found or not published"));
        return mapper.toFullDto(event);
    }


    @Override
    public List<EventFullDto> getAllEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }

        List<Event> events = eventRepository.findEventsForAdmin(users, states, categories, rangeStart, rangeEnd);
        return events.stream().skip(from).limit(size).map(mapper::toFullDto).toList();
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest request) {
        Optional<Event> optional = eventRepository.findById(eventId);
        Event event = optional.orElseThrow(() -> new NotFoundException("Event with id " + eventId + " was not found"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Event with id " + eventId + " has already been published");
        }

        if (request.getStateAction() != null && event.getState() != EventState.PENDING && request.getStateAction().equalsIgnoreCase("PUBLISHED")) {
            if (event.getPublishedOn().isBefore(LocalDateTime.now().minusHours(1))) {
                throw new ConflictException("Cannot publish the event because it's not in the right state");
            }
        }

        if (request.getStateAction() != null && request.getStateAction().equalsIgnoreCase("CANCELED") && event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Cannot cancel the event because it has been published");
        }

        mapper.updateFromAdminRequest(request, event);
        return mapper.toFullDto(eventRepository.save(event));
    }

}
