package com.socialnetwork.module.post.repository;

import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

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