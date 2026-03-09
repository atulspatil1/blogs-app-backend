package org.atulspatil1.blogsappbackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "7a3f8c2e1b9d4f6a0e5c8b3d7f2a9e4c1b8d5f3a7e2c9b6d4f1a8e5c2b9d7f4a");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 86400000L);
    }

    private UserDetails createUser(String email) {
        return new User(email, "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("generateToken — returns non-empty string")
    void generateToken_returnsNonEmpty() {
        String token = jwtUtil.generateToken(createUser("atul@test.com"));
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractEmail — returns correct subject")
    void extractEmail_returnsCorrectSubject() {
        UserDetails user = createUser("atul@test.com");
        String token = jwtUtil.generateToken(user);

        String email = jwtUtil.extractEmail(token);

        assertThat(email).isEqualTo("atul@test.com");
    }

    @Test
    @DisplayName("isTokenValid — valid token + matching user — returns true")
    void isTokenValid_validToken_returnsTrue() {
        UserDetails user = createUser("atul@test.com");
        String token = jwtUtil.generateToken(user);

        Boolean valid = jwtUtil.isTokenValid(token, user);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("isTokenValid — valid token + wrong user — returns false")
    void isTokenValid_wrongUser_returnsFalse() {
        UserDetails user = createUser("atul@test.com");
        UserDetails otherUser = createUser("other@test.com");
        String token = jwtUtil.generateToken(user);

        Boolean valid = jwtUtil.isTokenValid(token, otherUser);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("expired token — throws ExpiredJwtException")
    void expiredToken_throwsException() {
        // Set expiration to 0ms so token is immediately expired
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 0L);

        UserDetails user = createUser("atul@test.com");
        String token = jwtUtil.generateToken(user);

        // Small delay to ensure expiration
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertThatThrownBy(() -> jwtUtil.isTokenValid(token, user))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
