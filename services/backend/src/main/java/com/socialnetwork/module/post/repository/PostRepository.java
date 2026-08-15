package com.socialnetwork.module.post.repository;

import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    boolean existsByIdAndStatus(
            UUID id,
            PostStatus status
    );

    Page<Post> findByAuthorIdAndStatusOrderByCreatedAtDesc(
            UUID authorId,
            PostStatus status,
            Pageable pageable
    );

    Page<Post> findByStatusOrderByCreatedAtDesc(
            PostStatus status,
            Pageable pageable
    );
}