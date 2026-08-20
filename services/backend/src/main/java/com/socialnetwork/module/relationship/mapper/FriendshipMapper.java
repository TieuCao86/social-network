package com.socialnetwork.module.relationship.mapper;

import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.entity.Friendship;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    FriendshipResponse toResponse(Friendship friendship);
}