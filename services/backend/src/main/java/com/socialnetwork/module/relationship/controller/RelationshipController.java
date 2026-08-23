package com.socialnetwork.module.relationship.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import com.socialnetwork.module.relationship.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    // ============================================================
    // GET RELATIONSHIP
    // ============================================================

    /**
     * Lấy trạng thái quan hệ giữa current user và target user
     *
     * GET /api/relationships/{targetUserId}
     */
    @GetMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<RelationshipResponse> getRelationship(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        RelationshipResponse response =
                relationshipService.getRelationship(
                        currentUserId,
                        targetUserId
                );

        return ApiResponse.success(response);
    }
}