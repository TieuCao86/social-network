-- ============================================================
-- 1. USERS
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id             UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    username       VARCHAR(50)              NOT NULL,
    email          VARCHAR(255),
    phone          VARCHAR(20),
    password_hash  VARCHAR(255)             NOT NULL,
    status         VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',
    role           VARCHAR(20)              NOT NULL DEFAULT 'USER',
    email_verified BOOLEAN                  NOT NULL DEFAULT FALSE,
    phone_verified BOOLEAN                  NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_username
        UNIQUE (username),

    CONSTRAINT uk_users_email
        UNIQUE (email),

    CONSTRAINT uk_users_phone
        UNIQUE (phone),

    CONSTRAINT chk_users_role
        CHECK (role IN ('USER', 'MODERATOR', 'ADMIN')),

    CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED', 'PENDING_VERIFICATION'))
        );

-- ============================================================
-- 2. USER PROFILES
-- ============================================================

CREATE TABLE IF NOT EXISTS user_profiles (
    user_id        UUID                     PRIMARY KEY,
    full_name      VARCHAR(100),
    avatar_file_id UUID,
    cover_file_id  UUID,
    bio            VARCHAR(500),
    website        VARCHAR(255),
    location       VARCHAR(255),
    birth_date     DATE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        );

-- ============================================================
-- 3. USER SETTINGS
-- ============================================================

CREATE TABLE IF NOT EXISTS user_settings (
    user_id         UUID                     PRIMARY KEY,
    private_account BOOLEAN                  NOT NULL DEFAULT FALSE,
    allow_tagging   BOOLEAN                  NOT NULL DEFAULT TRUE,
    language        VARCHAR(10)              NOT NULL DEFAULT 'vi',
    theme           VARCHAR(20)              NOT NULL DEFAULT 'SYSTEM',
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_settings_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        );

-- ============================================================
-- 4. POSTS
-- ============================================================

CREATE TABLE IF NOT EXISTS posts (
    id             UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    author_id      UUID                     NOT NULL,
    content        TEXT,
    visibility     VARCHAR(20)              NOT NULL DEFAULT 'PUBLIC',
    status         VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',
    comment_count  INT                      NOT NULL DEFAULT 0,
    reaction_count INT                      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_posts_visibility
        CHECK (visibility IN ('PUBLIC', 'FRIENDS', 'PRIVATE')),

    CONSTRAINT chk_posts_status
        CHECK (status IN ('ACTIVE', 'ARCHIVED', 'DELETED'))
        );

-- ============================================================
-- 5. POST MEDIA
-- ============================================================

CREATE TABLE IF NOT EXISTS post_media (
    id         UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id    UUID                     NOT NULL,
    type       VARCHAR(20)              NOT NULL,
    file_id    UUID                     NOT NULL,
    sort_order INT                      NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_media_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_post_media_type
        CHECK (type IN ('IMAGE', 'VIDEO'))
        );

-- ============================================================
-- 6. POST REACTIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS post_reactions (
    id         UUID                     PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id    UUID                     NOT NULL,
    user_id    UUID                     NOT NULL,
    type       VARCHAR(20)              NOT NULL DEFAULT 'LIKE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_reactions_post
        FOREIGN KEY (post_id)
        REFERENCES posts (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_post_reactions_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT uq_post_user_reaction
        UNIQUE (post_id, user_id),

    CONSTRAINT chk_post_reaction_type
        CHECK (type IN ('LIKE', 'LOVE', 'HAHA', 'WOW', 'SAD', 'ANGRY'))
        );

-- ============================================================
-- 7. INDEXES TỐI ƯU HIỆU NĂNG
-- ============================================================

-- Lấy bài viết ACTIVE theo tác giả cho trang Profile Timeline
CREATE INDEX IF NOT EXISTS idx_posts_author_active_created
    ON posts (author_id, created_at DESC)
    WHERE status = 'ACTIVE';

-- Lấy bài viết ACTIVE cho Public Feed toàn hệ thống
CREATE INDEX IF NOT EXISTS idx_posts_active_created
    ON posts (created_at DESC)
    WHERE status = 'ACTIVE';

-- Sắp xếp Media theo thứ tự hiển thị của từng bài viết
CREATE INDEX IF NOT EXISTS idx_post_media_post_sort
    ON post_media (post_id, sort_order);

-- Lấy danh sách Reaction của bài viết theo thời gian
CREATE INDEX IF NOT EXISTS idx_post_reactions_post_created
    ON post_reactions (post_id, created_at DESC);

-- Lọc danh sách Reaction theo loại (LIKE, LOVE,...) có phân trang
CREATE INDEX IF NOT EXISTS idx_post_reactions_post_type_created
    ON post_reactions (post_id, type, created_at DESC);