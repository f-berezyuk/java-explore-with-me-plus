package ru.practicum.categories.service;

import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoriesService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long id, NewCategoryDto newCategoryDto);

    void deleteCategory(Long id);

    CategoryDto findBy(Long id);

    List<CategoryDto> findBy(int from, int size);
}
