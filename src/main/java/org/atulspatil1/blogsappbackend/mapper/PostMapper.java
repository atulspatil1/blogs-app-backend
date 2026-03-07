package org.atulspatil1.blogsappbackend.mapper;

import org.atulspatil1.blogsappbackend.dto.CommentResponse;
import org.atulspatil1.blogsappbackend.dto.PostDetailResponse;
import org.atulspatil1.blogsappbackend.dto.PostSummaryResponse;
import org.atulspatil1.blogsappbackend.model.Category;
import org.atulspatil1.blogsappbackend.model.Comment;
import org.atulspatil1.blogsappbackend.model.Post;
import org.atulspatil1.blogsappbackend.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "categoryNames")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagNames")
    PostSummaryResponse toSummary(Post post);

    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "categories", target = "categories", qualifiedByName = "categoryNames")
    @Mapping(source = "tags", target = "tags", qualifiedByName = "tagNames")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "approvedComments")
    PostDetailResponse toDetail(Post post);

    @Named("statusToString")
    default String statusToString(Post.Status status) {
        return status == null ? null : status.name();
    }

    @Named("categoryNames")
    default Set<String> categoryNames(Set<Category> categories) {
        if (categories == null) return Collections.emptySet();
        return categories.stream().map(Category::getName).collect(Collectors.toSet());
    }

    @Named("tagNames")
    default Set<String> tagNames(Set<Tag> tags) {
        if (tags == null) return Collections.emptySet();
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }

    @Named("approvedComments")
    default List<CommentResponse> approvedComments(List<Comment> comments) {
        if (comments == null) return null;
        return comments.stream()
                .filter(Comment::getApproved)
                .map(c -> CommentResponse.builder()
                        .id(c.getId())
                        .authorName(c.getAuthorName())
                        .body(c.getBody())
                        .approved(c.getApproved())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
