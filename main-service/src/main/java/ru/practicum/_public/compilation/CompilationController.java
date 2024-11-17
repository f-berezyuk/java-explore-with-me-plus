package ru.practicum._public.compilation;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.CompilationService;
import ru.practicum.compilation.dto.CompilationDto;

@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false, value = "false") boolean pinned,
                                                @RequestParam(required = false, value = "0") int from,
                                                @RequestParam(required = false, value = "10") int size) {
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long id) {
        return compilationService.get(id);
    }
}
