package ru.practicum.event.mapper;

import org.mapstruct.*;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "state", constant = "PENDING")
//    @Mapping(target = "category.id", source = "category")
//    @Mapping(target = "user", ignore = true)
//    Event toEntity(NewEventDto dto);

//    Event toEntity(EventShortDto dto);

    EventShortDto toShortDto(Event event);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "category.id", source = "category")
//    void updateEventFromAdminRequest(UpdateEventAdminRequest request, @MappingTarget Event event);

//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "category.id", source = "category")
//    void updateEventFromUserRequest(UpdateEventUserRequest request, @MappingTarget Event event);
}
