package org.atulspatil1.blogsappbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class PostSummaryResponse {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String coverImageUrl;
    private String status;
    private String authorUsername;
    private Set<String> categories;
    private Set<String> tags;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
