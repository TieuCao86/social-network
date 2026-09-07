
-- ============================================================
-- COMMENT MEDIA
-- ============================================================

CREATE TABLE IF NOT EXISTS comment_media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    comment_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    file_id UUID NOT NULL,

    sort_order INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment_media_comment
        FOREIGN KEY (comment_id)
        REFERENCES comments(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_comment_media_type
        CHECK (type IN ('IMAGE', 'VIDEO', 'GIF')),

    CONSTRAINT uq_comment_media_order
        UNIQUE (comment_id, sort_order)
);

-- Lấy media của comment theo thứ tự
CREATE INDEX IF NOT EXISTS idx_comment_media_comment
    ON comment_media (comment_id, sort_order ASC);

-- Hỗ trợ truy vấn theo file
CREATE INDEX IF NOT EXISTS idx_comment_media_file
    ON comment_media (file_id);

