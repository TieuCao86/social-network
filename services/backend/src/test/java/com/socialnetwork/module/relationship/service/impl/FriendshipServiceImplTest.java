package com.socialnetwork.module.relationship.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import com.socialnetwork.module.relationship.mapper.FriendshipMapper;
import com.socialnetwork.module.relationship.repository.FriendshipRepository;
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
class FriendshipServiceImplTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private FriendshipMapper friendshipMapper;

    @InjectMocks
    private FriendshipServiceImpl friendshipService;

    private UUID currentUserId;
    private UUID targetUserId;
    private UUID friendshipId;

    private Friendship mockFriendship;
    private FriendshipResponse mockFriendshipResponse;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        friendshipId = UUID.randomUUID();

        mockFriendship = Friendship.builder()
                .id(friendshipId)
                .requesterId(currentUserId)
                .addresseeId(targetUserId)
                .status(FriendshipStatus.PENDING)
                .build();

        mockFriendshipResponse = FriendshipResponse.builder()
                .id(friendshipId)
                .requesterId(currentUserId)
                .addresseeId(targetUserId)
                .status(FriendshipStatus.PENDING)
                .build();
    }

    // ============================================================
    // 1. SEND REQUEST
    // ============================================================

    @Nested
    @DisplayName("Tests cho sendRequest()")
    class SendRequestTests {

        @Test
        @DisplayName("sendRequest - Tự gửi lời mời cho chính mình")
        void sendRequest_Self_ThrowsException() {

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(
                            currentUserId,
                            currentUserId
                    )
            );

            assertEquals(
                    ErrorCode.CANNOT_FRIEND_SELF,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    friendshipRepository,
                    friendshipMapper
            );
        }

        @Test
        @DisplayName("sendRequest - Thành công khi chưa có quan hệ")
        void sendRequest_NewRequest_Success() {

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.empty());

            when(friendshipRepository.save(
                    any(Friendship.class)
            )).thenReturn(mockFriendship);

            when(friendshipMapper.toResponse(
                    mockFriendship
            )).thenReturn(mockFriendshipResponse);

            FriendshipResponse result =
                    friendshipService.sendRequest(
                            currentUserId,
                            targetUserId
                    );

            assertNotNull(result);
            assertEquals(
                    friendshipId,
                    result.getId()
            );

            verify(friendshipRepository)
                    .findBetween(currentUserId, targetUserId);

            verify(friendshipRepository)
                    .save(any(Friendship.class));

            verify(friendshipMapper)
                    .toResponse(mockFriendship);
        }

        @Test
        @DisplayName("sendRequest - Đã là bạn bè")
        void sendRequest_AlreadyFriends_ThrowsException() {

            mockFriendship.setStatus(
                    FriendshipStatus.ACCEPTED
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.ALREADY_FRIENDS,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("sendRequest - Mình đang chặn người kia")
        void sendRequest_BlockingUser_ThrowsException() {

            mockFriendship.setStatus(
                    FriendshipStatus.BLOCKED
            );

            mockFriendship.setRequesterId(
                    currentUserId
            );

            mockFriendship.setAddresseeId(
                    targetUserId
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.BLOCKING_USER,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("sendRequest - Bị người kia chặn")
        void sendRequest_BlockedByUser_ThrowsException() {

            mockFriendship.setStatus(
                    FriendshipStatus.BLOCKED
            );

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.BLOCKED_BY_USER,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("sendRequest - Mình đã gửi lời mời")
        void sendRequest_AlreadySent_ThrowsException() {

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            mockFriendship.setRequesterId(
                    currentUserId
            );

            mockFriendship.setAddresseeId(
                    targetUserId
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_ALREADY_SENT,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("sendRequest - Người kia đã gửi lời mời")
        void sendRequest_AlreadyReceived_ThrowsException() {

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_ALREADY_RECEIVED,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }
    }

    // ============================================================
    // 2. ACCEPT REQUEST
    // ============================================================

    @Nested
    @DisplayName("Tests cho acceptRequest()")
    class AcceptRequestTests {

        @Test
        @DisplayName("acceptRequest - Thành công")
        void acceptRequest_Success() {

            // requesterId là người gửi
            // currentUserId là người nhận

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            when(friendshipRepository.save(
                    mockFriendship
            )).thenReturn(mockFriendship);

            when(friendshipMapper.toResponse(
                    mockFriendship
            )).thenReturn(mockFriendshipResponse);

            FriendshipResponse response =
                    friendshipService.acceptRequest(
                            currentUserId,
                            targetUserId
                    );

            assertNotNull(response);

            assertEquals(
                    FriendshipStatus.ACCEPTED,
                    mockFriendship.getStatus()
            );

            verify(friendshipRepository)
                    .findBetween(
                            currentUserId,
                            targetUserId
                    );

            verify(friendshipRepository)
                    .save(mockFriendship);

            verify(friendshipMapper)
                    .toResponse(mockFriendship);
        }

        @Test
        @DisplayName("acceptRequest - Tự accept lời mời của chính mình")
        void acceptRequest_Self_ThrowsException() {

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.acceptRequest(
                            currentUserId,
                            currentUserId
                    )
            );

            assertEquals(
                    ErrorCode.CANNOT_FRIEND_SELF,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    friendshipRepository,
                    friendshipMapper
            );
        }

        @Test
        @DisplayName("acceptRequest - Không tìm thấy lời mời")
        void acceptRequest_NotFound_ThrowsException() {

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.acceptRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("acceptRequest - Sai chiều lời mời")
        void acceptRequest_WrongDirection_ThrowsException() {

            // currentUserId là người gửi
            // nên không có quyền accept

            mockFriendship.setRequesterId(
                    currentUserId
            );

            mockFriendship.setAddresseeId(
                    targetUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.acceptRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("acceptRequest - Trạng thái không phải PENDING")
        void acceptRequest_NotPending_ThrowsException() {

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.ACCEPTED
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.acceptRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.INVALID_FRIEND_REQUEST_STATUS,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .save(any());
        }
    }

    // ============================================================
    // 3. REJECT REQUEST
    // ============================================================

    @Nested
    @DisplayName("Tests cho rejectRequest()")
    class RejectRequestTests {

        @Test
        @DisplayName("rejectRequest - Từ chối thành công")
        void rejectRequest_Success() {

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            friendshipService.rejectRequest(
                    currentUserId,
                    targetUserId
            );

            verify(friendshipRepository)
                    .delete(mockFriendship);
        }

        @Test
        @DisplayName("rejectRequest - Không tìm thấy lời mời")
        void rejectRequest_NotFound_ThrowsException() {

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.rejectRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("rejectRequest - Sai chiều lời mời")
        void rejectRequest_WrongDirection_ThrowsException() {

            mockFriendship.setRequesterId(
                    currentUserId
            );

            mockFriendship.setAddresseeId(
                    targetUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.rejectRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("rejectRequest - Trạng thái không phải PENDING")
        void rejectRequest_NotPending_ThrowsException() {

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.ACCEPTED
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.rejectRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.INVALID_FRIEND_REQUEST_STATUS,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }
    }

    // ============================================================
    // 4. CANCEL REQUEST
    // ============================================================

    @Nested
    @DisplayName("Tests cho cancelRequest()")
    class CancelRequestTests {

        @Test
        @DisplayName("cancelRequest - Người gửi hủy thành công")
        void cancelRequest_Success() {

            mockFriendship.setRequesterId(
                    currentUserId
            );

            mockFriendship.setAddresseeId(
                    targetUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            friendshipService.cancelRequest(
                    currentUserId,
                    targetUserId
            );

            verify(friendshipRepository)
                    .delete(mockFriendship);
        }

        @Test
        @DisplayName("cancelRequest - Người nhận không thể hủy")
        void cancelRequest_ByAddressee_ThrowsException() {

            mockFriendship.setRequesterId(
                    targetUserId
            );

            mockFriendship.setAddresseeId(
                    currentUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.cancelRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("cancelRequest - Không tìm thấy lời mời")
        void cancelRequest_NotFound_ThrowsException() {

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.cancelRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIEND_REQUEST_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("cancelRequest - Trạng thái không phải PENDING")
        void cancelRequest_NotPending_ThrowsException() {

            mockFriendship.setRequesterId(
                    currentUserId
            );

            mockFriendship.setAddresseeId(
                    targetUserId
            );

            mockFriendship.setStatus(
                    FriendshipStatus.ACCEPTED
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.cancelRequest(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.INVALID_FRIEND_REQUEST_STATUS,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }
    }

    // ============================================================
    // 5. UNFRIEND
    // ============================================================

    @Nested
    @DisplayName("Tests cho unfriend()")
    class UnfriendTests {

        @Test
        @DisplayName("unfriend - Hủy kết bạn thành công")
        void unfriend_Success() {

            mockFriendship.setStatus(
                    FriendshipStatus.ACCEPTED
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            friendshipService.unfriend(
                    currentUserId,
                    targetUserId
            );

            verify(friendshipRepository)
                    .delete(mockFriendship);
        }

        @Test
        @DisplayName("unfriend - Không tìm thấy quan hệ")
        void unfriend_NotFound_ThrowsException() {

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.unfriend(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.FRIENDSHIP_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("unfriend - Không ở trạng thái ACCEPTED")
        void unfriend_NotAccepted_ThrowsException() {

            mockFriendship.setStatus(
                    FriendshipStatus.PENDING
            );

            when(friendshipRepository.findBetween(
                    currentUserId,
                    targetUserId
            )).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.unfriend(
                            currentUserId,
                            targetUserId
                    )
            );

            assertEquals(
                    ErrorCode.NOT_FRIENDS,
                    exception.getErrorCode()
            );

            verify(friendshipRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("unfriend - Tự hủy kết bạn với chính mình")
        void unfriend_Self_ThrowsException() {

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.unfriend(
                            currentUserId,
                            currentUserId
                    )
            );

            assertEquals(
                    ErrorCode.CANNOT_FRIEND_SELF,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    friendshipRepository,
                    friendshipMapper
            );
        }
    }

    // ============================================================
    // 6. GET FRIENDS
    // ============================================================

    @Nested
    @DisplayName("Tests cho getFriends()")
    class GetFriendsTests {

        @Test
        @DisplayName("getFriends - Lấy danh sách bạn bè thành công")
        void getFriends_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Friendship> page =
                    new PageImpl<>(
                            List.of(mockFriendship),
                            pageable,
                            1
                    );

            when(friendshipRepository.findAllByUserIdAndStatus(
                    currentUserId,
                    FriendshipStatus.ACCEPTED,
                    pageable
            )).thenReturn(page);

            when(friendshipMapper.toResponse(
                    mockFriendship
            )).thenReturn(mockFriendshipResponse);

            Page<FriendshipResponse> result =
                    friendshipService.getFriends(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(
                    1,
                    result.getTotalElements()
            );

            verify(friendshipRepository)
                    .findAllByUserIdAndStatus(
                            currentUserId,
                            FriendshipStatus.ACCEPTED,
                            pageable
                    );
        }

        @Test
        @DisplayName("getFriends - Không có bạn bè")
        void getFriends_Empty_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Friendship> page =
                    new PageImpl<>(
                            List.of(),
                            pageable,
                            0
                    );

            when(friendshipRepository.findAllByUserIdAndStatus(
                    currentUserId,
                    FriendshipStatus.ACCEPTED,
                    pageable
            )).thenReturn(page);

            Page<FriendshipResponse> result =
                    friendshipService.getFriends(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(friendshipRepository)
                    .findAllByUserIdAndStatus(
                            currentUserId,
                            FriendshipStatus.ACCEPTED,
                            pageable
                    );
        }
    }

    // ============================================================
    // 7. RECEIVED REQUESTS
    // ============================================================

    @Nested
    @DisplayName("Tests cho getReceivedRequests()")
    class GetReceivedRequestsTests {

        @Test
        @DisplayName("getReceivedRequests - Thành công")
        void getReceivedRequests_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Friendship> page =
                    new PageImpl<>(
                            List.of(mockFriendship),
                            pageable,
                            1
                    );

            when(friendshipRepository.findByAddresseeIdAndStatus(
                    currentUserId,
                    FriendshipStatus.PENDING,
                    pageable
            )).thenReturn(page);

            when(friendshipMapper.toResponse(
                    mockFriendship
            )).thenReturn(mockFriendshipResponse);

            Page<FriendshipResponse> result =
                    friendshipService.getReceivedRequests(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(
                    1,
                    result.getTotalElements()
            );

            verify(friendshipRepository)
                    .findByAddresseeIdAndStatus(
                            currentUserId,
                            FriendshipStatus.PENDING,
                            pageable
                    );
        }
    }

    // ============================================================
    // 8. SENT REQUESTS
    // ============================================================

    @Nested
    @DisplayName("Tests cho getSentRequests()")
    class GetSentRequestsTests {

        @Test
        @DisplayName("getSentRequests - Thành công")
        void getSentRequests_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Friendship> page =
                    new PageImpl<>(
                            List.of(mockFriendship),
                            pageable,
                            1
                    );

            when(friendshipRepository.findByRequesterIdAndStatus(
                    currentUserId,
                    FriendshipStatus.PENDING,
                    pageable
            )).thenReturn(page);

            when(friendshipMapper.toResponse(
                    mockFriendship
            )).thenReturn(mockFriendshipResponse);

            Page<FriendshipResponse> result =
                    friendshipService.getSentRequests(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(
                    1,
                    result.getTotalElements()
            );

            verify(friendshipRepository)
                    .findByRequesterIdAndStatus(
                            currentUserId,
                            FriendshipStatus.PENDING,
                            pageable
                    );
        }
    }

    // ============================================================
    // 9. COUNT FRIENDS
    // ============================================================

    @Nested
    @DisplayName("Tests cho countFriends()")
    class CountFriendsTests {

        @Test
        @DisplayName("countFriends - Đếm số lượng bạn bè")
        void countFriends_Success() {

            when(friendshipRepository.countFriends(
                    currentUserId
            )).thenReturn(42L);

            long count =
                    friendshipService.countFriends(
                            currentUserId
                    );

            assertEquals(42L, count);

            verify(friendshipRepository)
                    .countFriends(currentUserId);
        }

        @Test
        @DisplayName("countFriends - Không có bạn bè")
        void countFriends_Zero() {

            when(friendshipRepository.countFriends(
                    currentUserId
            )).thenReturn(0L);

            long count =
                    friendshipService.countFriends(
                            currentUserId
                    );

            assertEquals(0L, count);

            verify(friendshipRepository)
                    .countFriends(currentUserId);
        }
    }
}