package com.socialnetwork.module.comment.repository;

import com.socialnetwork.module.comment.entity.Comment;
import com.socialnetwork.module.comment.entity.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository
        extends JpaRepository<Comment, UUID> {

    Page<Comment> findByPostIdAndParentIdIsNullAndStatus(
            UUID postId,
            CommentStatus status,
            Pageable pageable
    );

    Page<Comment> findByParentIdAndStatus(
            UUID parentId,
            CommentStatus status,
            Pageable pageable
    );

    Page<Comment> findByUserIdAndStatus(
            UUID userId,
            CommentStatus status,
            Pageable pageable
    );

    // Tổng tất cả comment + reply của post
    long countByPostIdAndStatus(
            UUID postId,
            CommentStatus status
    );

    // Tổng reply trực tiếp của comment
    long countByParentIdAndStatus(
            UUID parentId,
            CommentStatus status
    );
}