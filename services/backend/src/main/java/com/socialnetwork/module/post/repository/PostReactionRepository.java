package com.socialnetwork.module.post.repository;

import com.socialnetwork.module.post.entity.PostReaction;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, UUID> {

    Optional<PostReaction> findByPostIdAndUserId(
            UUID postId,
            UUID userId
    );

    interface ReactionCountProjection {
        ReactionType getType();
        Long getCount();
    }

    @Query("""
        SELECT r.type AS type, COUNT(r) AS count
        FROM PostReaction r
        WHERE r.postId = :postId
        GROUP BY r.type
    """)
    List<ReactionCountProjection> countReactionsByPostIdGroupedByType(
            @Param("postId") UUID postId
    );

    Page<PostReaction> findByPostIdOrderByCreatedAtDesc(
            UUID postId,
            Pageable pageable
    );

    Page<PostReaction> findByPostIdAndTypeOrderByCreatedAtDesc(
            UUID postId,
            ReactionType type,
            Pageable pageable
    );
}