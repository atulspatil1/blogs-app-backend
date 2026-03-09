package org.atulspatil1.blogsappbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.blogsappbackend.dto.PostDetailResponse;
import org.atulspatil1.blogsappbackend.dto.PostSummaryResponse;
import org.atulspatil1.blogsappbackend.dto.request.PostRequest;
import org.atulspatil1.blogsappbackend.exception.ResourceNotFoundException;
import org.atulspatil1.blogsappbackend.model.Post;
import org.atulspatil1.blogsappbackend.security.JwtAuthFilter;
import org.atulspatil1.blogsappbackend.security.JwtUtil;
import org.atulspatil1.blogsappbackend.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PostService postService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthFilter jwtAuthFilter;

    private final PostSummaryResponse summary = PostSummaryResponse.builder()
            .id(1L).title("Test").slug("test").authorUsername("atul")
            .status("PUBLISHED").categories(Set.of()).tags(Set.of())
            .build();

    private final PostDetailResponse detail = PostDetailResponse.builder()
            .id(1L).title("Test").slug("test").content("Content")
            .authorUsername("atul").status("PUBLISHED")
            .categories(Set.of()).tags(Set.of()).comments(List.of())
            .build();

    // ---- Public endpoints ----

    @Nested
    @DisplayName("GET /api/v1/posts")
    class GetPublishedPosts {
        @Test
        @DisplayName("returns 200 with page of summaries")
        void returns200() throws Exception {
            Page<PostSummaryResponse> page = new PageImpl<>(List.of(summary));
            when(postService.getPublishedPosts(0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/posts").param("page", "0").param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content[0].title").value("Test"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/posts/{slug}")
    class GetPostBySlug {
        @Test
        @DisplayName("found — returns 200")
        void returns200() throws Exception {
            when(postService.getPostBySlug("test")).thenReturn(detail);

            mockMvc.perform(get("/api/v1/posts/test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.slug").value("test"));
        }

        @Test
        @DisplayName("not found — returns 404")
        void returns404() throws Exception {
            when(postService.getPostBySlug("unknown"))
                    .thenThrow(new ResourceNotFoundException("Post not found"));

            mockMvc.perform(get("/api/v1/posts/unknown"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/posts/category/{slug}")
    class GetByCategory {
        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            Page<PostSummaryResponse> page = new PageImpl<>(List.of(summary));
            when(postService.getPostsByCategory(eq("java"), eq(0), eq(10))).thenReturn(page);

            mockMvc.perform(get("/api/v1/posts/category/java"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/posts/tag/{slug}")
    class GetByTag {
        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            Page<PostSummaryResponse> page = new PageImpl<>(List.of(summary));
            when(postService.getPostsByTag(eq("jwt"), eq(0), eq(10))).thenReturn(page);

            mockMvc.perform(get("/api/v1/posts/tag/jwt"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/posts/search")
    class SearchPosts {
        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            Page<PostSummaryResponse> page = new PageImpl<>(List.of(summary));
            when(postService.searchPosts(eq("spring"), eq(0), eq(10))).thenReturn(page);

            mockMvc.perform(get("/api/v1/posts/search").param("query", "spring"))
                    .andExpect(status().isOk());
        }
    }

    // ---- Admin endpoints ----

    @Nested
    @DisplayName("GET /api/v1/posts/admin/all")
    class GetAllPosts {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("as ADMIN — returns 200")
        void asAdmin_returns200() throws Exception {
            Page<PostSummaryResponse> page = new PageImpl<>(List.of(summary));
            when(postService.getAllPosts(0, 10)).thenReturn(page);

            mockMvc.perform(get("/api/v1/posts/admin/all"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/posts")
    class CreatePost {
        @Test
        @WithMockUser(username = "atul@test.com", roles = "ADMIN")
        @DisplayName("as ADMIN with valid body — returns 201")
        void asAdmin_returns201() throws Exception {
            PostRequest request = new PostRequest();
            request.setTitle("New Post");
            request.setContent("Content here");
            request.setStatus(Post.Status.PUBLISHED);

            when(postService.createPost(any(PostRequest.class), anyString())).thenReturn(detail);

            mockMvc.perform(post("/api/v1/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .principal(() -> "atul@test.com"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.title").value("Test"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("invalid body — returns 400")
        void invalidBody_returns400() throws Exception {
            PostRequest request = new PostRequest(); // missing required fields

            mockMvc.perform(post("/api/v1/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/posts/{id}")
    class UpdatePost {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("as ADMIN — returns 200")
        void asAdmin_returns200() throws Exception {
            PostRequest request = new PostRequest();
            request.setTitle("Updated");
            request.setContent("Updated content");
            request.setStatus(Post.Status.PUBLISHED);

            when(postService.updatePost(eq(1L), any(PostRequest.class))).thenReturn(detail);

            mockMvc.perform(put("/api/v1/posts/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/posts/{id}")
    class DeletePost {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("as ADMIN — returns 204")
        void asAdmin_returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/posts/1"))
                    .andExpect(status().isNoContent());
        }
    }
}
