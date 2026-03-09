package org.atulspatil1.blogsappbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atulspatil1.blogsappbackend.dto.AuthResponse;
import org.atulspatil1.blogsappbackend.dto.request.LoginRequest;
import org.atulspatil1.blogsappbackend.security.JwtAuthFilter;
import org.atulspatil1.blogsappbackend.security.JwtUtil;
import org.atulspatil1.blogsappbackend.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthService authService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthFilter jwtAuthFilter;

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {
        @Test
        @DisplayName("valid credentials — returns 200 with token")
        void validCredentials_returns200() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("atul@test.com");
            request.setPassword("password");

            AuthResponse response = AuthResponse.builder()
                    .token("jwt-token").email("atul@test.com")
                    .username("atul").role("ADMIN").build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.token").value("jwt-token"))
                    .andExpect(jsonPath("$.data.email").value("atul@test.com"));
        }

        @Test
        @DisplayName("bad credentials — returns 401")
        void badCredentials_returns401() throws Exception {
            LoginRequest request = new LoginRequest();
            request.setEmail("atul@test.com");
            request.setPassword("wrong");

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("missing fields — returns 400")
        void missingFields_returns400() throws Exception {
            LoginRequest request = new LoginRequest(); // blank email + password

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
