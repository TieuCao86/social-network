package com.socialnetwork.module.relationship.repository;

import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    /**
     * Tìm friendship giữa 2 user, không quan tâm chiều.
     */
    @Query("""
            SELECT f
            FROM Friendship f
            WHERE
                (f.requesterId = :user1 AND f.addresseeId = :user2)
                OR
                (f.requesterId = :user2 AND f.addresseeId = :user1)
            """)
    Optional<Friendship> findBetween(
            @Param("user1") UUID user1,
            @Param("user2") UUID user2
    );

    /**
     * Lấy lời mời đang chờ mà user nhận được.
     */
    Page<Friendship> findByAddresseeIdAndStatus(
            UUID addresseeId,
            FriendshipStatus status,
            Pageable pageable
    );

    /**
     * Lấy lời mời mà user đã gửi.
     */
    Page<Friendship> findByRequesterIdAndStatus(
            UUID requesterId,
            FriendshipStatus status,
            Pageable pageable
    );

    /**
     * Lấy tất cả friendship của user theo status.
     *
     * Ví dụ:
     * ACCEPTED -> danh sách bạn bè
     * PENDING  -> tất cả request liên quan đến user
     */
    @Query("""
            SELECT f
            FROM Friendship f
            WHERE
                (f.requesterId = :userId OR f.addresseeId = :userId)
                AND f.status = :status
            """)
    Page<Friendship> findAllByUserIdAndStatus(
            @Param("userId") UUID userId,
            @Param("status") FriendshipStatus status,
            Pageable pageable
    );

    /**
     * Đếm số bạn bè đã chấp nhận.
     */
    @Query("""
            SELECT COUNT(f)
            FROM Friendship f
            WHERE
                (f.requesterId = :userId OR f.addresseeId = :userId)
                AND f.status = com.socialnetwork.module.relationship.entity.enums.FriendshipStatus.ACCEPTED
            """)
    long countFriends(@Param("userId") UUID userId);
}