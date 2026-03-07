package org.atulspatil1.blogsappbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atulspatil1.blogsappbackend.dto.CommentResponse;
import org.atulspatil1.blogsappbackend.dto.request.CommentRequest;
import org.atulspatil1.blogsappbackend.exception.ResourceNotFoundException;
import org.atulspatil1.blogsappbackend.mapper.CommentMapper;
import org.atulspatil1.blogsappbackend.model.Comment;
import org.atulspatil1.blogsappbackend.model.Post;
import org.atulspatil1.blogsappbackend.repository.CommentRepository;
import org.atulspatil1.blogsappbackend.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

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

        Comment savedComment = commentRepository.save(comment);
        log.info("event=comment.submitted commentId={} postId={} approved={}",
                savedComment.getId(), request.getPostId(), savedComment.getApproved());
        return commentMapper.toResponse(savedComment);
    }

    public List<CommentResponse> getApprovedComments(Long postId) {
        return commentMapper.toResponseList(commentRepository.findByPostIdAndApprovedTrue(postId));
    }

    //Admin
    public List<CommentResponse> getPendingComments() {
        return commentMapper.toResponseList(commentRepository.findByApprovedFalse());
    }

    public CommentResponse approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found:" + id));
        comment.setApproved(true);
        Comment approvedComment = commentRepository.save(comment);
        log.info("event=comment.approved commentId={}", id);
        return commentMapper.toResponse(approvedComment);
    }

    public void deleteComment(Long id) {
        if(!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comment not found:" + id);
        }
        commentRepository.deleteById(id);
        log.info("event=comment.deleted commentId={}", id);
    }
}
