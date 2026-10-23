-- ============================================================
-- FILES
-- ============================================================

CREATE TABLE IF NOT EXISTS files (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                     original_name VARCHAR(255) NOT NULL,
                                     stored_name VARCHAR(255) NOT NULL UNIQUE,
                                     content_type VARCHAR(100) NOT NULL,
                                     size_bytes BIGINT NOT NULL,
                                     storage_path VARCHAR(1000) NOT NULL,
                                     uploaded_by UUID NOT NULL,

                                     created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_files_uploaded_by
                                         FOREIGN KEY (uploaded_by)
                                             REFERENCES users(id)
                                             ON DELETE CASCADE,

                                     CONSTRAINT chk_files_size
                                         CHECK (size_bytes > 0)
);

CREATE INDEX IF NOT EXISTS idx_files_uploaded_by
    ON files (uploaded_by);

CREATE INDEX IF NOT EXISTS idx_files_content_type
    ON files (content_type);