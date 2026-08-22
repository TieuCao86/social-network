package com.socialnetwork.module.relationship.mapper;

import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import com.socialnetwork.module.relationship.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FollowMapper {

    FollowResponse toResponse(Follow follow);
}