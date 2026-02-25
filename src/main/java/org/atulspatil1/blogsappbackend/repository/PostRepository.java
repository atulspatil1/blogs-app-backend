package org.atulspatil1.blogsappbackend.repository;

import org.atulspatil1.blogsappbackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository <Post, Long> {

    Optional<Post> findBySlug(String slug);

    Page<Post> findByStatus(Post.Status status, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.categories c WHERE c.slug = :slug AND p.status = 'PUBLISHED'")
    Page<Post> findPublishedByCategorySlug(@Param("slug") String slug, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.slug = :slug AND p.status = 'PUBLISHED'")
    Page<Post> findPublishedByTagSlug(@Param("slug") String slug, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED' AND " +
        "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')))"
    )
    Page<Post> searchPublished(@Param("query") String query, Pageable pageable);
}
