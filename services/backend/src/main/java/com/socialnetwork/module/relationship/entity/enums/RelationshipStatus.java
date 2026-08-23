package com.socialnetwork.module.relationship.entity.enums;

public enum RelationshipStatus {

    /** Không có quan hệ */
    NONE,

    /** Mình đã gửi lời mời kết bạn */
    REQUEST_SENT,

    /** Người kia đã gửi lời mời kết bạn cho mình */
    REQUEST_RECEIVED,

    /** Hai người đã là bạn */
    FRIENDS,

    /** Mình đang chặn người kia */
    BLOCKING,

    /** Người kia đang chặn mình */
    BLOCKED_BY,

    /** Mình đang follow người kia */
    FOLLOWING,

    /** Người kia đang follow mình */
    FOLLOWED_BY,

    /** Hai bên follow lẫn nhau */
    FOLLOWING_EACH_OTHER
}