package org.atulspatil1.blogsappbackend.controller;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.config.ApiRoutes;
import org.atulspatil1.blogsappbackend.dto.CategoryResponse;
import org.atulspatil1.blogsappbackend.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({ApiRoutes.CATEGORIES_V1, ApiRoutes.CATEGORIES_LEGACY})
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
