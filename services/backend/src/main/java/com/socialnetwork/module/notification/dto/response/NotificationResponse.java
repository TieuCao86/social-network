package com.socialnetwork.module.notification.dto.response;

import com.socialnetwork.module.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class NotificationResponse {

    private UUID id;
    private UUID actorId;
    private NotificationType type;
    private UUID referenceId;
    private String content;
    private boolean read;
    private Instant createdAt;
}