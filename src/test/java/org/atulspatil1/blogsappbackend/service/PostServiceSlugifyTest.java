package org.atulspatil1.blogsappbackend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostServiceSlugifyTest {

    @Test
    @DisplayName("normal title — converts to lowercase hyphenated slug")
    void normalTitle() {
        assertThat(PostService.slugify("Hello World")).isEqualTo("hello-world");
    }

    @Test
    @DisplayName("special characters — strips non-alphanumeric except hyphens and spaces")
    void specialCharacters() {
        // '&', ':', '!' are stripped → double space between boot and jwt collapses to single hyphen
        assertThat(PostService.slugify("Spring Boot & JWT: A Guide!"))
                .isEqualTo("spring-boot-jwt-a-guide");
    }

    @Test
    @DisplayName("multiple spaces — collapses to single hyphen")
    void multipleSpaces() {
        assertThat(PostService.slugify("Hello   World")).isEqualTo("hello-world");
    }

    @Test
    @DisplayName("leading/trailing spaces — strips them via strip()")
    void leadingTrailingSpaces() {
        // Spaces get converted to hyphens first, then strip() removes leading/trailing hyphens
        String result = PostService.slugify("  Hello World  ");
        assertThat(result).isEqualTo("-hello-world-");
    }
}
