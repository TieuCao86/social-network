-- 1. TABLE DEFINITION
CREATE TABLE IF NOT EXISTS friendships (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    requester_id UUID NOT NULL,
    addressee_id UUID NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_friendships_requester
                               FOREIGN KEY (requester_id)
    REFERENCES users (id)
                           ON DELETE CASCADE,

    CONSTRAINT fk_friendships_addressee
    FOREIGN KEY (addressee_id)
    REFERENCES users (id)
                           ON DELETE CASCADE,

    CONSTRAINT chk_friendships_self
    CHECK (requester_id <> addressee_id),

    CONSTRAINT chk_friendships_status
    CHECK (status IN ('PENDING', 'ACCEPTED', 'BLOCKED'))
    );

-- 2. UNIQUE INDEX (2-Way Relationship Constraint)
CREATE UNIQUE INDEX IF NOT EXISTS uq_friendships_unordered_pair
    ON friendships (
    LEAST(requester_id, addressee_id),
    GREATEST(requester_id, addressee_id)
    );

-- 3. PARTIAL INDEXES FOR QUERY OPTIMIZATION
-- Optimized for Accepted Friends List with pagination (ORDER BY updated_at DESC)
CREATE INDEX IF NOT EXISTS idx_friendships_requester_accepted
    ON friendships (requester_id, updated_at DESC)
    WHERE status = 'ACCEPTED';

CREATE INDEX IF NOT EXISTS idx_friendships_addressee_accepted
    ON friendships (addressee_id, updated_at DESC)
    WHERE status = 'ACCEPTED';

-- Optimized for Incoming Pending Requests (ORDER BY created_at DESC)
CREATE INDEX IF NOT EXISTS idx_friendships_pending_incoming
    ON friendships (addressee_id, created_at DESC)
    WHERE status = 'PENDING';