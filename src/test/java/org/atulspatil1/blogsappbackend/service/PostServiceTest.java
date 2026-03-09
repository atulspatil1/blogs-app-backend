package org.atulspatil1.blogsappbackend.service;

import org.atulspatil1.blogsappbackend.dto.PostDetailResponse;
import org.atulspatil1.blogsappbackend.dto.PostSummaryResponse;
import org.atulspatil1.blogsappbackend.dto.request.PostRequest;
import org.atulspatil1.blogsappbackend.exception.ResourceNotFoundException;
import org.atulspatil1.blogsappbackend.mapper.PostMapper;
import org.atulspatil1.blogsappbackend.model.*;
import org.atulspatil1.blogsappbackend.repository.CategoryRepository;
import org.atulspatil1.blogsappbackend.repository.PostRepository;
import org.atulspatil1.blogsappbackend.repository.TagRepository;
import org.atulspatil1.blogsappbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private TagRepository tagRepository;
    @Mock private UserRepository userRepository;
    @Mock private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User testAuthor;
    private Post testPost;
    private PostSummaryResponse testSummary;
    private PostDetailResponse testDetail;

    @BeforeEach
    void setUp() {
        testAuthor = User.builder()
                .id(1L).username("atul").email("atul@test.com")
                .password("encoded").role(User.Role.ADMIN)
                .build();

        testPost = Post.builder()
                .id(1L).title("Test Post").slug("test-post")
                .summary("Summary").content("Content")
                .status(Post.Status.PUBLISHED).author(testAuthor)
                .categories(new HashSet<>()).tags(new HashSet<>())
                .publishedAt(LocalDateTime.now())
                .build();

        testSummary = PostSummaryResponse.builder()
                .id(1L).title("Test Post").slug("test-post")
                .authorUsername("atul").status("PUBLISHED")
                .categories(Set.of()).tags(Set.of())
                .build();

        testDetail = PostDetailResponse.builder()
                .id(1L).title("Test Post").slug("test-post")
                .content("Content").authorUsername("atul").status("PUBLISHED")
                .categories(Set.of()).tags(Set.of()).comments(List.of())
                .build();
    }

    // ---- Read operations ----

    @Nested
    @DisplayName("getPublishedPosts")
    class GetPublishedPosts {
        @Test
        @DisplayName("returns page of summaries")
        void returnsPageOfSummaries() {
            Page<Post> page = new PageImpl<>(List.of(testPost));
            when(postRepository.findByStatusWithRelations(eq(Post.Status.PUBLISHED), any(Pageable.class)))
                    .thenReturn(page);
            when(postMapper.toSummary(testPost)).thenReturn(testSummary);

            Page<PostSummaryResponse> result = postService.getPublishedPosts(0, 10);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Post");
            verify(postRepository).findByStatusWithRelations(eq(Post.Status.PUBLISHED), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("getPostBySlug")
    class GetPostBySlug {
        @Test
        @DisplayName("found — returns detail response")
        void found_returnsDetail() {
            when(postRepository.findBySlugWithRelations("test-post")).thenReturn(Optional.of(testPost));
            when(postMapper.toDetail(testPost)).thenReturn(testDetail);

            PostDetailResponse result = postService.getPostBySlug("test-post");

            assertThat(result.getSlug()).isEqualTo("test-post");
            verify(postRepository).findBySlugWithRelations("test-post");
        }

        @Test
        @DisplayName("not found — throws ResourceNotFoundException")
        void notFound_throws() {
            when(postRepository.findBySlugWithRelations("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.getPostBySlug("unknown"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("unknown");
        }
    }

    @Nested
    @DisplayName("getPostsByCategory")
    class GetPostsByCategory {
        @Test
        @DisplayName("returns filtered page")
        void returnsFilteredPage() {
            Page<Post> page = new PageImpl<>(List.of(testPost));
            when(postRepository.findPublishedByCategorySlug(eq("java"), any(Pageable.class))).thenReturn(page);
            when(postMapper.toSummary(testPost)).thenReturn(testSummary);

            Page<PostSummaryResponse> result = postService.getPostsByCategory("java", 0, 10);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getPostsByTag")
    class GetPostsByTag {
        @Test
        @DisplayName("returns filtered page")
        void returnsFilteredPage() {
            Page<Post> page = new PageImpl<>(List.of(testPost));
            when(postRepository.findPublishedByTagSlug(eq("jwt"), any(Pageable.class))).thenReturn(page);
            when(postMapper.toSummary(testPost)).thenReturn(testSummary);

            Page<PostSummaryResponse> result = postService.getPostsByTag("jwt", 0, 10);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("searchPosts")
    class SearchPosts {
        @Test
        @DisplayName("returns matching page")
        void returnsMatchingPage() {
            Page<Post> page = new PageImpl<>(List.of(testPost));
            when(postRepository.searchPublished(eq("test"), any(Pageable.class))).thenReturn(page);
            when(postMapper.toSummary(testPost)).thenReturn(testSummary);

            Page<PostSummaryResponse> result = postService.searchPosts("test", 0, 10);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getAllPosts (admin)")
    class GetAllPosts {
        @Test
        @DisplayName("returns all posts")
        void returnsAll() {
            Page<Post> page = new PageImpl<>(List.of(testPost));
            when(postRepository.findAllWithRelations(any(Pageable.class))).thenReturn(page);
            when(postMapper.toSummary(testPost)).thenReturn(testSummary);

            Page<PostSummaryResponse> result = postService.getAllPosts(0, 10);

            assertThat(result.getContent()).hasSize(1);
            verify(postRepository).findAllWithRelations(any(Pageable.class));
        }
    }

    // ---- Write operations ----

    @Nested
    @DisplayName("createPost")
    class CreatePost {
        private PostRequest request;

        @BeforeEach
        void setUp() {
            request = new PostRequest();
            request.setTitle("New Post");
            request.setContent("Some content");
            request.setStatus(Post.Status.PUBLISHED);
        }

        @Test
        @DisplayName("published — sets publishedAt")
        void published_setsPublishedAt() {
            when(userRepository.findByEmail("atul@test.com")).thenReturn(Optional.of(testAuthor));
            when(postRepository.existsBySlug(anyString())).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
                Post p = inv.getArgument(0);
                p.setId(2L);
                return p;
            });
            when(postMapper.toDetail(any(Post.class))).thenReturn(testDetail);

            postService.createPost(request, "atul@test.com");

            verify(postRepository).save(argThat(post ->
                    post.getPublishedAt() != null));
        }

        @Test
        @DisplayName("draft — no publishedAt")
        void draft_noPublishedAt() {
            request.setStatus(Post.Status.DRAFT);
            when(userRepository.findByEmail("atul@test.com")).thenReturn(Optional.of(testAuthor));
            when(postRepository.existsBySlug(anyString())).thenReturn(false);
            when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
            when(postMapper.toDetail(any(Post.class))).thenReturn(testDetail);

            postService.createPost(request, "atul@test.com");

            verify(postRepository).save(argThat(post ->
                    post.getPublishedAt() == null));
        }

        @Test
        @DisplayName("user not found — throws")
        void userNotFound_throws() {
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.createPost(request, "unknown@test.com"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updatePost")
    class UpdatePost {
        @Test
        @DisplayName("success — updates fields")
        void success() {
            PostRequest request = new PostRequest();
            request.setTitle("Updated");
            request.setContent("Updated content");
            request.setStatus(Post.Status.PUBLISHED);

            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
            when(postMapper.toDetail(any(Post.class))).thenReturn(testDetail);

            postService.updatePost(1L, request);

            verify(postRepository).save(argThat(post ->
                    post.getTitle().equals("Updated")));
        }

        @Test
        @DisplayName("not found — throws")
        void notFound_throws() {
            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.updatePost(99L, new PostRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deletePost")
    class DeletePost {
        @Test
        @DisplayName("success — deletes")
        void success() {
            when(postRepository.existsById(1L)).thenReturn(true);
            postService.deletePost(1L);
            verify(postRepository).deleteById(1L);
        }

        @Test
        @DisplayName("not found — throws")
        void notFound_throws() {
            when(postRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> postService.deletePost(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
