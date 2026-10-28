package com.socialnetwork.module.notification.repository;

import com.socialnetwork.module.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(
            UUID recipientId,
            Pageable pageable
    );

    long countByRecipientIdAndReadFalse(UUID recipientId);

    boolean existsByIdAndRecipientId(UUID id, UUID recipientId);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.read = true
        WHERE n.recipientId = :recipientId
          AND n.read = false
    """)
    int markAllAsRead(@Param("recipientId") UUID recipientId);
}