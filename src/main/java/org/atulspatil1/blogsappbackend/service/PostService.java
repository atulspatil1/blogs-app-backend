package org.atulspatil1.blogsappbackend.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.dto.PostDetailResponse;
import org.atulspatil1.blogsappbackend.dto.PostSummaryResponse;
import org.atulspatil1.blogsappbackend.dto.request.PostRequest;
import org.atulspatil1.blogsappbackend.exception.ResourceNotFoundException;
import org.atulspatil1.blogsappbackend.model.*;
import org.atulspatil1.blogsappbackend.repository.CategoryRepository;
import org.atulspatil1.blogsappbackend.repository.PostRepository;
import org.atulspatil1.blogsappbackend.repository.TagRepository;
import org.atulspatil1.blogsappbackend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    //Public
    public Page<PostSummaryResponse> getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findByStatus(Post.Status.PUBLISHED, pageable)
                .map(this::toSummary);
    }

    public PostDetailResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with slug: " + slug));
        return toDetail(post);
    }

    public Page<PostSummaryResponse> getPostsByCategory(String categorySlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findPublishedByCategorySlug(categorySlug, pageable).map(this::toSummary);
    }

    public Page<PostSummaryResponse> getPostsByTag(String tagSlug, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.findPublishedByTagSlug(tagSlug, pageable).map(this::toSummary);
    }

    public Page<PostSummaryResponse> searchPosts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return postRepository.searchPublished(query, pageable).map(this::toSummary);
    }

    //Admin
    public Page<PostSummaryResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable).map(this::toSummary);
    }

    @Transactional
    public PostDetailResponse createPost(PostRequest request, String authorEmail) {
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .slug(resolveSlug(request))
                .summary(request.getSummary())
                .content(request.getContent())
                .coverImageUrl(request.getCoverImageUrl())
                .status(request.getStatus())
                .author(author)
                .categories(resolveCategoriesByIds(request.getCategoryIds()))
                .tags(resolveTagsByIds(request.getTagIds()))
                .build();

        if(request.getStatus() == Post.Status.PUBLISHED) {
            post.setPublishedAt(LocalDateTime.now());
        }

        return toDetail(postRepository.save(post));
    }

    @Transactional
    public PostDetailResponse updatePost(long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        post.setTitle(request.getTitle());
        post.setSlug(resolveUpdateSlug(post, request));
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setCoverImageUrl(request.getCoverImageUrl());
        if(request.getCategoryIds() != null) {
            post.setCategories(resolveCategoriesByIds(request.getCategoryIds()));
        }
        if(request.getTagIds() != null) {
            post.setTags(resolveTagsByIds(request.getTagIds()));
        }

        if(request.getStatus() == Post.Status.PUBLISHED && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        post.setStatus(request.getStatus());

        return toDetail(postRepository.save(post));
    }

    public void deletePost(Long id) {
        if(!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }

    //Helpers
    private String resolveSlug(PostRequest request) {
        return (request.getSlug() != null && !request.getSlug().isBlank())
                ? request.getSlug()
                : slugify(request.getTitle());
    }

    private String resolveUpdateSlug(Post post, PostRequest request) {
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            return request.getSlug();
        }
        return post.getSlug();
    }

    public static String slugify(String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "")
                .replaceAll("-+", "-")
                .strip();
    }

    private Set<Category> resolveCategoriesByIds(Set<Long> ids) {
        if(ids == null || ids.isEmpty())
            return new HashSet<>();

        return ids.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)))
                .collect(Collectors.toSet());
    }

    private Set<Tag> resolveTagsByIds(Set<Long> ids) {
        if(ids == null || ids.isEmpty())
            return new HashSet<>();

        return ids.stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id)))
                .collect(Collectors.toSet());
    }

    private PostSummaryResponse toSummary(Post post) {
        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .coverImageUrl(post.getCoverImageUrl())
                .status(post.getStatus().name())
                .authorUsername(post.getAuthor().getUsername())
                .categories(post.getCategories().stream().map(Category::getName).collect(Collectors.toSet()))
                .tags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private PostDetailResponse toDetail(Post post) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .summary(post.getSummary())
                .content(post.getContent())
                .coverImageUrl(post.getCoverImageUrl())
                .status(post.getStatus().name())
                .authorUsername(post.getAuthor().getUsername())
                .categories(post.getCategories().stream().map(Category::getName).collect(Collectors.toSet()))
                .tags(post.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .comments(post.getComments() == null ? null :
                        post.getComments().stream()
                                .filter(Comment::getApproved)
                                .map(c -> org.atulspatil1.blogsappbackend.dto.CommentResponse.builder()
                                        .id(c.getId())
                                        .authorName(c.getAuthorName())
                                        .body(c.getBody())
                                        .approved(c.getApproved())
                                        .createdAt(c.getCreatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
