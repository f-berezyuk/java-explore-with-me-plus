package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id;
    private CategoryDto category;
    private UserShortDto user;
    private String title;
    private String annotation;
    private int confirmedRequests;
    private int views;
    private Boolean paid;
    private String eventDate;
}
