package org.atulspatil1.blogsappbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagResponse {

    private Long id;
    private String name;
    private String slug;
}
