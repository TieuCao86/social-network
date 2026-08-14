package com.socialnetwork.module.post.dto.request;

import com.socialnetwork.module.post.entity.PostVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostCreateRequest {

    @Size(
            max = 5000,
            message = "Nội dung bài viết không được vượt quá 5000 ký tự"
    )
    private String content;

    private PostVisibility visibility = PostVisibility.PUBLIC;

    @Valid
    private List<PostMediaRequest> mediaList;
}