package com.socialnetwork.module.comment.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.comment.dto.request.CommentMediaRequest;
import com.socialnetwork.module.comment.dto.request.CreateCommentRequest;
import com.socialnetwork.module.comment.dto.request.UpdateCommentRequest;
import com.socialnetwork.module.comment.dto.response.CommentAuthorResponse;
import com.socialnetwork.module.comment.dto.response.CommentResponse;
import com.socialnetwork.module.comment.entity.Comment;
import com.socialnetwork.module.comment.entity.CommentMedia;
import com.socialnetwork.module.comment.entity.CommentStatus;
import com.socialnetwork.module.comment.mapper.CommentMapper;
import com.socialnetwork.module.comment.repository.CommentMediaRepository;
import com.socialnetwork.module.comment.repository.CommentRepository;
import com.socialnetwork.module.comment.service.CommentService;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserProfile;
import com.socialnetwork.module.user.repository.UserProfileRepository;
import com.socialnetwork.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMediaRepository commentMediaRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CommentMapper commentMapper;

    // ============================================================
    // CREATE COMMENT
    // ============================================================

    @Override
    @Transactional
    public CommentResponse createComment(UUID currentUserId, UUID postId, CreateCommentRequest request) {
        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasMedia = request.getMediaList() != null && !request.getMediaList().isEmpty();

        if (!hasContent && !hasMedia) {
            throw new BusinessException(ErrorCode.COMMENT_CONTENT_REQUIRED);
        }

        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

            if (parentComment.getStatus() != CommentStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.PARENT_COMMENT_DELETED);
            }

            if (!parentComment.getPostId().equals(postId)) {
                throw new BusinessException(ErrorCode.PARENT_COMMENT_NOT_FOUND);
            }
        }

        Comment comment = commentMapper.toEntity(request);
        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setStatus(CommentStatus.ACTIVE);

        comment = commentRepository.save(comment);

        // Save Media
        if (hasMedia) {

            List<CommentMedia> mediaList = new ArrayList<>();

            for (int i = 0; i < request.getMediaList().size(); i++) {

                CommentMediaRequest mediaRequest =
                        request.getMediaList().get(i);

                CommentMedia media =
                        CommentMedia.builder()
                                .commentId(comment.getId())
                                .fileId(mediaRequest.getFileId())
                                .type(mediaRequest.getType())
                                .sortOrder(i)
                                .build();

                mediaList.add(media);
            }

            commentMediaRepository.saveAll(mediaList);
        }

        return toResponse(comment);
    }

    // ============================================================
    // UPDATE COMMENT
    // ============================================================

    @Override
    @Transactional
    public CommentResponse updateComment(UUID currentUserId, UUID commentId, UpdateCommentRequest request) {
        Comment comment = getComment(commentId);

        validateOwner(comment, currentUserId);
        validateActive(comment);

        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasMedia = request.getMediaList() != null && !request.getMediaList().isEmpty();

        if (!hasContent && !hasMedia) {
            throw new BusinessException(ErrorCode.COMMENT_CONTENT_REQUIRED);
        }

        commentMapper.update(request, comment);

        // Xóa media cũ
        commentMediaRepository.deleteByCommentId(comment.getId());

        // Lưu media mới
        if (hasMedia) {

            List<CommentMedia> mediaList =
                    IntStream.range(0, request.getMediaList().size())
                            .mapToObj(i -> {
                                CommentMediaRequest mediaRequest =
                                        request.getMediaList().get(i);

                                return CommentMedia.builder()
                                        .commentId(comment.getId())
                                        .fileId(mediaRequest.getFileId())
                                        .type(mediaRequest.getType())
                                        .sortOrder(i)
                                        .build();
                            })
                            .collect(Collectors.toList());

            commentMediaRepository.saveAll(mediaList);
        }

        return toResponse(comment);
    }

    // ============================================================
    // DELETE COMMENT
    // ============================================================

    @Override
    @Transactional
    public void deleteComment(UUID currentUserId, UUID commentId) {
        Comment comment = getComment(commentId);

        validateOwner(comment, currentUserId);
        validateActive(comment);

        comment.setStatus(CommentStatus.DELETED);
    }

    // ============================================================
    // GET COMMENTS
    // ============================================================

    @Override
    public Page<CommentResponse> getComments(UUID postId, Pageable pageable) {
        return commentRepository
                .findByPostIdAndParentIdIsNullAndStatusOrderByCreatedAtAsc(postId, CommentStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    // ============================================================
    // GET REPLIES
    // ============================================================

    @Override
    public Page<CommentResponse> getReplies(UUID commentId, Pageable pageable) {
        Comment comment = getComment(commentId);
        validateActive(comment);

        return commentRepository
                .findByParentIdAndStatusOrderByCreatedAtAsc(commentId, CommentStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    // ============================================================
    // COUNTS
    // ============================================================

    @Override
    public long countComments(UUID postId) {
        return commentRepository.countByPostIdAndStatus(postId, CommentStatus.ACTIVE);
    }

    @Override
    public long countReplies(UUID commentId) {
        return commentRepository.countByParentIdAndStatus(commentId, CommentStatus.ACTIVE);
    }

    // ============================================================
    // MAPPING COMMENT → RESPONSE
    // ============================================================

    private CommentResponse toResponse(Comment comment) {

        CommentResponse response =
                commentMapper.toResponse(comment);

        User user =
                userRepository.findById(comment.getUserId())
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        UserProfile profile =
                userProfileRepository.findById(comment.getUserId())
                        .orElse(null);

        response.setAuthor(
                CommentAuthorResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(
                                profile != null
                                        ? profile.getFullName()
                                        : null
                        )
                        .avatarFileId(
                                profile != null
                                        ? profile.getAvatarFileId()
                                        : null
                        )
                        .build()
        );

        response.setMediaList(
                commentMediaRepository
                        .findByCommentIdOrderBySortOrderAsc(
                                comment.getId()
                        )
                        .stream()
                        .map(commentMapper::toMediaResponse)
                        .toList()
        );

        response.setReplyCount(
                commentRepository.countByParentIdAndStatus(
                        comment.getId(),
                        CommentStatus.ACTIVE
                )
        );

        return response;
    }

    // ============================================================
    // COMMON
    // ============================================================

    private Comment getComment(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateOwner(Comment comment, UUID currentUserId) {
        if (!comment.getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_OWNER);
        }
    }

    private void validateActive(Comment comment) {
        if (comment.getStatus() != CommentStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.COMMENT_DELETED);
        }
    }
}