package compilation.mapper;

import compilation.dto.CompilationDto;
import compilation.model.Compilation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.mapper.EventMapper;

@Component
@AllArgsConstructor
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream().map(EventMapper::toShortDto).toList())
                .build();
    }
}