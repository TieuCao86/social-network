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
    // 1. FOLLOW
    // ============================================================

    @Nested
    @DisplayName("follow()")
    class FollowTests {

        @Test
        @DisplayName("Follow thành công")
        void follow_Success() {

            when(followRepository
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    ))
                    .thenReturn(false);

            when(followRepository.save(any(Follow.class)))
                    .thenReturn(sampleFollow);

            when(followMapper.toResponse(sampleFollow))
                    .thenReturn(sampleResponse);

            FollowResponse result =
                    followService.follow(
                            currentUserId,
                            targetUserId
                    );

            assertNotNull(result);
            assertEquals(
                    sampleResponse.getId(),
                    result.getId()
            );

            verify(followRepository)
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    );

            verify(followRepository)
                    .save(any(Follow.class));

            verify(followMapper)
                    .toResponse(sampleFollow);
        }

        @Test
        @DisplayName("Không được tự follow chính mình")
        void follow_Self_ThrowsException() {

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> followService.follow(
                                    currentUserId,
                                    currentUserId
                            )
                    );

            assertEquals(
                    ErrorCode.CANNOT_FOLLOW_SELF,
                    exception.getErrorCode()
            );

            verifyNoInteractions(followRepository);
            verifyNoInteractions(followMapper);
        }

        @Test
        @DisplayName("Đã follow trước đó")
        void follow_AlreadyFollowing_ThrowsException() {

            when(followRepository
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    ))
                    .thenReturn(true);

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> followService.follow(
                                    currentUserId,
                                    targetUserId
                            )
                    );

            assertEquals(
                    ErrorCode.ALREADY_FOLLOWING,
                    exception.getErrorCode()
            );

            verify(followRepository)
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    );

            verify(followRepository, never())
                    .save(any());

            verifyNoInteractions(followMapper);
        }
    }

    // ============================================================
    // 2. UNFOLLOW
    // ============================================================

    @Nested
    @DisplayName("unfollow()")
    class UnfollowTests {

        @Test
        @DisplayName("Unfollow thành công")
        void unfollow_Success() {

            when(followRepository
                    .findByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    ))
                    .thenReturn(Optional.of(sampleFollow));

            assertDoesNotThrow(
                    () -> followService.unfollow(
                            currentUserId,
                            targetUserId
                    )
            );

            verify(followRepository)
                    .findByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    );

            verify(followRepository)
                    .delete(sampleFollow);
        }

        @Test
        @DisplayName("Không được tự unfollow chính mình")
        void unfollow_Self_ThrowsException() {

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> followService.unfollow(
                                    currentUserId,
                                    currentUserId
                            )
                    );

            assertEquals(
                    ErrorCode.CANNOT_FOLLOW_SELF,
                    exception.getErrorCode()
            );

            verifyNoInteractions(followRepository);
        }

        @Test
        @DisplayName("Chưa follow người dùng")
        void unfollow_NotFollowing_ThrowsException() {

            when(followRepository
                    .findByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    ))
                    .thenReturn(Optional.empty());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> followService.unfollow(
                                    currentUserId,
                                    targetUserId
                            )
                    );

            assertEquals(
                    ErrorCode.NOT_FOLLOWING,
                    exception.getErrorCode()
            );

            verify(followRepository, never())
                    .delete(any());
        }
    }

    // ============================================================
    // 3. IS FOLLOWING
    // ============================================================

    @Nested
    @DisplayName("isFollowing()")
    class IsFollowingTests {

        @Test
        @DisplayName("Đang follow -> true")
        void isFollowing_True() {

            when(followRepository
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    ))
                    .thenReturn(true);

            boolean result =
                    followService.isFollowing(
                            currentUserId,
                            targetUserId
                    );

            assertTrue(result);

            verify(followRepository)
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    );
        }

        @Test
        @DisplayName("Không follow -> false")
        void isFollowing_False() {

            when(followRepository
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    ))
                    .thenReturn(false);

            boolean result =
                    followService.isFollowing(
                            currentUserId,
                            targetUserId
                    );

            assertFalse(result);

            verify(followRepository)
                    .existsByFollowerIdAndFollowingId(
                            currentUserId,
                            targetUserId
                    );
        }

        @Test
        @DisplayName("Kiểm tra chính mình -> false và không query DB")
        void isFollowing_Self_ReturnsFalse() {

            boolean result =
                    followService.isFollowing(
                            currentUserId,
                            currentUserId
                    );

            assertFalse(result);

            verifyNoInteractions(followRepository);
        }
    }

    // ============================================================
    // 4. GET FOLLOWING
    // ============================================================

    @Nested
    @DisplayName("getFollowing()")
    class GetFollowingTests {

        @Test
        @DisplayName("Lấy danh sách following thành công")
        void getFollowing_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Follow> page =
                    new PageImpl<>(
                            List.of(sampleFollow),
                            pageable,
                            1
                    );

            when(followRepository
                    .findByFollowerId(
                            currentUserId,
                            pageable
                    ))
                    .thenReturn(page);

            when(followMapper.toResponse(sampleFollow))
                    .thenReturn(sampleResponse);

            Page<FollowResponse> result =
                    followService.getFollowing(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(
                    sampleResponse.getId(),
                    result.getContent().get(0).getId()
            );

            verify(followRepository)
                    .findByFollowerId(
                            currentUserId,
                            pageable
                    );

            verify(followMapper)
                    .toResponse(sampleFollow);
        }

        @Test
        @DisplayName("Không có following -> page rỗng")
        void getFollowing_Empty() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Follow> page =
                    new PageImpl<>(
                            List.of(),
                            pageable,
                            0
                    );

            when(followRepository
                    .findByFollowerId(
                            currentUserId,
                            pageable
                    ))
                    .thenReturn(page);

            Page<FollowResponse> result =
                    followService.getFollowing(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());

            verify(followRepository)
                    .findByFollowerId(
                            currentUserId,
                            pageable
                    );

            verifyNoInteractions(followMapper);
        }
    }

    // ============================================================
    // 5. GET FOLLOWERS
    // ============================================================

    @Nested
    @DisplayName("getFollowers()")
    class GetFollowersTests {

        @Test
        @DisplayName("Lấy danh sách followers thành công")
        void getFollowers_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Follow> page =
                    new PageImpl<>(
                            List.of(sampleFollow),
                            pageable,
                            1
                    );

            when(followRepository
                    .findByFollowingId(
                            currentUserId,
                            pageable
                    ))
                    .thenReturn(page);

            when(followMapper.toResponse(sampleFollow))
                    .thenReturn(sampleResponse);

            Page<FollowResponse> result =
                    followService.getFollowers(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            verify(followRepository)
                    .findByFollowingId(
                            currentUserId,
                            pageable
                    );

            verify(followMapper)
                    .toResponse(sampleFollow);
        }

        @Test
        @DisplayName("Không có followers -> page rỗng")
        void getFollowers_Empty() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Follow> page =
                    new PageImpl<>(
                            List.of(),
                            pageable,
                            0
                    );

            when(followRepository
                    .findByFollowingId(
                            currentUserId,
                            pageable
                    ))
                    .thenReturn(page);

            Page<FollowResponse> result =
                    followService.getFollowers(
                            currentUserId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());

            verify(followRepository)
                    .findByFollowingId(
                            currentUserId,
                            pageable
                    );

            verifyNoInteractions(followMapper);
        }
    }

    // ============================================================
    // 6. COUNT FOLLOWING
    // ============================================================

    @Nested
    @DisplayName("countFollowing()")
    class CountFollowingTests {

        @Test
        @DisplayName("Đếm following thành công")
        void countFollowing_Success() {

            when(followRepository
                    .countByFollowerId(currentUserId))
                    .thenReturn(15L);

            long result =
                    followService.countFollowing(
                            currentUserId
                    );

            assertEquals(15L, result);

            verify(followRepository)
                    .countByFollowerId(currentUserId);
        }

        @Test
        @DisplayName("Không có following -> 0")
        void countFollowing_Zero() {

            when(followRepository
                    .countByFollowerId(currentUserId))
                    .thenReturn(0L);

            long result =
                    followService.countFollowing(
                            currentUserId
                    );

            assertEquals(0L, result);

            verify(followRepository)
                    .countByFollowerId(currentUserId);
        }
    }

    // ============================================================
    // 7. COUNT FOLLOWERS
    // ============================================================

    @Nested
    @DisplayName("countFollowers()")
    class CountFollowersTests {

        @Test
        @DisplayName("Đếm followers thành công")
        void countFollowers_Success() {

            when(followRepository
                    .countByFollowingId(currentUserId))
                    .thenReturn(25L);

            long result =
                    followService.countFollowers(
                            currentUserId
                    );

            assertEquals(25L, result);

            verify(followRepository)
                    .countByFollowingId(currentUserId);
        }

        @Test
        @DisplayName("Không có followers -> 0")
        void countFollowers_Zero() {

            when(followRepository
                    .countByFollowingId(currentUserId))
                    .thenReturn(0L);

            long result =
                    followService.countFollowers(
                            currentUserId
                    );

            assertEquals(0L, result);

            verify(followRepository)
                    .countByFollowingId(currentUserId);
        }
    }
}