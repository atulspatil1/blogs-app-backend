package org.atulspatil1.blogsappbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {

    private Long id;
    private String authorName;
    private String body;
    private Boolean approved;
    private LocalDateTime createdAt;
}
