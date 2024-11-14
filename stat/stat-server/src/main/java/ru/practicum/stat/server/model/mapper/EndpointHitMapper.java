package ru.practicum.stat.server.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stat.dto.EndpointHit;
import ru.practicum.stat.server.model.EndpointHitEntity;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHit toDto(EndpointHitEntity entity);

    EndpointHitEntity toEntity(EndpointHit dto);
}
