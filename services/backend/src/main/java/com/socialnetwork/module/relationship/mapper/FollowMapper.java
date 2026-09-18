package com.socialnetwork.module.relationship.mapper;

import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import com.socialnetwork.module.relationship.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FollowMapper {

    @Mapping(target = "followId", source = "id")
    FollowResponse toResponse(Follow follow);
}