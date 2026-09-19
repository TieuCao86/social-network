package com.socialnetwork.module.user.repository;

import com.socialnetwork.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailOrPhone(String email, String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailOrPhone(String email, String phone);

    boolean existsByUsernameOrEmailOrPhone(
            String username,
            String email,
            String phone
    );

    @Query("""
        SELECT u
        FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
          AND u.id <> :currentUserId
          AND NOT EXISTS (
              SELECT f.id
              FROM Friendship f
              WHERE
                  (
                      (f.requesterId = :currentUserId AND f.addresseeId = u.id)
                      OR
                      (f.requesterId = u.id AND f.addresseeId = :currentUserId)
                  )
                  AND f.status = com.socialnetwork.module.relationship.entity.enums.FriendshipStatus.BLOCKED
          )
        """)
    Page<User> searchUsers(
            @Param("currentUserId") UUID currentUserId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}