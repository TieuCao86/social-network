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
import com.socialnetwork.module.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    // ============================================================
    // CREATE COMMENT
    // ============================================================

    @Override
    @Transactional
    public CommentResponse createComment(
            UUID currentUserId,
            UUID postId,
            CreateCommentRequest request
    ) {

        // --------------------------------------------------------
        // Nếu là reply thì kiểm tra comment cha
        // --------------------------------------------------------

        if (request.getParentId() != null) {

            Comment parentComment = commentRepository
                    .findById(request.getParentId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    ErrorCode.PARENT_COMMENT_NOT_FOUND
                            )
                    );

            if (parentComment.getStatus() != CommentStatus.ACTIVE) {
                throw new BusinessException(
                        ErrorCode.PARENT_COMMENT_DELETED
                );
            }

            // Không cho reply comment thuộc post khác
            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException(
                        ErrorCode.PARENT_COMMENT_NOT_FOUND
                );
            }
        }

        // --------------------------------------------------------
        // Tạo comment
        // --------------------------------------------------------

        Comment comment = commentMapper.toEntity(request);

        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setStatus(CommentStatus.ACTIVE);

        comment = commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    // ============================================================
    // UPDATE COMMENT
    // ============================================================

    @Override
    @Transactional
    public CommentResponse updateComment(
            UUID currentUserId,
            UUID commentId,
            UpdateCommentRequest request
    ) {

        Comment comment = getComment(commentId);

        // --------------------------------------------------------
        // Kiểm tra quyền sở hữu
        // --------------------------------------------------------

        validateOwner(comment, currentUserId);

        // --------------------------------------------------------
        // Không cho sửa comment đã xóa
        // --------------------------------------------------------

        validateActive(comment);

        // --------------------------------------------------------
        // Update content
        // --------------------------------------------------------

        commentMapper.update(request, comment);

        return commentMapper.toResponse(comment);
    }

    // ============================================================
    // DELETE COMMENT
    // ============================================================

    @Override
    @Transactional
    public void deleteComment(
            UUID currentUserId,
            UUID commentId
    ) {

        Comment comment = getComment(commentId);

        // --------------------------------------------------------
        // Kiểm tra quyền sở hữu
        // --------------------------------------------------------

        validateOwner(comment, currentUserId);

        // --------------------------------------------------------
        // Nếu đã deleted thì báo lỗi
        // --------------------------------------------------------

        validateActive(comment);

        // --------------------------------------------------------
        // Soft delete
        // --------------------------------------------------------

        comment.setStatus(CommentStatus.DELETED);
    }

    // ============================================================
    // GET COMMENTS
    // ============================================================

    @Override
    public Page<CommentResponse> getComments(
            UUID postId,
            Pageable pageable
    ) {

        return commentRepository
                .findByPostIdAndParentIdIsNullAndStatus(
                        postId,
                        CommentStatus.ACTIVE,
                        pageable
                )
                .map(commentMapper::toResponse);
    }

    // ============================================================
    // GET REPLIES
    // ============================================================

    @Override
    public Page<CommentResponse> getReplies(
            UUID commentId,
            Pageable pageable
    ) {

        Comment comment = getComment(commentId);

        validateActive(comment);

        return commentRepository
                .findByParentIdAndStatus(
                        commentId,
                        CommentStatus.ACTIVE,
                        pageable
                )
                .map(commentMapper::toResponse);
    }

    // ============================================================
    // COUNT COMMENTS
    // ============================================================

    @Override
    public long countComments(UUID postId) {

        return commentRepository.countByPostIdAndStatus(
                postId,
                CommentStatus.ACTIVE
        );
    }

    // ============================================================
    // COUNT REPLIES
    // ============================================================

    @Override
    public long countReplies(UUID commentId) {

        return commentRepository.countByParentIdAndStatus(
                commentId,
                CommentStatus.ACTIVE
        );
    }

    // ============================================================
    // PRIVATE METHODS
    // ============================================================

    private Comment getComment(UUID commentId) {

        return commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.COMMENT_NOT_FOUND
                        )
                );
    }

    private void validateOwner(
            Comment comment,
            UUID currentUserId
    ) {

        if (!comment.getUserId().equals(currentUserId)) {
            throw new BusinessException(
                    ErrorCode.COMMENT_NOT_OWNER
            );
        }
    }

    private void validateActive(Comment comment) {

        if (comment.getStatus() != CommentStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.COMMENT_DELETED
            );
        }
    }
}