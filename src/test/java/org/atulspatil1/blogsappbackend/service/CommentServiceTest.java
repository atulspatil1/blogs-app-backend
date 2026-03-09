package org.atulspatil1.blogsappbackend.service;

import org.atulspatil1.blogsappbackend.dto.CommentResponse;
import org.atulspatil1.blogsappbackend.dto.request.CommentRequest;
import org.atulspatil1.blogsappbackend.exception.ResourceNotFoundException;
import org.atulspatil1.blogsappbackend.mapper.CommentMapper;
import org.atulspatil1.blogsappbackend.model.Comment;
import org.atulspatil1.blogsappbackend.model.Post;
import org.atulspatil1.blogsappbackend.repository.CommentRepository;
import org.atulspatil1.blogsappbackend.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Post testPost;
    private Comment testComment;
    private CommentResponse testResponse;

    @BeforeEach
    void setUp() {
        testPost = Post.builder().id(1L).title("Test").slug("test").content("c")
                .status(Post.Status.PUBLISHED).build();

        testComment = Comment.builder()
                .id(1L).post(testPost).authorName("Jane")
                .email("jane@test.com").body("Great post!")
                .approved(false).createdAt(LocalDateTime.now())
                .build();

        testResponse = CommentResponse.builder()
                .id(1L).authorName("Jane").body("Great post!")
                .approved(false).createdAt(testComment.getCreatedAt())
                .build();
    }

    @Nested
    @DisplayName("submitComment")
    class SubmitComment {
        @Test
        @DisplayName("success — creates unapproved comment")
        void success() {
            CommentRequest request = new CommentRequest();
            request.setPostId(1L);
            request.setAuthorName("Jane");
            request.setEmail("jane@test.com");
            request.setBody("Great post!");

            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
            when(commentMapper.toResponse(testComment)).thenReturn(testResponse);

            CommentResponse result = commentService.submitComment(request);

            assertThat(result.getAuthorName()).isEqualTo("Jane");
            assertThat(result.getApproved()).isFalse();
            verify(commentRepository).save(argThat(c -> !c.getApproved()));
        }

        @Test
        @DisplayName("post not found — throws")
        void postNotFound_throws() {
            CommentRequest request = new CommentRequest();
            request.setPostId(99L);

            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.submitComment(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getApprovedComments")
    class GetApprovedComments {
        @Test
        @DisplayName("returns list of approved comments")
        void returnsList() {
            List<Comment> comments = List.of(testComment);
            List<CommentResponse> responses = List.of(testResponse);
            when(commentRepository.findByPostIdAndApprovedTrue(1L)).thenReturn(comments);
            when(commentMapper.toResponseList(comments)).thenReturn(responses);

            List<CommentResponse> result = commentService.getApprovedComments(1L);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getPendingComments")
    class GetPendingComments {
        @Test
        @DisplayName("returns list of unapproved comments")
        void returnsList() {
            List<Comment> comments = List.of(testComment);
            List<CommentResponse> responses = List.of(testResponse);
            when(commentRepository.findByApprovedFalse()).thenReturn(comments);
            when(commentMapper.toResponseList(comments)).thenReturn(responses);

            List<CommentResponse> result = commentService.getPendingComments();

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("approveComment")
    class ApproveComment {
        @Test
        @DisplayName("success — sets approved to true")
        void success() {
            CommentResponse approvedResponse = CommentResponse.builder()
                    .id(1L).authorName("Jane").body("Great post!").approved(true).build();

            when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
            when(commentMapper.toResponse(any(Comment.class))).thenReturn(approvedResponse);

            CommentResponse result = commentService.approveComment(1L);

            assertThat(result.getApproved()).isTrue();
            verify(commentRepository).save(argThat(Comment::getApproved));
        }

        @Test
        @DisplayName("not found — throws")
        void notFound_throws() {
            when(commentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.approveComment(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteComment")
    class DeleteComment {
        @Test
        @DisplayName("success — deletes")
        void success() {
            when(commentRepository.existsById(1L)).thenReturn(true);
            commentService.deleteComment(1L);
            verify(commentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("not found — throws")
        void notFound_throws() {
            when(commentRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> commentService.deleteComment(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
