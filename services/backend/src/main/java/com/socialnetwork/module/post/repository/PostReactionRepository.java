package com.socialnetwork.module.post.repository;

import com.socialnetwork.module.post.entity.PostReaction;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostReactionRepository
        extends JpaRepository<PostReaction, UUID> {

    Optional<PostReaction> findByPostIdAndUserId(
            UUID postId,
            UUID userId
    );


    /*
     * ============================================================
     * REACTION SUMMARY - SINGLE POST
     * ============================================================
     */

    interface ReactionCountProjection {

        ReactionType getType();

        Long getCount();
    }

    @Query("""
        SELECT
            r.type AS type,
            COUNT(r) AS count
        FROM PostReaction r
        WHERE r.postId = :postId
        GROUP BY r.type
        """)
    List<ReactionCountProjection> countReactionsByPostIdGroupedByType(
            @Param("postId") UUID postId
    );


    /*
     * ============================================================
     * REACTION SUMMARY - MULTIPLE POSTS
     * ============================================================
     */

    interface ReactionCountByPostProjection {

        UUID getPostId();

        ReactionType getType();

        Long getCount();
    }

    @Query("""
        SELECT
            r.postId AS postId,
            r.type AS type,
            COUNT(r) AS count
        FROM PostReaction r
        WHERE r.postId IN :postIds
        GROUP BY r.postId, r.type
        """)
    List<ReactionCountByPostProjection> countReactionsByPostIds(
            @Param("postIds") List<UUID> postIds
    );


    /*
     * ============================================================
     * CURRENT USER REACTIONS - MULTIPLE POSTS
     * ============================================================
     */

    @Query("""
        SELECT r
        FROM PostReaction r
        WHERE r.postId IN :postIds
          AND r.userId = :userId
        """)
    List<PostReaction> findByPostIdInAndUserId(
            @Param("postIds") List<UUID> postIds,
            @Param("userId") UUID userId
    );


    /*
     * ============================================================
     * PAGINATION
     * ============================================================
     */

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