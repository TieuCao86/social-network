package com.socialnetwork.module.post.mapper;

import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface PostMapper {

    // 1. Map Post + MediaList
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "mediaList", source = "mediaList")
    @Mapping(target = "totalReactions", ignore = true)
    @Mapping(target = "currentUserReaction", ignore = true)
    @Mapping(target = "reactionSummary", ignore = true)
    PostResponse toResponse(Post post, List<PostMedia> mediaList);

    // 2. Map đầy đủ kèm theo Reaction Metadata
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "mediaList", source = "mediaList")
    @Mapping(target = "totalReactions", source = "totalReactions")
    @Mapping(target = "currentUserReaction", source = "currentUserReaction")
    @Mapping(target = "reactionSummary", source = "reactionSummary")
    PostResponse toResponse(
            Post post,
            List<PostMedia> mediaList,
            long totalReactions,
            ReactionType currentUserReaction,
            Map<ReactionType, Long> reactionSummary
    );

    // 3. Map đơn lẻ Post -> PostResponse
    @Mapping(target = "mediaList", ignore = true)
    @Mapping(target = "totalReactions", ignore = true)
    @Mapping(target = "currentUserReaction", ignore = true)
    @Mapping(target = "reactionSummary", ignore = true)
    PostResponse toResponse(Post post);

    // 4. Map PostMedia -> MediaItemResponse
    PostResponse.MediaItemResponse toMediaResponse(PostMedia postMedia);

    // 5. Map List<PostMedia> -> List<MediaItemResponse>
    List<PostResponse.MediaItemResponse> toMediaResponseList(List<PostMedia> mediaList);
}