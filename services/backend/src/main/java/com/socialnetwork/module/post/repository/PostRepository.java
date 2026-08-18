package com.socialnetwork.module.post.repository;

import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.enums.PostStatus;
import com.socialnetwork.module.post.entity.enums.PostVisibility;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
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

    Page<Post> findByStatusAndVisibilityOrderByCreatedAtDesc(
            PostStatus status,
            PostVisibility visibility,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p
        FROM Post p
        WHERE p.id = :postId
          AND p.status = :status
    """)
    Optional<Post> findByIdAndStatusForUpdate(
            @Param("postId") UUID postId,
            @Param("status") PostStatus status
    );
}