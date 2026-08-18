package com.socialnetwork.module.post.mapper;

import com.socialnetwork.module.post.dto.response.MediaItemResponse;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "mediaList", source = "mediaList")
    @Mapping(target = "totalReactions", source = "post.reactionCount")
    @Mapping(target = "commentCount", source = "post.commentCount")
    @Mapping(target = "currentUserReaction", ignore = true)
    @Mapping(target = "topReactions", ignore = true)
    @Mapping(target = "shareCount", constant = "0L")
    PostResponse toResponse(
            Post post,
            List<PostMedia> mediaList
    );


    @Mapping(target = "mediaList", ignore = true)
    @Mapping(target = "totalReactions", source = "reactionCount")
    @Mapping(target = "commentCount", source = "commentCount")
    @Mapping(target = "currentUserReaction", ignore = true)
    @Mapping(target = "topReactions", ignore = true)
    @Mapping(target = "shareCount", constant = "0L")
    PostResponse toResponse(Post post);


    MediaItemResponse toMediaResponse(PostMedia postMedia);


    List<MediaItemResponse> toMediaResponseList(
            List<PostMedia> mediaList
    );
}