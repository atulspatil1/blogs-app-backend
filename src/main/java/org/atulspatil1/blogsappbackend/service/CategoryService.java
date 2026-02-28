package org.atulspatil1.blogsappbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.blogsappbackend.dto.CategoryResponse;
import org.atulspatil1.blogsappbackend.model.Category;
import org.atulspatil1.blogsappbackend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> categories = categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("event=category.list.fetched count={}", categories.size());
        return categories;
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .build();
    }
}
