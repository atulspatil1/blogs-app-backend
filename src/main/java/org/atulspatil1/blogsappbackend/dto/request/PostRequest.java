package org.atulspatil1.blogsappbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.atulspatil1.blogsappbackend.model.Post;

import java.util.Set;

@Data
public class PostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    //Slug is auto-generated from title if not provided
    private String slug;

    private String summary;

    @NotBlank(message = "Content is required")
    private String content;

    private String coverImageUrl;

    @NotNull(message = "Status is required")
    private Post.Status status;

    private Set<Long> categoryIds;

    private Set<Long> tagIds;
}
