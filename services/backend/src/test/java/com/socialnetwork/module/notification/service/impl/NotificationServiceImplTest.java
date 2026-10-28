
package com.socialnetwork.module.notification.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.notification.dto.response.NotificationResponse;
import com.socialnetwork.module.notification.entity.Notification;
import com.socialnetwork.module.notification.entity.NotificationType;
import com.socialnetwork.module.notification.mapper.NotificationMapper;
import com.socialnetwork.module.notification.repository.NotificationRepository;
import com.socialnetwork.module.notification.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID recipientId;
    private UUID actorId;
    private UUID notificationId;
    private UUID referenceId;

    private Notification mockNotification;
    private NotificationResponse mockResponse;

    @BeforeEach
    void setUp() {
        recipientId = UUID.randomUUID();
        actorId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        referenceId = UUID.randomUUID();

        mockNotification = Notification.builder()
                .id(notificationId)
                .recipientId(recipientId)
                .actorId(actorId)
                .type(NotificationType.FRIEND_POST)
                .referenceId(referenceId)
                .content("Bạn bè vừa đăng bài viết")
                .read(false)
                .build();

        mockResponse = NotificationResponse.builder()
                .id(notificationId)
                .actorId(actorId)
                .type(NotificationType.FRIEND_POST)
                .referenceId(referenceId)
                .content("Bạn bè vừa đăng bài viết")
                .read(false)
                .build();
    }

    // =======================================================
    // CREATE NOTIFICATION
    // =======================================================

    @Nested
    @DisplayName("Tests cho createNotification()")
    class CreateNotificationTests {

        @Test
        @DisplayName("createNotification - Thành công")
        void createNotification_Success() {

            when(notificationRepository.save(any(Notification.class)))
                    .thenReturn(mockNotification);

            when(notificationMapper.toResponse(mockNotification))
                    .thenReturn(mockResponse);

            NotificationResponse result =
                    notificationService.createNotification(
                            recipientId,
                            actorId,
                            NotificationType.FRIEND_POST,
                            referenceId,
                            "Bạn bè vừa đăng bài viết"
                    );

            assertNotNull(result);
            assertEquals(notificationId, result.getId());
            assertEquals(actorId, result.getActorId());

            verify(notificationRepository).save(argThat(notification ->
                    notification.getRecipientId().equals(recipientId)
                            && notification.getActorId().equals(actorId)
                            && notification.getType() == NotificationType.FRIEND_POST
                            && notification.getReferenceId().equals(referenceId)
                            && notification.getContent()
                            .equals("Bạn bè vừa đăng bài viết")
                            && !notification.isRead()
            ));

            verify(notificationMapper).toResponse(mockNotification);
        }

        @Test
        @DisplayName("createNotification - Không tạo thông báo cho chính mình")
        void createNotification_SameUser_ReturnsNull() {

            NotificationResponse result =
                    notificationService.createNotification(
                            recipientId,
                            recipientId,
                            NotificationType.FRIEND_POST,
                            referenceId,
                            "Nội dung"
                    );

            assertNull(result);

            verifyNoInteractions(
                    notificationRepository,
                    notificationMapper
            );
        }
    }

    // =======================================================
    // GET NOTIFICATIONS
    // =======================================================

    @Nested
    @DisplayName("Tests cho getNotifications()")
    class GetNotificationsTests {

        @Test
        @DisplayName("getNotifications - Thành công")
        void getNotifications_Success() {

            Pageable pageable = PageRequest.of(0, 10);

            Page<Notification> notificationPage =
                    new PageImpl<>(List.of(mockNotification), pageable, 1);

            when(notificationRepository
                    .findByRecipientIdOrderByCreatedAtDesc(
                            recipientId,
                            pageable
                    ))
                    .thenReturn(notificationPage);

            when(notificationMapper.toResponse(mockNotification))
                    .thenReturn(mockResponse);

            Page<NotificationResponse> result =
                    notificationService.getNotifications(
                            recipientId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(notificationId, result.getContent().get(0).getId());

            verify(notificationRepository)
                    .findByRecipientIdOrderByCreatedAtDesc(
                            recipientId,
                            pageable
                    );

            verify(notificationMapper).toResponse(mockNotification);
        }

        @Test
        @DisplayName("getNotifications - Không có thông báo")
        void getNotifications_EmptyPage() {

            Pageable pageable = PageRequest.of(0, 10);

            when(notificationRepository
                    .findByRecipientIdOrderByCreatedAtDesc(
                            recipientId,
                            pageable
                    ))
                    .thenReturn(Page.empty(pageable));

            Page<NotificationResponse> result =
                    notificationService.getNotifications(
                            recipientId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(notificationRepository)
                    .findByRecipientIdOrderByCreatedAtDesc(
                            recipientId,
                            pageable
                    );

            verifyNoInteractions(notificationMapper);
        }
    }

    // =======================================================
    // GET UNREAD COUNT
    // =======================================================

    @Nested
    @DisplayName("Tests cho getUnreadCount()")
    class GetUnreadCountTests {

        @Test
        @DisplayName("getUnreadCount - Có thông báo chưa đọc")
        void getUnreadCount_Success() {

            when(notificationRepository
                    .countByRecipientIdAndReadFalse(recipientId))
                    .thenReturn(5L);

            long result =
                    notificationService.getUnreadCount(recipientId);

            assertEquals(5L, result);

            verify(notificationRepository)
                    .countByRecipientIdAndReadFalse(recipientId);
        }

        @Test
        @DisplayName("getUnreadCount - Không có thông báo chưa đọc")
        void getUnreadCount_Zero() {

            when(notificationRepository
                    .countByRecipientIdAndReadFalse(recipientId))
                    .thenReturn(0L);

            long result =
                    notificationService.getUnreadCount(recipientId);

            assertEquals(0L, result);

            verify(notificationRepository)
                    .countByRecipientIdAndReadFalse(recipientId);
        }
    }

    // =======================================================
    // MARK AS READ
    // =======================================================

    @Nested
    @DisplayName("Tests cho markAsRead()")
    class MarkAsReadTests {

        @Test
        @DisplayName("markAsRead - Thành công")
        void markAsRead_Success() {

            when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.of(mockNotification));

            notificationService.markAsRead(recipientId, notificationId);

            assertTrue(mockNotification.isRead());

            verify(notificationRepository).findById(notificationId);
            verify(notificationRepository).save(mockNotification);
        }

        @Test
        @DisplayName("markAsRead - Không tìm thấy thông báo")
        void markAsRead_NotFound_ThrowsException() {

            when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> notificationService.markAsRead(
                            recipientId,
                            notificationId
                    )
            );

            assertEquals(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(notificationRepository).findById(notificationId);
            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("markAsRead - Người khác đánh dấu thông báo")
        void markAsRead_WrongRecipient_ThrowsException() {

            UUID otherUserId = UUID.randomUUID();

            when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.of(mockNotification));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> notificationService.markAsRead(
                            otherUserId,
                            notificationId
                    )
            );

            assertEquals(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("markAsRead - Thông báo đã đọc")
        void markAsRead_AlreadyRead() {

            mockNotification.setRead(true);

            when(notificationRepository.findById(notificationId))
                    .thenReturn(Optional.of(mockNotification));

            notificationService.markAsRead(recipientId, notificationId);

            assertTrue(mockNotification.isRead());

            verify(notificationRepository).findById(notificationId);
            verify(notificationRepository, never()).save(any());
        }
    }

    // =======================================================
    // MARK ALL AS READ
    // =======================================================

    @Nested
    @DisplayName("Tests cho markAllAsRead()")
    class MarkAllAsReadTests {

        @Test
        @DisplayName("markAllAsRead - Cập nhật thành công")
        void markAllAsRead_Success() {

            when(notificationRepository.markAllAsRead(recipientId))
                    .thenReturn(5);

            int result = notificationService.markAllAsRead(recipientId);

            assertEquals(5, result);

            verify(notificationRepository)
                    .markAllAsRead(recipientId);
        }

        @Test
        @DisplayName("markAllAsRead - Không có thông báo cần cập nhật")
        void markAllAsRead_NoNotifications() {

            when(notificationRepository.markAllAsRead(recipientId))
                    .thenReturn(0);

            int result = notificationService.markAllAsRead(recipientId);

            assertEquals(0, result);

            verify(notificationRepository)
                    .markAllAsRead(recipientId);
        }
    }
}
