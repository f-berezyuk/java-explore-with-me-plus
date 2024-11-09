package ru.practicum.stat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
