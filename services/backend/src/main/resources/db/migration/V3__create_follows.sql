-- ============================================================
-- FOLLOWS
-- ============================================================

CREATE TABLE IF NOT EXISTS follows (
                                       id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    follower_id  UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               -- ========================================================
                               -- FOREIGN KEYS
                               -- ========================================================

                               CONSTRAINT fk_follows_follower
                               FOREIGN KEY (follower_id)
    REFERENCES users (id)
                           ON DELETE CASCADE,

    CONSTRAINT fk_follows_following
    FOREIGN KEY (following_id)
    REFERENCES users (id)
                           ON DELETE CASCADE,

    -- ========================================================
    -- VALIDATION
    -- ========================================================

    CONSTRAINT chk_follows_self
    CHECK (follower_id <> following_id),

    -- Một user chỉ follow một user khác một lần
    CONSTRAINT uq_follows_pair
    UNIQUE (follower_id, following_id)
    );

-- ============================================================
-- INDEXES
-- ============================================================

-- Lấy danh sách những người user đang follow
CREATE INDEX IF NOT EXISTS idx_follows_follower_created
    ON follows (follower_id, created_at DESC);

-- Lấy danh sách follower của user
CREATE INDEX IF NOT EXISTS idx_follows_following_created
    ON follows (following_id, created_at DESC);