package com.socialnetwork.module.post.mapper;

import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    // 1. Phương thức map kết hợp từ 2 đối tượng đầu vào (Post + Danh sách PostMedia)
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "mediaList", source = "mediaList") // Gán trực tiếp mediaList vào PostResponse
    PostResponse toResponse(Post post, List<PostMedia> mediaList);

    // 2. Map đơn lẻ Post -> PostResponse (nếu bài viết không có media)
    @Mapping(target = "mediaList", ignore = true)
    PostResponse toResponse(Post post);

    // 3. Map PostMedia -> MediaItemResponse
    PostResponse.MediaItemResponse toMediaResponse(PostMedia postMedia);

    // 4. Map List<PostMedia> -> List<MediaItemResponse>
    List<PostResponse.MediaItemResponse> toMediaResponseList(List<PostMedia> mediaList);
}