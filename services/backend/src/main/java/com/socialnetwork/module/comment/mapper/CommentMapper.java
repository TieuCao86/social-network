package com.socialnetwork.module.comment.mapper;

import com.socialnetwork.module.comment.dto.request.CreateCommentRequest;
import com.socialnetwork.module.comment.dto.request.UpdateCommentRequest;
import com.socialnetwork.module.comment.dto.response.CommentMediaResponse;
import com.socialnetwork.module.comment.dto.response.CommentResponse;
import com.socialnetwork.module.comment.entity.Comment;
import com.socialnetwork.module.comment.entity.CommentMedia;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommentMapper {

    // ============================================================
    // CREATE
    // ============================================================

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toEntity(CreateCommentRequest request);


    // ============================================================
    // RESPONSE
    // ============================================================

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "mediaList", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    CommentResponse toResponse(Comment comment);


    // ============================================================
    // UPDATE
    // ============================================================

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "postId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(
            UpdateCommentRequest request,
            @MappingTarget Comment comment
    );


    // ============================================================
    // MEDIA RESPONSE
    // ============================================================

    CommentMediaResponse toMediaResponse(CommentMedia media);
}