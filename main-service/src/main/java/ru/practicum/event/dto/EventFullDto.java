package ru.practicum.event.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.dto.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private Integer participantLimit = 0;
    private LocalDateTime publishedOn;
    private boolean requestModeration = true;
    private EventState state;
    private String title;
    private Long views;
}

