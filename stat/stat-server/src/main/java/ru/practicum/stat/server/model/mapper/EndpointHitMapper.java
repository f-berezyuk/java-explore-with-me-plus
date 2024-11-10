package ru.practicum.stat.server.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.stat.server.model.EndpointHitEntity;
import ru.practicum.stat.dto.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);

    EndpointHit toDto(EndpointHitEntity entity);

    EndpointHitEntity toEntity(EndpointHit dto);
}
