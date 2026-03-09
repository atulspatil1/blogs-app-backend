package org.atulspatil1.blogsappbackend.service;

import org.atulspatil1.blogsappbackend.dto.AuthResponse;
import org.atulspatil1.blogsappbackend.dto.request.LoginRequest;
import org.atulspatil1.blogsappbackend.model.User;
import org.atulspatil1.blogsappbackend.repository.UserRepository;
import org.atulspatil1.blogsappbackend.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private UserRepository userRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("login — success — returns token and user info")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("atul@test.com");
        request.setPassword("password");

        User user = User.builder()
                .id(1L).username("atul").email("atul@test.com")
                .password("encoded").role(User.Role.ADMIN).build();

        // UserDetailsService returns a Spring Security User
        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "atul@test.com", "encoded",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(userDetailsService.loadUserByUsername("atul@test.com")).thenReturn(springUser);
        when(jwtUtil.generateToken(springUser)).thenReturn("jwt-token-123");
        when(userRepository.findByEmail("atul@test.com")).thenReturn(Optional.of(user));

        AuthResponse result = authService.login(request);

        assertThat(result.getToken()).isEqualTo("jwt-token-123");
        assertThat(result.getEmail()).isEqualTo("atul@test.com");
        assertThat(result.getUsername()).isEqualTo("atul");
        assertThat(result.getRole()).isEqualTo("ADMIN");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login — bad credentials — throws BadCredentialsException")
    void login_badCredentials_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail("atul@test.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
