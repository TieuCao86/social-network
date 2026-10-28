CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               recipient_id UUID NOT NULL,
                               actor_id UUID NOT NULL,
                               type VARCHAR(30) NOT NULL,
                               reference_id UUID NOT NULL,
                               content VARCHAR(500) NOT NULL,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_notifications_recipient
                                   FOREIGN KEY (recipient_id) REFERENCES users(id),

                               CONSTRAINT fk_notifications_actor
                                   FOREIGN KEY (actor_id) REFERENCES users(id)
);

CREATE INDEX idx_notifications_recipient_created
    ON notifications(recipient_id, created_at DESC);

CREATE INDEX idx_notifications_recipient_read
    ON notifications(recipient_id, is_read);