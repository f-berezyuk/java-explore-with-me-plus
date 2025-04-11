package ru.practicum.EWM.stat.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.EWM.stat.dto.EndpointHit;
import ru.practicum.EWM.stat.server.model.EndpointHitEntity;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHit toDto(EndpointHitEntity entity);

    @Mapping(target = "id", ignore = true)
    EndpointHitEntity toEntity(EndpointHit dto);
}
