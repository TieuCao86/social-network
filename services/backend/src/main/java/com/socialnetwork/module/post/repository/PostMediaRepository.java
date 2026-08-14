package com.socialnetwork.module.post.repository;

import com.socialnetwork.module.post.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostMediaRepository extends JpaRepository<PostMedia, UUID> {

    List<PostMedia> findByPostIdOrderBySortOrderAsc(UUID postId);

    List<PostMedia> findByPostIdInOrderByPostIdAscSortOrderAsc(
            List<UUID> postIds
    );

    void deleteByPostId(UUID postId);
}