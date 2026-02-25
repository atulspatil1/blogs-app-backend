package org.atulspatil1.blogsappbackend.repository;

import org.atulspatil1.blogsappbackend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdAndApprovedTrue(Long postId);
    List<Comment> findByApprovedFalse();
}
