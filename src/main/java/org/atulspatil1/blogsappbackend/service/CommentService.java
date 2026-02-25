package org.atulspatil1.blogsappbackend.service;

import lombok.RequiredArgsConstructor;
import org.atulspatil1.blogsappbackend.dto.CommentResponse;
import org.atulspatil1.blogsappbackend.dto.request.CommentRequest;
import org.atulspatil1.blogsappbackend.exception.ResourceNotFoundException;
import org.atulspatil1.blogsappbackend.model.Comment;
import org.atulspatil1.blogsappbackend.model.Post;
import org.atulspatil1.blogsappbackend.repository.CommentRepository;
import org.atulspatil1.blogsappbackend.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponse submitComment(CommentRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + request.getPostId()));

        Comment comment = Comment.builder()
                .post(post)
                .authorName(request.getAuthorName())
                .email(request.getEmail())
                .body(request.getBody())
                .approved(false)
                .build();

        return toResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getApprovedComments(Long postId) {
        return commentRepository.findByPostIdAndApprovedTrue(postId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    //Admin
    public List<CommentResponse> getPendingComments() {
        return commentRepository.findByApprovedFalse()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CommentResponse approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found:" + id));
        comment.setApproved(true);
        return toResponse(commentRepository.save(comment));
    }

    public void deleteComment(Long id) {
        if(!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found:" + id);
        }
        commentRepository.deleteById(id);
    }

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .authorName(comment.getAuthorName())
                .body(comment.getBody())
                .approved(comment.getApproved())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
