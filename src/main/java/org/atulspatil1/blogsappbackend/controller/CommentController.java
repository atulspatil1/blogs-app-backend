package org.atulspatil1.blogsappbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.dto.CommentResponse;
import org.atulspatil1.blogsappbackend.dto.request.CommentRequest;
import org.atulspatil1.blogsappbackend.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //Public
    @PostMapping
    public ResponseEntity<CommentResponse> submit(@Valid @RequestBody CommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.submitComment(request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getApproved(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getApprovedComments(postId));
    }

    //Admin
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponse>> getPending() {
        return ResponseEntity.ok(commentService.getPendingComments());
    }

    @PutMapping("/admin/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.approveComment(id));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
