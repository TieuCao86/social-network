package com.socialnetwork.module.comment.repository;

import com.socialnetwork.module.comment.entity.CommentMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CommentMediaRepository
        extends JpaRepository<CommentMedia, UUID> {

    List<CommentMedia> findByCommentIdOrderBySortOrderAsc(
            UUID commentId
    );

    List<CommentMedia> findByCommentIdInOrderByCommentIdAscSortOrderAsc(
            Collection<UUID> commentIds
    );

    void deleteByCommentId(UUID commentId);
}