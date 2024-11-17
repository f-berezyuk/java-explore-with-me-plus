package ru.practicum.compilation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.common.NotFoundException;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

@Service
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationsRepository compilationsRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    private static void assertCompletion(List<Long> eventIds, List<Event> events) throws BadRequestException {
        if (eventIds.size() != events.size()) {
            eventIds.removeAll(events.stream().map(Event::getId).toList());
            throw new BadRequestException("Событий с id {" + eventIds + "} не существует");
        }
    }

    @Override
    public List<CompilationDto> getAll(boolean pinned, int from, int size) {
        var page = from / size;
        var sort = Sort.by("id");
        var pageable = PageRequest.of(page, size, sort);
        var compilationPage = compilationsRepository.findByPinned(pinned, pageable);
        var compilations = compilationPage.getContent();

        return compilations.isEmpty()
                ? Collections.emptyList()
                : compilations.stream().map(compilationMapper::toCompilationDto).toList();
    }

    @Override
    public CompilationDto get(Long id) {
        return compilationsRepository.findById(id)
                .map(compilationMapper::toCompilationDto)
                .orElse(null);
    }

    @Override
    public CompilationDto addNewCompilation(NewCompilationDto newCompilationDto) throws BadRequestException {
        var compilationBuilder = Compilation.builder().pinned(newCompilationDto.getPinned());
        var eventIds = newCompilationDto.getEvents();
        if (eventIds != null) {
            List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            assertCompletion(eventIds, events);
            compilationBuilder.events(events);

        }

        Optional.ofNullable(newCompilationDto.getTitle()).ifPresent(compilationBuilder::title);

        var result = compilationsRepository.saveAndFlush(compilationBuilder.build());
        return compilationMapper.toCompilationDto(result);
    }

    @Override
    public void delete(Long id) {
        var compilation = getOrElseThrow(id);

        compilationsRepository.delete(compilation);
    }

    public Compilation getOrElseThrow(Long id) {
        return compilationsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
    }

    @Override
    public CompilationDto update(Long id, UpdateCompilationRequest request) throws BadRequestException {
        var compilation = getOrElseThrow(id);
        var updateBuilder = compilation.toBuilder();
        var eventsIds = request.getEvents();

        if (eventsIds != null) {
            var events = eventRepository.findAllByIdIn(eventsIds);
            assertCompletion(eventsIds, events);
            updateBuilder.events(events);
        }

        if (request.getPinned() != null) {
            updateBuilder.pinned(request.getPinned());
        }

        if (request.getTitle() != null) {
            updateBuilder.title(request.getTitle());
        }

        var result = compilationsRepository.saveAndFlush(updateBuilder.build());

        return compilationMapper.toCompilationDto(result);
    }
}
