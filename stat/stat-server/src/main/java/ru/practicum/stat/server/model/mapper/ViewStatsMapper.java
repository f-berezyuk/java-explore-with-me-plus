package ru.practicum.stat.server.model.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stat.dto.ViewStats;
import ru.practicum.stat.server.projection.ViewStatsProjection;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStats toDto(ViewStatsProjection projection);
}
