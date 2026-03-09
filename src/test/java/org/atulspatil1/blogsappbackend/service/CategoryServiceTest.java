package org.atulspatil1.blogsappbackend.service;

import org.atulspatil1.blogsappbackend.dto.CategoryResponse;
import org.atulspatil1.blogsappbackend.mapper.CategoryMapper;
import org.atulspatil1.blogsappbackend.model.Category;
import org.atulspatil1.blogsappbackend.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("getAllCategories — returns mapped list")
    void getAllCategories_returnsMappedList() {
        Category cat = Category.builder().id(1L).name("Java").slug("java").build();
        CategoryResponse resp = CategoryResponse.builder().id(1L).name("Java").slug("java").build();

        when(categoryRepository.findAll()).thenReturn(List.of(cat));
        when(categoryMapper.toResponse(cat)).thenReturn(resp);

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Java");
    }
}
