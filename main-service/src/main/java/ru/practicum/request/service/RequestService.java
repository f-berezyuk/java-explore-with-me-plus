package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    List<RequestDto> getRequests(long userId, long eventId);

    List<RequestDto> getRequests(long userId);

    List<RequestDto> getRequestsByUserIdAndEventIdAndRequestIds(long userId, long eventId, List<Long> RequestIds);

    RequestDto createRequest(long userId, long eventId);

    List<RequestDto> saveAll(List<RequestDto> requests);

    RequestDto cancelRequest(long userId, long requestId);
}
