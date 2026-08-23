package com.socialnetwork.module.relationship.util;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;

import java.util.UUID;

public final class RelationshipValidator {

    private RelationshipValidator() {
    }

    public static void validateNotSelf(
            UUID currentUserId,
            UUID targetUserId,
            ErrorCode errorCode
    ) {
        if (currentUserId != null && currentUserId.equals(targetUserId)) {
            throw new BusinessException(errorCode);
        }
    }
}