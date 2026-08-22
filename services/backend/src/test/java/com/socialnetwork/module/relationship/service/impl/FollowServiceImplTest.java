package com.socialnetwork.module.relationship.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import com.socialnetwork.module.relationship.entity.Follow;
import com.socialnetwork.module.relationship.mapper.FollowMapper;
import com.socialnetwork.module.relationship.repository.FollowRepository;
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
class FollowServiceImplTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private FollowMapper followMapper;

    @InjectMocks
    private FollowServiceImpl followService;

    private UUID currentUserId;
    private UUID targetUserId;
    private Follow sampleFollow;
    private FollowResponse sampleResponse;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();

        sampleFollow = Follow.builder()
                .id(UUID.randomUUID())
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();

        sampleResponse = FollowResponse.builder()
                .id(sampleFollow.getId())
                .build();
    }

    // ============================================================
    // 1. FOLLOW TESTS
    // ============================================================

    @Nested
    @DisplayName("follow()")
    class FollowTests {

        @Test
        @DisplayName("Follow thành công khi input hợp lệ và chưa từng follow")
        void follow_Success() {
            when(followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId))
                    .thenReturn(false);
            when(followRepository.save(any(Follow.class)))
                    .thenReturn(sampleFollow);
            when(followMapper.toResponse(sampleFollow))
                    .thenReturn(sampleResponse);

            FollowResponse result = followService.follow(currentUserId, targetUserId);

            assertNotNull(result);
            assertEquals(sampleResponse.getId(), result.getId());
            verify(followRepository, times(1)).save(any(Follow.class));
        }

        @Test
        @DisplayName("Ném lỗi CANNOT_FOLLOW_SELF khi tự follow chính mình")
        void follow_Self_ThrowsException() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> followService.follow(currentUserId, currentUserId)
            );

            assertEquals(ErrorCode.CANNOT_FOLLOW_SELF, ex.getErrorCode());
            verify(followRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ném lỗi ALREADY_FOLLOWING khi đã follow trước đó")
        void follow_AlreadyFollowing_ThrowsException() {
            when(followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId))
                    .thenReturn(true);

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> followService.follow(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.ALREADY_FOLLOWING, ex.getErrorCode());
            verify(followRepository, never()).save(any());
        }
    }

    // ============================================================
    // 2. UNFOLLOW TESTS
    // ============================================================

    @Nested
    @DisplayName("unfollow()")
    class UnfollowTests {

        @Test
        @DisplayName("Unfollow thành công khi đang follow")
        void unfollow_Success() {
            when(followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId))
                    .thenReturn(Optional.of(sampleFollow));
            doNothing().when(followRepository).delete(sampleFollow);

            assertDoesNotThrow(() -> followService.unfollow(currentUserId, targetUserId));
            verify(followRepository, times(1)).delete(sampleFollow);
        }

        @Test
        @DisplayName("Ném lỗi CANNOT_FOLLOW_SELF khi tự unfollow chính mình")
        void unfollow_Self_ThrowsException() {
            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> followService.unfollow(currentUserId, currentUserId)
            );

            assertEquals(ErrorCode.CANNOT_FOLLOW_SELF, ex.getErrorCode());
            verify(followRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Ném lỗi NOT_FOLLOWING khi chưa follow đối tượng")
        void unfollow_NotFollowing_ThrowsException() {
            when(followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId))
                    .thenReturn(Optional.empty());

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> followService.unfollow(currentUserId, targetUserId)
            );

            assertEquals(ErrorCode.NOT_FOLLOWING, ex.getErrorCode());
            verify(followRepository, never()).delete(any());
        }
    }

    // ============================================================
    // 3. IS FOLLOWING TESTS
    // ============================================================

    @Nested
    @DisplayName("isFollowing()")
    class IsFollowingTests {

        @Test
        @DisplayName("Trả về true khi đang follow")
        void isFollowing_True() {
            when(followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId))
                    .thenReturn(true);

            assertTrue(followService.isFollowing(currentUserId, targetUserId));
            verify(followRepository, times(1)).existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
        }

        @Test
        @DisplayName("Trả về false khi không follow")
        void isFollowing_False() {
            when(followRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId))
                    .thenReturn(false);

            assertFalse(followService.isFollowing(currentUserId, targetUserId));
            verify(followRepository, times(1)).existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
        }

        @Test
        @DisplayName("Trả về false ngay lập tức nếu kiểm tra với chính mình (không query DB)")
        void isFollowing_Self_ReturnsFalseWithoutQuerying() {
            assertFalse(followService.isFollowing(currentUserId, currentUserId));
            verify(followRepository, never()).existsByFollowerIdAndFollowingId(any(), any());
        }
    }

    // ============================================================
    // 4. PAGINATION & COUNT TESTS
    // ============================================================

    @Nested
    @DisplayName("Pagination & Count Queries")
    class QueryTests {

        @Test
        @DisplayName("Lấy danh sách following có phân trang")
        void getFollowing_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Follow> followPage = new PageImpl<>(List.of(sampleFollow));

            when(followRepository.findByFollowerId(currentUserId, pageable)).thenReturn(followPage);
            when(followMapper.toResponse(sampleFollow)).thenReturn(sampleResponse);

            Page<FollowResponse> result = followService.getFollowing(currentUserId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(followRepository, times(1)).findByFollowerId(currentUserId, pageable);
        }

        @Test
        @DisplayName("Lấy danh sách followers có phân trang")
        void getFollowers_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Follow> followPage = new PageImpl<>(List.of(sampleFollow));

            when(followRepository.findByFollowingId(currentUserId, pageable)).thenReturn(followPage);
            when(followMapper.toResponse(sampleFollow)).thenReturn(sampleResponse);

            Page<FollowResponse> result = followService.getFollowers(currentUserId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            verify(followRepository, times(1)).findByFollowingId(currentUserId, pageable);
        }

        @Test
        @DisplayName("Đếm số lượng following")
        void countFollowing_Success() {
            when(followRepository.countByFollowerId(currentUserId)).thenReturn(15L);

            long count = followService.countFollowing(currentUserId);

            assertEquals(15L, count);
            verify(followRepository, times(1)).countByFollowerId(currentUserId);
        }

        @Test
        @DisplayName("Đếm số lượng followers")
        void countFollowers_Success() {
            when(followRepository.countByFollowingId(currentUserId)).thenReturn(25L);

            long count = followService.countFollowers(currentUserId);

            assertEquals(25L, count);
            verify(followRepository, times(1)).countByFollowingId(currentUserId);
        }
    }
}