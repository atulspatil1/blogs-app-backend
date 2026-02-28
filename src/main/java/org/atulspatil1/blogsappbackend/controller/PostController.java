package org.atulspatil1.blogsappbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.config.ApiRoutes;
import org.atulspatil1.blogsappbackend.dto.PostDetailResponse;
import org.atulspatil1.blogsappbackend.dto.PostSummaryResponse;
import org.atulspatil1.blogsappbackend.dto.request.PostRequest;
import org.atulspatil1.blogsappbackend.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping({ApiRoutes.POSTS_V1, ApiRoutes.POSTS_LEGACY})
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //Public
    @GetMapping
    public ResponseEntity<Page<PostSummaryResponse>> getPublishedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPublishedPosts(page, size));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetailResponse> getPostBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    @GetMapping("/category/{categorySlug}")
    public ResponseEntity<Page<PostSummaryResponse>> getPostsByCategory(
            @PathVariable String categorySlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByCategory(categorySlug, page, size));
    }

    @GetMapping("/tag/{tagSlug}")
    public ResponseEntity<Page<PostSummaryResponse>> getByTag(
            @PathVariable String tagSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getPostsByTag(tagSlug, page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostSummaryResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.searchPosts(query, page, size));
    }

    //Admin
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PostSummaryResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDetailResponse> createPost(
            @Valid @RequestBody PostRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(request, principal.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(('ADMIN'))")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
