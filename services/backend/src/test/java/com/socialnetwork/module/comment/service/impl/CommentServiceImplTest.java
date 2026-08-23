package com.socialnetwork.module.comment.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.comment.dto.request.CreateCommentRequest;
import com.socialnetwork.module.comment.dto.request.UpdateCommentRequest;
import com.socialnetwork.module.comment.dto.response.CommentResponse;
import com.socialnetwork.module.comment.entity.Comment;
import com.socialnetwork.module.comment.entity.CommentStatus;
import com.socialnetwork.module.comment.mapper.CommentMapper;
import com.socialnetwork.module.comment.repository.CommentRepository;
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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID currentUserId;
    private UUID otherUserId;
    private UUID postId;
    private UUID commentId;
    private UUID parentCommentId;

    private Comment sampleComment;
    private Comment parentComment;
    private CommentResponse sampleResponse;

    @BeforeEach
    void setUp() {

        currentUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();

        postId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        parentCommentId = UUID.randomUUID();

        sampleComment = Comment.builder()
                .id(commentId)
                .postId(postId)
                .userId(currentUserId)
                .parentId(null)
                .content("Đây là comment test")
                .status(CommentStatus.ACTIVE)
                .build();

        parentComment = Comment.builder()
                .id(parentCommentId)
                .postId(postId)
                .userId(otherUserId)
                .parentId(null)
                .content("Comment cha")
                .status(CommentStatus.ACTIVE)
                .build();

        sampleResponse = CommentResponse.builder()
                .id(commentId)
                .postId(postId)
                .userId(currentUserId)
                .content("Đây là comment test")
                .status(CommentStatus.ACTIVE)
                .build();
    }

    // ============================================================
    // 1. CREATE COMMENT
    // ============================================================

    @Nested
    @DisplayName("createComment()")
    class CreateCommentTests {

        @Test
        @DisplayName("Tạo comment gốc thành công")
        void createComment_Root_Success() {

            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Comment mới")
                    .parentId(null)
                    .build();

            Comment comment = Comment.builder()
                    .postId(postId)
                    .userId(currentUserId)
                    .content("Comment mới")
                    .status(CommentStatus.ACTIVE)
                    .build();

            when(commentMapper.toEntity(request))
                    .thenReturn(comment);

            when(commentRepository.save(comment))
                    .thenReturn(sampleComment);

            when(commentMapper.toResponse(sampleComment))
                    .thenReturn(sampleResponse);

            CommentResponse result =
                    commentService.createComment(
                            currentUserId,
                            postId,
                            request
                    );

            assertNotNull(result);
            assertEquals(sampleResponse.getId(), result.getId());

            verify(commentMapper).toEntity(request);
            verify(commentRepository).save(comment);
            verify(commentMapper).toResponse(sampleComment);
        }

        @Test
        @DisplayName("Tạo reply thành công khi comment cha tồn tại")
        void createComment_Reply_Success() {

            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Reply")
                    .parentId(parentCommentId)
                    .build();

            Comment reply = Comment.builder()
                    .postId(postId)
                    .userId(currentUserId)
                    .parentId(parentCommentId)
                    .content("Reply")
                    .status(CommentStatus.ACTIVE)
                    .build();

            when(commentRepository.findById(parentCommentId))
                    .thenReturn(Optional.of(parentComment));

            when(commentMapper.toEntity(request))
                    .thenReturn(reply);

            when(commentRepository.save(reply))
                    .thenReturn(sampleComment);

            when(commentMapper.toResponse(sampleComment))
                    .thenReturn(sampleResponse);

            CommentResponse result =
                    commentService.createComment(
                            currentUserId,
                            postId,
                            request
                    );

            assertNotNull(result);

            verify(commentRepository)
                    .findById(parentCommentId);

            verify(commentRepository)
                    .save(reply);
        }

        @Test
        @DisplayName("Reply thất bại khi không tìm thấy comment cha")
        void createComment_ParentNotFound_ThrowsException() {

            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Reply")
                    .parentId(parentCommentId)
                    .build();

            when(commentRepository.findById(parentCommentId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.createComment(
                            currentUserId,
                            postId,
                            request
                    )
            );

            assertEquals(
                    ErrorCode.PARENT_COMMENT_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(commentRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("Reply thất bại khi comment cha đã bị xóa")
        void createComment_ParentDeleted_ThrowsException() {

            parentComment.setStatus(CommentStatus.DELETED);

            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Reply")
                    .parentId(parentCommentId)
                    .build();

            when(commentRepository.findById(parentCommentId))
                    .thenReturn(Optional.of(parentComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.createComment(
                            currentUserId,
                            postId,
                            request
                    )
            );

            assertEquals(
                    ErrorCode.PARENT_COMMENT_DELETED,
                    exception.getErrorCode()
            );

            verify(commentRepository, never())
                    .save(any());
        }

        @Test
        @DisplayName("Reply thất bại khi comment cha thuộc post khác")
        void createComment_ParentDifferentPost_ThrowsException() {

            parentComment.setPostId(UUID.randomUUID());

            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("Reply")
                    .parentId(parentCommentId)
                    .build();

            when(commentRepository.findById(parentCommentId))
                    .thenReturn(Optional.of(parentComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.createComment(
                            currentUserId,
                            postId,
                            request
                    )
            );

            assertEquals(
                    ErrorCode.PARENT_COMMENT_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(commentRepository, never())
                    .save(any());
        }
    }

    // ============================================================
    // 2. UPDATE COMMENT
    // ============================================================

    @Nested
    @DisplayName("updateComment()")
    class UpdateCommentTests {

        @Test
        @DisplayName("Cập nhật comment thành công")
        void updateComment_Success() {

            UpdateCommentRequest request = UpdateCommentRequest.builder()
                    .content("Nội dung mới")
                    .build();

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            doNothing()
                    .when(commentMapper)
                    .update(request, sampleComment);

            when(commentMapper.toResponse(sampleComment))
                    .thenReturn(sampleResponse);

            CommentResponse result =
                    commentService.updateComment(
                            currentUserId,
                            commentId,
                            request
                    );

            assertNotNull(result);

            verify(commentRepository)
                    .findById(commentId);

            verify(commentMapper)
                    .update(request, sampleComment);

            verify(commentMapper)
                    .toResponse(sampleComment);
        }

        @Test
        @DisplayName("Cập nhật thất bại khi không tìm thấy comment")
        void updateComment_NotFound_ThrowsException() {

            UpdateCommentRequest request = UpdateCommentRequest.builder()
                    .content("Nội dung mới")
                    .build();

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.updateComment(
                            currentUserId,
                            commentId,
                            request
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(commentMapper, never())
                    .update(any(), any());
        }

        @Test
        @DisplayName("Cập nhật thất bại khi không phải chủ comment")
        void updateComment_NotOwner_ThrowsException() {

            UpdateCommentRequest request = UpdateCommentRequest.builder()
                    .content("Nội dung mới")
                    .build();

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.updateComment(
                            otherUserId,
                            commentId,
                            request
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_NOT_OWNER,
                    exception.getErrorCode()
            );

            verify(commentMapper, never())
                    .update(any(), any());
        }

        @Test
        @DisplayName("Cập nhật thất bại khi comment đã bị xóa")
        void updateComment_Deleted_ThrowsException() {

            sampleComment.setStatus(CommentStatus.DELETED);

            UpdateCommentRequest request = UpdateCommentRequest.builder()
                    .content("Nội dung mới")
                    .build();

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.updateComment(
                            currentUserId,
                            commentId,
                            request
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_DELETED,
                    exception.getErrorCode()
            );

            verify(commentMapper, never())
                    .update(any(), any());
        }
    }

    // ============================================================
    // 3. DELETE COMMENT
    // ============================================================

    @Nested
    @DisplayName("deleteComment()")
    class DeleteCommentTests {

        @Test
        @DisplayName("Xóa comment thành công bằng soft delete")
        void deleteComment_Success() {

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            commentService.deleteComment(
                    currentUserId,
                    commentId
            );

            assertEquals(
                    CommentStatus.DELETED,
                    sampleComment.getStatus()
            );

            verify(commentRepository)
                    .findById(commentId);

            verify(commentRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("Xóa thất bại khi không tìm thấy comment")
        void deleteComment_NotFound_ThrowsException() {

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.deleteComment(
                            currentUserId,
                            commentId
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(commentRepository, never())
                    .delete(any());
        }

        @Test
        @DisplayName("Xóa thất bại khi không phải chủ comment")
        void deleteComment_NotOwner_ThrowsException() {

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.deleteComment(
                            otherUserId,
                            commentId
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_NOT_OWNER,
                    exception.getErrorCode()
            );

            assertEquals(
                    CommentStatus.ACTIVE,
                    sampleComment.getStatus()
            );
        }

        @Test
        @DisplayName("Xóa thất bại khi comment đã bị xóa")
        void deleteComment_AlreadyDeleted_ThrowsException() {

            sampleComment.setStatus(CommentStatus.DELETED);

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.deleteComment(
                            currentUserId,
                            commentId
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_DELETED,
                    exception.getErrorCode()
            );
        }
    }

    // ============================================================
    // 4. GET COMMENTS
    // ============================================================

    @Nested
    @DisplayName("getComments()")
    class GetCommentsTests {

        private final Pageable pageable =
                PageRequest.of(0, 10);

        @Test
        @DisplayName("Lấy danh sách comment của post thành công")
        void getComments_Success() {

            Page<Comment> page =
                    new PageImpl<>(
                            List.of(sampleComment),
                            pageable,
                            1
                    );

            when(commentRepository
                    .findByPostIdAndParentIdIsNullAndStatus(
                            postId,
                            CommentStatus.ACTIVE,
                            pageable
                    ))
                    .thenReturn(page);

            when(commentMapper.toResponse(sampleComment))
                    .thenReturn(sampleResponse);

            Page<CommentResponse> result =
                    commentService.getComments(
                            postId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(
                    sampleResponse.getId(),
                    result.getContent().get(0).getId()
            );

            verify(commentRepository)
                    .findByPostIdAndParentIdIsNullAndStatus(
                            postId,
                            CommentStatus.ACTIVE,
                            pageable
                    );
        }

        @Test
        @DisplayName("Không có comment trả về page rỗng")
        void getComments_Empty() {

            Page<Comment> page =
                    new PageImpl<>(
                            List.of(),
                            pageable,
                            0
                    );

            when(commentRepository
                    .findByPostIdAndParentIdIsNullAndStatus(
                            postId,
                            CommentStatus.ACTIVE,
                            pageable
                    ))
                    .thenReturn(page);

            Page<CommentResponse> result =
                    commentService.getComments(
                            postId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());
            assertEquals(0, result.getTotalElements());
        }
    }

    // ============================================================
    // 5. GET REPLIES
    // ============================================================

    @Nested
    @DisplayName("getReplies()")
    class GetRepliesTests {

        private final Pageable pageable =
                PageRequest.of(0, 10);

        @Test
        @DisplayName("Lấy replies thành công")
        void getReplies_Success() {

            Comment reply = Comment.builder()
                    .id(UUID.randomUUID())
                    .postId(postId)
                    .userId(currentUserId)
                    .parentId(commentId)
                    .content("Reply")
                    .status(CommentStatus.ACTIVE)
                    .build();

            CommentResponse replyResponse =
                    CommentResponse.builder()
                            .id(reply.getId())
                            .parentId(commentId)
                            .content("Reply")
                            .status(CommentStatus.ACTIVE)
                            .build();

            Page<Comment> page =
                    new PageImpl<>(
                            List.of(reply),
                            pageable,
                            1
                    );

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            when(commentRepository.findByParentIdAndStatus(
                    commentId,
                    CommentStatus.ACTIVE,
                    pageable
            )).thenReturn(page);

            when(commentMapper.toResponse(reply))
                    .thenReturn(replyResponse);

            Page<CommentResponse> result =
                    commentService.getReplies(
                            commentId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            verify(commentRepository)
                    .findById(commentId);

            verify(commentRepository)
                    .findByParentIdAndStatus(
                            commentId,
                            CommentStatus.ACTIVE,
                            pageable
                    );
        }

        @Test
        @DisplayName("Lấy replies thất bại khi comment cha không tồn tại")
        void getReplies_ParentNotFound_ThrowsException() {

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.getReplies(
                            commentId,
                            pageable
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(commentRepository, never())
                    .findByParentIdAndStatus(
                            any(),
                            any(),
                            any()
                    );
        }

        @Test
        @DisplayName("Lấy replies thất bại khi comment cha đã bị xóa")
        void getReplies_ParentDeleted_ThrowsException() {

            sampleComment.setStatus(CommentStatus.DELETED);

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.of(sampleComment));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> commentService.getReplies(
                            commentId,
                            pageable
                    )
            );

            assertEquals(
                    ErrorCode.COMMENT_DELETED,
                    exception.getErrorCode()
            );

            verify(commentRepository, never())
                    .findByParentIdAndStatus(
                            any(),
                            any(),
                            any()
                    );
        }
    }

    // ============================================================
    // 6. COUNT COMMENTS
    // ============================================================

    @Nested
    @DisplayName("countComments()")
    class CountCommentsTests {

        @Test
        @DisplayName("Đếm tất cả comment + reply của post")
        void countComments_Success() {

            when(commentRepository.countByPostIdAndStatus(
                    postId,
                    CommentStatus.ACTIVE
            )).thenReturn(10L);

            long result =
                    commentService.countComments(postId);

            assertEquals(10L, result);

            verify(commentRepository)
                    .countByPostIdAndStatus(
                            postId,
                            CommentStatus.ACTIVE
                    );
        }

        @Test
        @DisplayName("Không có comment trả về 0")
        void countComments_Empty_ReturnsZero() {

            when(commentRepository.countByPostIdAndStatus(
                    postId,
                    CommentStatus.ACTIVE
            )).thenReturn(0L);

            long result =
                    commentService.countComments(postId);

            assertEquals(0L, result);
        }
    }

    // ============================================================
    // 7. COUNT REPLIES
    // ============================================================

    @Nested
    @DisplayName("countReplies()")
    class CountRepliesTests {

        @Test
        @DisplayName("Đếm số replies của comment")
        void countReplies_Success() {

            when(commentRepository.countByParentIdAndStatus(
                    commentId,
                    CommentStatus.ACTIVE
            )).thenReturn(5L);

            long result =
                    commentService.countReplies(commentId);

            assertEquals(5L, result);

            verify(commentRepository)
                    .countByParentIdAndStatus(
                            commentId,
                            CommentStatus.ACTIVE
                    );
        }

        @Test
        @DisplayName("Không có reply trả về 0")
        void countReplies_Empty_ReturnsZero() {

            when(commentRepository.countByParentIdAndStatus(
                    commentId,
                    CommentStatus.ACTIVE
            )).thenReturn(0L);

            long result =
                    commentService.countReplies(commentId);

            assertEquals(0L, result);
        }
    }
}