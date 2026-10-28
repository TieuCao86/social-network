package com.socialnetwork.module.notification.service;

import com.socialnetwork.module.notification.dto.response.NotificationResponse;
import com.socialnetwork.module.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    NotificationResponse createNotification(
            UUID recipientId,
            UUID actorId,
            NotificationType type,
            UUID referenceId,
            String content
    );

    Page<NotificationResponse> getNotifications(
            UUID userId,
            Pageable pageable
    );

    long getUnreadCount(UUID userId);

    void markAsRead(UUID userId, UUID notificationId);

    int markAllAsRead(UUID userId);
}