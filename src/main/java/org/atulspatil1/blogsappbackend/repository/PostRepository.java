package org.atulspatil1.blogsappbackend.repository;

import org.atulspatil1.blogsappbackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository <Post, Long> {

    boolean existsBySlug(String slug);

    Optional<Post> findBySlug(String slug);

    Page<Post> findByStatus(Post.Status status, Pageable pageable);

    // -- Fetch-join variants (avoid N+1) --

    // Detail view: fetch all relationships including comments
    @Query("SELECT p FROM Post p " +
           "LEFT JOIN FETCH p.author " +
           "LEFT JOIN FETCH p.categories " +
           "LEFT JOIN FETCH p.tags " +
           "LEFT JOIN FETCH p.comments " +
           "WHERE p.slug = :slug")
    Optional<Post> findBySlugWithRelations(@Param("slug") String slug);

    // Summary lists: fetch author, categories, tags (no comments)
    @Query(value = "SELECT DISTINCT p FROM Post p " +
                   "LEFT JOIN FETCH p.author " +
                   "LEFT JOIN FETCH p.categories " +
                   "LEFT JOIN FETCH p.tags " +
                   "WHERE p.status = :status",
           countQuery = "SELECT COUNT(p) FROM Post p WHERE p.status = :status")
    Page<Post> findByStatusWithRelations(@Param("status") Post.Status status, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
                   "LEFT JOIN FETCH p.author " +
                   "LEFT JOIN FETCH p.categories " +
                   "LEFT JOIN FETCH p.tags " +
                   "JOIN p.categories c WHERE c.slug = :slug AND p.status = 'PUBLISHED'",
           countQuery = "SELECT COUNT(p) FROM Post p JOIN p.categories c WHERE c.slug = :slug AND p.status = 'PUBLISHED'")
    Page<Post> findPublishedByCategorySlug(@Param("slug") String slug, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
                   "LEFT JOIN FETCH p.author " +
                   "LEFT JOIN FETCH p.categories " +
                   "LEFT JOIN FETCH p.tags " +
                   "JOIN p.tags t WHERE t.slug = :slug AND p.status = 'PUBLISHED'",
           countQuery = "SELECT COUNT(p) FROM Post p JOIN p.tags t WHERE t.slug = :slug AND p.status = 'PUBLISHED'")
    Page<Post> findPublishedByTagSlug(@Param("slug") String slug, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p " +
                   "LEFT JOIN FETCH p.author " +
                   "LEFT JOIN FETCH p.categories " +
                   "LEFT JOIN FETCH p.tags " +
                   "WHERE p.status = 'PUBLISHED' AND " +
                   "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')))",
           countQuery = "SELECT COUNT(p) FROM Post p WHERE p.status = 'PUBLISHED' AND " +
                   "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.summary) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Post> searchPublished(@Param("query") String query, Pageable pageable);

    // Admin: all posts with relations
    @Query(value = "SELECT DISTINCT p FROM Post p " +
                   "LEFT JOIN FETCH p.author " +
                   "LEFT JOIN FETCH p.categories " +
                   "LEFT JOIN FETCH p.tags",
           countQuery = "SELECT COUNT(p) FROM Post p")
    Page<Post> findAllWithRelations(Pageable pageable);
}
