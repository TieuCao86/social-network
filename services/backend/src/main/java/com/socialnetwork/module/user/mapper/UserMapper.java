package com.socialnetwork.module.user.mapper;

import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.dto.response.UserSearchResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "phoneVerified", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(source = "id", target = "userId")
    UserResponse toResponse(User user);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "profile.fullName", target = "fullName")
    @Mapping(source = "profile.avatarFileId", target = "avatarFileId")
    UserSearchResponse toSearchResponse(
            User user,
            UserProfile profile,
            RelationshipStatus relationshipStatus
    );
}