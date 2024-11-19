package ru.practicum.request.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;
import ru.practicum.statistic.service.StatService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final StatService statService;

    @GetMapping
    List<RequestDto> getRequests(@PathVariable long userId, HttpServletRequest httpServletRequest) {
        List<RequestDto> requestDtos = requestService.getRequests(userId);
        statService.createStats(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
        return requestDtos;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RequestDto createRequest(@PathVariable long userId, @RequestParam long eventId, HttpServletRequest httpServletRequest) {
        RequestDto requestDto = requestService.createRequest(userId, eventId);
        statService.createStats(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
        return requestDto;
    }

    @PatchMapping("/{requestId}/cancel")
    RequestDto requestCancel(@PathVariable long requestId, @PathVariable long userId, HttpServletRequest httpServletRequest) {
        RequestDto requestDto = requestService.cancelRequest(userId, requestId);
        statService.createStats(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
        return requestDto;
    }
}
