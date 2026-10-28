package com.socialnetwork.module.notification.mapper;

import com.socialnetwork.module.notification.dto.response.NotificationResponse;
import com.socialnetwork.module.notification.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification);
}