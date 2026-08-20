package com.socialnetwork.module.relationship.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import com.socialnetwork.module.relationship.mapper.FriendshipMapper;
import com.socialnetwork.module.relationship.mapper.RelationshipMapper;
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

    @Mock
    private RelationshipMapper relationshipMapper;

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
    // 1. TESTS CHO SEND REQUEST
    // ============================================================
    @Nested
    @DisplayName("Tests cho sendRequest()")
    class SendRequestTests {

        @Test
        @DisplayName("sendRequest - Tự gửi lời mời cho chính mình ném CANNOT_FRIEND_SELF")
        void sendRequest_Self_ThrowsException() {
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(currentUserId, currentUserId)
            );

            assertEquals(ErrorCode.CANNOT_FRIEND_SELF, exception.getErrorCode());
            verifyNoInteractions(friendshipRepository, friendshipMapper);
        }

        @Test
        @DisplayName("sendRequest - Thành công khi chưa từng có quan hệ trước đó")
        void sendRequest_NewRequest_Success() {
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.empty());
            when(friendshipRepository.save(any(Friendship.class))).thenReturn(mockFriendship);
            when(friendshipMapper.toResponse(mockFriendship)).thenReturn(mockFriendshipResponse);

            FriendshipResponse result = friendshipService.sendRequest(currentUserId, targetUserId);

            assertNotNull(result);
            assertEquals(mockFriendshipResponse.getId(), result.getId());
            verify(friendshipRepository).findBetween(currentUserId, targetUserId);
            verify(friendshipRepository).save(any(Friendship.class));
            verify(friendshipMapper).toResponse(mockFriendship);
        }

        @Test
        @DisplayName("sendRequest - Đã là bạn bè ném ALREADY_FRIENDS")
        void sendRequest_AlreadyFriends_ThrowsException() {
            mockFriendship.setStatus(FriendshipStatus.ACCEPTED);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.ALREADY_FRIENDS, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }

        @Test
        @DisplayName("sendRequest - Đang chặn đối phương ném BLOCKING_USER")
        void sendRequest_BlockingUser_ThrowsException() {
            mockFriendship.setStatus(FriendshipStatus.BLOCKED);
            mockFriendship.setRequesterId(currentUserId);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.BLOCKING_USER, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }

        @Test
        @DisplayName("sendRequest - Bị đối phương chặn ném BLOCKED_BY_USER")
        void sendRequest_BlockedByUser_ThrowsException() {
            mockFriendship.setStatus(FriendshipStatus.BLOCKED);
            mockFriendship.setRequesterId(targetUserId);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.BLOCKED_BY_USER, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }

        @Test
        @DisplayName("sendRequest - Mình đã gửi lời mời trước đó ném FRIEND_REQUEST_ALREADY_SENT")
        void sendRequest_AlreadySent_ThrowsException() {
            mockFriendship.setStatus(FriendshipStatus.PENDING);
            mockFriendship.setRequesterId(currentUserId);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.FRIEND_REQUEST_ALREADY_SENT, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }

        @Test
        @DisplayName("sendRequest - Đối phương đã gửi lời mời trước đó ném FRIEND_REQUEST_ALREADY_RECEIVED")
        void sendRequest_AlreadyReceived_ThrowsException() {
            mockFriendship.setStatus(FriendshipStatus.PENDING);
            mockFriendship.setRequesterId(targetUserId);
            mockFriendship.setAddresseeId(currentUserId);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.sendRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.FRIEND_REQUEST_ALREADY_RECEIVED, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }
    }

    // ============================================================
    // 2. TESTS CHO ACCEPT REQUEST
    // ============================================================
    @Nested
    @DisplayName("Tests cho acceptRequest()")
    class AcceptRequestTests {

        @Test
        @DisplayName("acceptRequest - Thành công khi đúng người nhận và trạng thái PENDING")
        void acceptRequest_Success() {
            // targetUserId là người gửi, currentUserId là người nhận
            mockFriendship.setRequesterId(targetUserId);
            mockFriendship.setAddresseeId(currentUserId);
            mockFriendship.setStatus(FriendshipStatus.PENDING);

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));
            when(friendshipRepository.save(mockFriendship)).thenReturn(mockFriendship);
            when(friendshipMapper.toResponse(mockFriendship)).thenReturn(mockFriendshipResponse);

            FriendshipResponse response = friendshipService.acceptRequest(currentUserId, targetUserId);

            assertNotNull(response);
            assertEquals(FriendshipStatus.ACCEPTED, mockFriendship.getStatus());
            verify(friendshipRepository).save(mockFriendship);
            verify(friendshipMapper).toResponse(mockFriendship);
        }

        @Test
        @DisplayName("acceptRequest - Ném FRIEND_REQUEST_NOT_FOUND khi sai chiều người nhận")
        void acceptRequest_WrongDirection_ThrowsException() {
            // currentUserId là người gửi, không thể tự accept request của chính mình
            mockFriendship.setRequesterId(currentUserId);
            mockFriendship.setAddresseeId(targetUserId);

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.acceptRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.FRIEND_REQUEST_NOT_FOUND, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }

        @Test
        @DisplayName("acceptRequest - Lời mời không ở trạng thái PENDING ném INVALID_FRIEND_REQUEST_STATUS")
        void acceptRequest_NotPending_ThrowsException() {
            mockFriendship.setRequesterId(targetUserId);
            mockFriendship.setAddresseeId(currentUserId);
            mockFriendship.setStatus(FriendshipStatus.ACCEPTED);

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.acceptRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.INVALID_FRIEND_REQUEST_STATUS, exception.getErrorCode());
            verify(friendshipRepository, never()).save(any());
        }
    }

    // ============================================================
    // 3. TESTS CHO REJECT REQUEST
    // ============================================================
    @Nested
    @DisplayName("Tests cho rejectRequest()")
    class RejectRequestTests {

        @Test
        @DisplayName("rejectRequest - Từ chối thành công và xóa bản ghi")
        void rejectRequest_Success() {
            mockFriendship.setRequesterId(targetUserId);
            mockFriendship.setAddresseeId(currentUserId);
            mockFriendship.setStatus(FriendshipStatus.PENDING);

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            friendshipService.rejectRequest(currentUserId, targetUserId);

            verify(friendshipRepository).delete(mockFriendship);
        }

        @Test
        @DisplayName("rejectRequest - Không tìm thấy yêu cầu ném FRIEND_REQUEST_NOT_FOUND")
        void rejectRequest_NotFound_ThrowsException() {
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.rejectRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.FRIEND_REQUEST_NOT_FOUND, exception.getErrorCode());
            verify(friendshipRepository, never()).delete(any());
        }
    }

    // ============================================================
    // 4. TESTS CHO CANCEL REQUEST
    // ============================================================
    @Nested
    @DisplayName("Tests cho cancelRequest()")
    class CancelRequestTests {

        @Test
        @DisplayName("cancelRequest - Người gửi tự hủy lời mời thành công")
        void cancelRequest_Success() {
            mockFriendship.setRequesterId(currentUserId);
            mockFriendship.setAddresseeId(targetUserId);
            mockFriendship.setStatus(FriendshipStatus.PENDING);

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            friendshipService.cancelRequest(currentUserId, targetUserId);

            verify(friendshipRepository).delete(mockFriendship);
        }

        @Test
        @DisplayName("cancelRequest - Người nhận cố hủy thay người gửi ném FRIEND_REQUEST_NOT_FOUND")
        void cancelRequest_ByAddressee_ThrowsException() {
            mockFriendship.setRequesterId(targetUserId);
            mockFriendship.setAddresseeId(currentUserId);

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.cancelRequest(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.FRIEND_REQUEST_NOT_FOUND, exception.getErrorCode());
            verify(friendshipRepository, never()).delete(any());
        }
    }

    // ============================================================
    // 5. TESTS CHO UNFRIEND
    // ============================================================
    @Nested
    @DisplayName("Tests cho unfriend()")
    class UnfriendTests {

        @Test
        @DisplayName("unfriend - Hủy kết bạn thành công")
        void unfriend_Success() {
            mockFriendship.setStatus(FriendshipStatus.ACCEPTED);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            friendshipService.unfriend(currentUserId, targetUserId);

            verify(friendshipRepository).delete(mockFriendship);
        }

        @Test
        @DisplayName("unfriend - Chưa từng kết bạn ném FRIENDSHIP_NOT_FOUND")
        void unfriend_NotFound_ThrowsException() {
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.unfriend(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.FRIENDSHIP_NOT_FOUND, exception.getErrorCode());
            verify(friendshipRepository, never()).delete(any());
        }

        @Test
        @DisplayName("unfriend - Trạng thái không phải ACCEPTED ném NOT_FRIENDS")
        void unfriend_NotAccepted_ThrowsException() {
            mockFriendship.setStatus(FriendshipStatus.PENDING);
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> friendshipService.unfriend(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.NOT_FRIENDS, exception.getErrorCode());
            verify(friendshipRepository, never()).delete(any());
        }
    }

    // ============================================================
    // 6. TESTS CHO GET RELATIONSHIP
    // ============================================================
    @Nested
    @DisplayName("Tests cho getRelationship()")
    class GetRelationshipTests {

        @Test
        @DisplayName("getRelationship - Trả về trạng thái từ RelationshipMapper khi tìm thấy")
        void getRelationship_Found_Success() {
            RelationshipResponse expectedResponse = RelationshipResponse.builder()
                    .userId(targetUserId)
                    .relationshipStatus(RelationshipStatus.FRIENDS)
                    .build();

            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.of(mockFriendship));
            when(relationshipMapper.toResponse(currentUserId, targetUserId, mockFriendship)).thenReturn(expectedResponse);

            RelationshipResponse result = friendshipService.getRelationship(currentUserId, targetUserId);

            assertNotNull(result);
            assertEquals(RelationshipStatus.FRIENDS, result.getRelationshipStatus());
            verify(relationshipMapper).toResponse(currentUserId, targetUserId, mockFriendship);
        }

        @Test
        @DisplayName("getRelationship - Trả về NONE khi không tìm thấy bản ghi")
        void getRelationship_NotFound_ReturnsNone() {
            when(friendshipRepository.findBetween(currentUserId, targetUserId)).thenReturn(Optional.empty());

            RelationshipResponse result = friendshipService.getRelationship(currentUserId, targetUserId);

            assertNotNull(result);
            assertEquals(targetUserId, result.getUserId());
            assertEquals(RelationshipStatus.NONE, result.getRelationshipStatus());
            verifyNoInteractions(relationshipMapper);
        }
    }

    // ============================================================
    // 7. TESTS CHO QUERIES (GET LIST & COUNT)
    // ============================================================
    @Nested
    @DisplayName("Tests cho danh sách bạn bè và lời mời")
    class QueryListTests {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        @DisplayName("getFriends - Lấy danh sách bạn bè thành công")
        void getFriends_Success() {
            Page<Friendship> page = new PageImpl<>(List.of(mockFriendship), pageable, 1);
            when(friendshipRepository.findAllByUserIdAndStatus(currentUserId, FriendshipStatus.ACCEPTED, pageable))
                    .thenReturn(page);
            when(friendshipMapper.toResponse(mockFriendship)).thenReturn(mockFriendshipResponse);

            Page<FriendshipResponse> result = friendshipService.getFriends(currentUserId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(friendshipRepository).findAllByUserIdAndStatus(currentUserId, FriendshipStatus.ACCEPTED, pageable);
        }

        @Test
        @DisplayName("getReceivedRequests - Lấy danh sách lời mời nhận được thành công")
        void getReceivedRequests_Success() {
            Page<Friendship> page = new PageImpl<>(List.of(mockFriendship), pageable, 1);
            when(friendshipRepository.findByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING, pageable))
                    .thenReturn(page);
            when(friendshipMapper.toResponse(mockFriendship)).thenReturn(mockFriendshipResponse);

            Page<FriendshipResponse> result = friendshipService.getReceivedRequests(currentUserId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(friendshipRepository).findByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING, pageable);
        }

        @Test
        @DisplayName("getSentRequests - Lấy danh sách lời mời đã gửi thành công")
        void getSentRequests_Success() {
            Page<Friendship> page = new PageImpl<>(List.of(mockFriendship), pageable, 1);
            when(friendshipRepository.findByRequesterIdAndStatus(currentUserId, FriendshipStatus.PENDING, pageable))
                    .thenReturn(page);
            when(friendshipMapper.toResponse(mockFriendship)).thenReturn(mockFriendshipResponse);

            Page<FriendshipResponse> result = friendshipService.getSentRequests(currentUserId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(friendshipRepository).findByRequesterIdAndStatus(currentUserId, FriendshipStatus.PENDING, pageable);
        }

        @Test
        @DisplayName("countFriends - Đếm số lượng bạn bè")
        void countFriends_Success() {
            when(friendshipRepository.countFriends(currentUserId)).thenReturn(42L);

            long count = friendshipService.countFriends(currentUserId);

            assertEquals(42L, count);
            verify(friendshipRepository).countFriends(currentUserId);
        }
    }
}