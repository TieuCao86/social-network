-- ============================================================
-- COMMENTS
-- ============================================================

CREATE TABLE IF NOT EXISTS comments (
                                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    post_id UUID NOT NULL,
    user_id UUID NOT NULL,
    parent_id UUID,

    content TEXT,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_comments_post
                             FOREIGN KEY (post_id)
    REFERENCES posts(id)
                         ON DELETE CASCADE,

    CONSTRAINT fk_comments_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
                         ON DELETE CASCADE,

    CONSTRAINT fk_comments_parent
    FOREIGN KEY (parent_id)
    REFERENCES comments(id)
                         ON DELETE CASCADE,

    CONSTRAINT chk_comments_status
    CHECK (
              status IN (
              'ACTIVE',
              'DELETED',
              'HIDDEN',
              'FLAGGED'
                        )
    ),

    CONSTRAINT chk_comments_content_or_empty
    CHECK (
              content IS NULL
              OR length(trim(content)) > 0
    )
    );

-- Comment gốc của post
CREATE INDEX idx_comments_post_created
    ON comments(post_id, created_at DESC);

-- Comment gốc (không có parent)
CREATE INDEX idx_comments_post_root
    ON comments(post_id, created_at DESC)
    WHERE parent_id IS NULL;

-- Reply
CREATE INDEX idx_comments_parent
    ON comments(parent_id)
    WHERE parent_id IS NOT NULL;

-- Comment theo user
CREATE INDEX idx_comments_user
    ON comments(user_id);