package org.atulspatil1.blogsappbackend.controller;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.dto.TagResponse;
import org.atulspatil1.blogsappbackend.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponse>> getAll() {
        return ResponseEntity.ok(tagService.getAllTags());
    }
}
