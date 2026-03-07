package org.atulspatil1.blogsappbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.blogsappbackend.dto.TagResponse;
import org.atulspatil1.blogsappbackend.mapper.TagMapper;
import org.atulspatil1.blogsappbackend.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagResponse> getAllTags() {
        List<TagResponse> tags = tagRepository.findAll().stream()
                .map(tagMapper::toResponse)
                .collect(Collectors.toList());
        log.info("event=tag.list.fetched count={}", tags.size());
        return tags;
    }
}
