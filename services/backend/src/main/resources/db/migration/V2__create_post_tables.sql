-- 1. BẢNG POSTS
CREATE TABLE posts (
                       id UUID PRIMARY KEY,
                       author_id UUID NOT NULL, -- Tham chiếu mềm tới users(id)
                       content TEXT,
                       visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

                       comment_count INT NOT NULL DEFAULT 0,
                       like_count INT NOT NULL DEFAULT 0,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT chk_posts_visibility CHECK (visibility IN ('PUBLIC', 'FRIENDS', 'PRIVATE')),
                       CONSTRAINT chk_posts_status CHECK (status IN ('ACTIVE', 'ARCHIVED', 'DELETED'))
);

-- Index tối ưu truy vấn Newsfeed/Trang cá nhân
CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);


-- 2. BẢNG POST_MEDIA
CREATE TABLE post_media (
                            id UUID PRIMARY KEY,
                            post_id UUID NOT NULL,
                            type VARCHAR(20) NOT NULL, -- IMAGE, VIDEO
                            file_id UUID NOT NULL,     -- Tham chiếu tới Media Service / File ID
                            sort_order INT NOT NULL DEFAULT 0,

                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_post_media_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                            CONSTRAINT chk_post_media_type CHECK (type IN ('IMAGE', 'VIDEO'))
);

-- Index tối ưu khi lấy danh sách media của 1 bài viết
CREATE INDEX idx_post_media_post_sort ON post_media(post_id, sort_order);