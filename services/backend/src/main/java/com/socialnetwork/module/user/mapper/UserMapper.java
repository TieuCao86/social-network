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

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phone", target = "phone")
    @Mapping(source = "user.status", target = "status")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.emailVerified", target = "emailVerified")
    @Mapping(source = "user.phoneVerified", target = "phoneVerified")
    @Mapping(source = "profile.fullName", target = "fullName")
    @Mapping(source = "profile.avatarFileId", target = "avatarFileId")
    @Mapping(source = "profile.coverFileId", target = "coverFileId")
    @Mapping(source = "profile.bio", target = "bio")
    @Mapping(source = "profile.website", target = "website")
    @Mapping(source = "profile.location", target = "location")
    @Mapping(source = "profile.birthDate", target = "birthDate")
    UserResponse toResponse(User user, UserProfile profile);

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