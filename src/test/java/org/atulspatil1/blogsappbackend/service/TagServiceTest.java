package org.atulspatil1.blogsappbackend.service;

import org.atulspatil1.blogsappbackend.dto.TagResponse;
import org.atulspatil1.blogsappbackend.mapper.TagMapper;
import org.atulspatil1.blogsappbackend.model.Tag;
import org.atulspatil1.blogsappbackend.repository.TagRepository;
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
class TagServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private TagMapper tagMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    @DisplayName("getAllTags — returns mapped list")
    void getAllTags_returnsMappedList() {
        Tag tag = Tag.builder().id(1L).name("JWT").slug("jwt").build();
        TagResponse resp = TagResponse.builder().id(1L).name("JWT").slug("jwt").build();

        when(tagRepository.findAll()).thenReturn(List.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(resp);

        List<TagResponse> result = tagService.getAllTags();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("JWT");
    }
}
