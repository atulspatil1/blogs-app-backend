package org.atulspatil1.blogsappbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.blogsappbackend.dto.CommentResponse;
import org.atulspatil1.blogsappbackend.dto.request.CommentRequest;
import org.atulspatil1.blogsappbackend.security.JwtAuthFilter;
import org.atulspatil1.blogsappbackend.security.JwtUtil;
import org.atulspatil1.blogsappbackend.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CommentService commentService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthFilter jwtAuthFilter;

    private final CommentResponse testResponse = CommentResponse.builder()
            .id(1L).authorName("Jane").body("Great post!")
            .approved(false).createdAt(LocalDateTime.now())
            .build();

    // ---- Public endpoints ----

    @Nested
    @DisplayName("POST /api/v1/comments")
    class SubmitComment {
        @Test
        @DisplayName("valid body — returns 201")
        void validBody_returns201() throws Exception {
            CommentRequest request = new CommentRequest();
            request.setPostId(1L);
            request.setAuthorName("Jane");
            request.setEmail("jane@test.com");
            request.setBody("Great post!");

            when(commentService.submitComment(any(CommentRequest.class))).thenReturn(testResponse);

            mockMvc.perform(post("/api/v1/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.authorName").value("Jane"));
        }

        @Test
        @DisplayName("invalid body — returns 400")
        void invalidBody_returns400() throws Exception {
            CommentRequest request = new CommentRequest(); // missing fields

            mockMvc.perform(post("/api/v1/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/comments/{postId}")
    class GetApprovedComments {
        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            when(commentService.getApprovedComments(1L)).thenReturn(List.of(testResponse));

            mockMvc.perform(get("/api/v1/comments/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].authorName").value("Jane"));
        }
    }

    // ---- Admin endpoints ----

    @Nested
    @DisplayName("GET /api/v1/comments/admin/pending")
    class GetPending {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("as ADMIN — returns 200")
        void asAdmin_returns200() throws Exception {
            when(commentService.getPendingComments()).thenReturn(List.of(testResponse));

            mockMvc.perform(get("/api/v1/comments/admin/pending"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/comments/admin/{id}/approve")
    class ApproveComment {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("as ADMIN — returns 200")
        void asAdmin_returns200() throws Exception {
            CommentResponse approved = CommentResponse.builder()
                    .id(1L).authorName("Jane").body("Great!").approved(true).build();
            when(commentService.approveComment(1L)).thenReturn(approved);

            mockMvc.perform(put("/api/v1/comments/admin/1/approve"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.approved").value(true));
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/comments/admin/{id}")
    class DeleteComment {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("as ADMIN — returns 204")
        void asAdmin_returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/comments/admin/1"))
                    .andExpect(status().isNoContent());
        }
    }
}
