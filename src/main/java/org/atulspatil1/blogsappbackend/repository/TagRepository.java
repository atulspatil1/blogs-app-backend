package org.atulspatil1.blogsappbackend.repository;


import org.atulspatil1.blogsappbackend.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);
    Set<Tag> findBySlugIn(Set<String> slugs);
}
