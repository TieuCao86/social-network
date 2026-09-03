package com.socialnetwork.module.post.mapper;

import com.socialnetwork.module.post.dto.response.MediaItemResponse;
import com.socialnetwork.module.post.dto.response.PostAuthorResponse;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    PostAuthorResponse toAuthorResponse(User user);

    MediaItemResponse toMediaResponse(PostMedia postMedia);

    List<MediaItemResponse> toMediaResponseList(
            List<PostMedia> mediaList
    );

    default Page<PostResponse> toResponsePage(
            Page<Post> postsPage,
            Map<UUID, List<PostMedia>> mediaMap,
            Map<UUID, User> userMap
    ) {
        return postsPage.map(post -> {

            List<PostMedia> mediaList =
                    mediaMap.getOrDefault(
                            post.getId(),
                            Collections.<PostMedia>emptyList()
                    );

            PostResponse response =
                    toResponse(post, mediaList);

            User author = userMap.get(post.getAuthorId());

            if (author != null) {
                response.setAuthor(
                        toAuthorResponse(author)
                );
            }

            return response;
        });
    }
}