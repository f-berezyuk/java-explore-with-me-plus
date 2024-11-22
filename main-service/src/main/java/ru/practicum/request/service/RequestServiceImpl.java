package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.common.ConflictException;
import ru.practicum.common.NotFoundException;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<RequestDto> getRequests(long userId) {
        log.info("Fetching requests for user with ID: {}", userId);
        User user = findUserById(userId);
        List<RequestDto> requests = requestMapper.toDtos(requestRepository.findByRequesterId(user.getId()));
        log.info("Found {} requests for user with ID: {}", requests.size(), userId);
        return requests;
    }

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        log.info("Creating request for user with ID: {} and event with ID: {}", userId, eventId);
        Event event = findEventById(eventId);
        User user = findUserById(userId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.error("Request already exists for user ID: {} and event ID: {}", userId, eventId);
            throw new ConflictException("Request already exists");
        }

        if (event.getUser().getId().equals(user.getId())) {
            log.error("User ID: {} is the initiator of event ID: {}", userId, eventId);
            throw new ConflictException("Request can't be created by initiator");
        }

        if (event.getState() != EventState.PUBLISHED) {
            log.error("Event ID: {} is not yet published", eventId);
            throw new ConflictException("Event not yet published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();
        if (event.getParticipantLimit() > 0 && !event.isRequestModeration() && event.getParticipantLimit() <= requestsSize) {
            log.error("Participant limit exceeded for event ID: {}", eventId);
            throw new ConflictException("Participant limit exceeded");
        }

        Request eventRequest = new Request(null, LocalDateTime.now(), event, user, RequestStatus.PENDING);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            eventRequest.setStatus(RequestStatus.CONFIRMED);
        }

        RequestDto createdRequest = requestMapper.toDto(requestRepository.save(eventRequest));
        log.info("Request created successfully for user ID: {} and event ID: {}", userId, eventId);
        return createdRequest;
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        log.info("Cancelling request with ID: {} for user with ID: {}", requestId, userId);
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() -> new NotFoundException(MessageFormat.format("Request with id={0} was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        RequestDto canceledRequest = requestMapper.toDto(requestRepository.save(request));
        log.info("Request with ID: {} cancelled successfully for user with ID: {}", requestId, userId);
        return canceledRequest;
    }

    private User findUserById(long userId) {
        log.debug("Finding user with ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with ID: {} not found", userId);
            return new NotFoundException(MessageFormat.format("User with id={0} was not found", userId));
        });
    }

    private Event findEventById(long eventId) {
        log.debug("Finding event with ID: {}", eventId);
        return eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID: {} not found", eventId);
            return new NotFoundException(MessageFormat.format("Event with id={0} was not found", eventId));
        });
    }

    @Override
    public List<RequestDto> getRequests(long userId, long eventId) {
        log.info("Fetching requests for event ID: {} by user with ID: {}", eventId, userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID: {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        if (!event.getUser().getId().equals(userId)) {
            log.error("User ID: {} is not the owner of event ID: {}", userId, eventId);
            throw new NotFoundException("User is not the owner of the event");
        }

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        log.info("Found {} requests for event ID: {} by user ID: {}", requests.size(), eventId, userId);
        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIds(long userId, long eventId, List<Long> requestIds) {
        log.info("Fetching specific requests for event ID: {} by user with ID: {}", eventId, userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID: {} not found", eventId);
            return new NotFoundException("Event not found");
        });

        if (!event.getUser().getId().equals(userId)) {
            log.error("User ID: {} is not the owner of event ID: {}", userId, eventId);
            throw new NotFoundException("User is not the owner of the event");
        }

        List<Request> requests = requestRepository.findAllById(requestIds);

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                log.error("Request with ID: {} does not belong to event ID: {}", request.getId(), eventId);
                throw new NotFoundException("Request does not belong to the specified event");
            }
        }

        log.info("Found {} requests for event ID: {} by user ID: {}", requests.size(), eventId, userId);
        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> saveAll(List<RequestDto> requests) {
        log.info("Saving batch of {} requests", requests.size());
        List<Request> requestEntities = requests.stream().map(requestMapper::toEntity).collect(Collectors.toList());
        List<Request> savedRequests = requestRepository.saveAll(requestEntities);
        log.info("Successfully saved {} requests", savedRequests.size());
        return savedRequests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }
}
