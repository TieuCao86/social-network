package com.socialnetwork.module.relationship.mapper;

import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.entity.Friendship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FriendshipMapper {

    @Mapping(target = "friendshipId", source = "id")
    FriendshipResponse toResponse(Friendship friendship);
}