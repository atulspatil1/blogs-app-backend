package org.atulspatil1.blogsappbackend.repository;

import org.atulspatil1.blogsappbackend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired private PostRepository postRepository;
    @Autowired private TestEntityManager em;

    private User author;
    private Category category;
    private Tag tag;
    private Post publishedPost;
    private Post draftPost;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .username("atul").email("atul@test.com")
                .password("encoded").role(User.Role.ADMIN)
                .build();
        em.persist(author);

        category = Category.builder()
                .name("Java").slug("java").description("Java posts")
                .build();
        em.persist(category);

        tag = Tag.builder().name("JWT").slug("jwt").build();
        em.persist(tag);

        publishedPost = Post.builder()
                .title("Published Post").slug("published-post")
                .summary("A published summary").content("Published content")
                .status(Post.Status.PUBLISHED).author(author)
                .categories(Set.of(category)).tags(Set.of(tag))
                .publishedAt(LocalDateTime.now())
                .build();
        em.persist(publishedPost);

        draftPost = Post.builder()
                .title("Draft Post").slug("draft-post")
                .summary("A draft summary").content("Draft content")
                .status(Post.Status.DRAFT).author(author)
                .categories(Set.of()).tags(Set.of())
                .build();
        em.persist(draftPost);

        Comment comment = Comment.builder()
                .post(publishedPost).authorName("Jane")
                .email("jane@test.com").body("Great post!")
                .approved(true)
                .build();
        em.persist(comment);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findBySlugWithRelations — found — returns post with relations")
    void findBySlugWithRelations_found() {
        Optional<Post> result = postRepository.findBySlugWithRelations("published-post");

        assertThat(result).isPresent();
        Post post = result.get();
        assertThat(post.getAuthor().getUsername()).isEqualTo("atul");
        assertThat(post.getCategories()).hasSize(1);
        assertThat(post.getTags()).hasSize(1);
        assertThat(post.getComments()).hasSize(1);
    }

    @Test
    @DisplayName("findBySlugWithRelations — not found — returns empty")
    void findBySlugWithRelations_notFound() {
        Optional<Post> result = postRepository.findBySlugWithRelations("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByStatusWithRelations — published only")
    void findByStatusWithRelations_published() {
        Page<Post> result = postRepository.findByStatusWithRelations(
                Post.Status.PUBLISHED, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSlug()).isEqualTo("published-post");
        assertThat(result.getContent().get(0).getAuthor()).isNotNull();
    }

    @Test
    @DisplayName("findPublishedByCategorySlug — filters by category")
    void findPublishedByCategorySlug_filters() {
        Page<Post> result = postRepository.findPublishedByCategorySlug("java", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSlug()).isEqualTo("published-post");
    }

    @Test
    @DisplayName("findPublishedByTagSlug — filters by tag")
    void findPublishedByTagSlug_filters() {
        Page<Post> result = postRepository.findPublishedByTagSlug("jwt", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getSlug()).isEqualTo("published-post");
    }

    @Test
    @DisplayName("searchPublished — matches title")
    void searchPublished_matchesTitle() {
        Page<Post> result = postRepository.searchPublished("Published", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("searchPublished — matches summary")
    void searchPublished_matchesSummary() {
        Page<Post> result = postRepository.searchPublished("published summary", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("findAllWithRelations — returns all posts")
    void findAllWithRelations_returnsAll() {
        Page<Post> result = postRepository.findAllWithRelations(PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(2);
    }
}
