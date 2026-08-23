package com.socialnetwork.module.relationship.repository;

import com.socialnetwork.module.relationship.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    // ============================================================
    // CHECK / FIND RELATIONSHIP
    // ============================================================

    Optional<Follow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    void deleteByFollowerIdAndFollowingId(
            UUID followerId,
            UUID followingId
    );

    // ============================================================
    // FOLLOWING (Những người mà user đang follow)
    // ============================================================

    Page<Follow> findByFollowerId(UUID followerId, Pageable pageable);

    // ============================================================
    // FOLLOWERS (Những người đang follow user)
    // ============================================================

    Page<Follow> findByFollowingId(UUID followingId, Pageable pageable);

    // ============================================================
    // COUNT
    // ============================================================

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);
}