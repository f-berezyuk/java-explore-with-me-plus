package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<RequestDto> getRequests(long userId) {
        User user = findUserById(userId);
        return requestMapper.toDtos(requestRepository.findByRequesterId(user.getId()));
    }

    @Override
    public RequestDto createRequest(long userId, long eventId) {
        Event event = findEventById(eventId);
        User user = findUserById(userId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exist");
        }

        if (event.getUser().getId().equals(user.getId())) {
            throw new ConflictException("Request can't be created by initiator");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event not yet published");
        }

        int requestsSize = requestRepository.findAllByEventId(eventId).size();
        if (event.getParticipantLimit() > 0 && !event.isRequestModeration() && event.getParticipantLimit() <= requestsSize) {
            throw new ConflictException("Participant limit exceeded");
        }

        Request eventRequest = new Request(null, LocalDateTime.now(), event, user,  RequestStatus.PENDING);
        if (!event.isRequestModeration()) {
            eventRequest.setStatus(RequestStatus.CONFIRMED);
        }

        if (event.getParticipantLimit() == 0) {
            eventRequest.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toDto(requestRepository.save(eventRequest));
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId).orElseThrow(() ->
                new NotFoundException(MessageFormat.format("Request with id={0} was not found", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("User with id={0} was not found",
                        userId)));
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(MessageFormat.format("Event with id={0} was not found",
                        eventId)));
    }
}
