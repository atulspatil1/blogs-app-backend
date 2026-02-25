package org.atulspatil1.blogsappbackend.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.dto.TagResponse;
import org.atulspatil1.blogsappbackend.model.Tag;
import org.atulspatil1.blogsappbackend.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }
}
