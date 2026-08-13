package com.socialnetwork.module.user.mapper;

import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
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

    UserResponse toResponse(User user);
}