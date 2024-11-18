package ru.practicum.request.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;
import ru.practicum.statistic.service.StatService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final StatService statService;

    @GetMapping
    List<RequestDto> getRequests(@PathVariable Long userId, HttpServletRequest httpServletRequest) {
        List<RequestDto> requestDtos = requestService.getRequests(userId);
        statService.createStats(httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());
        return requestDtos;
    }
}
