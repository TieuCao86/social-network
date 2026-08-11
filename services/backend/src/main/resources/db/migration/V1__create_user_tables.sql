CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       phone VARCHAR(20),
                       password_hash VARCHAR(255) NOT NULL,

                       status VARCHAR(20) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',

                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       phone_verified BOOLEAN NOT NULL DEFAULT FALSE,

                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,

                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT uk_users_phone UNIQUE (phone),

                       CONSTRAINT chk_users_role
                           CHECK (role IN ('USER', 'MODERATOR', 'ADMIN'))
);


CREATE TABLE user_profiles (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,

                               full_name VARCHAR(100),
                               avatar_file_id UUID,
                               cover_file_id UUID,
                               bio VARCHAR(500),
                               website VARCHAR(255),
                               location VARCHAR(255),
                               birth_date DATE,

                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP NOT NULL,

                               CONSTRAINT uk_user_profiles_user_id UNIQUE (user_id),

                               CONSTRAINT fk_user_profiles_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
);


CREATE TABLE user_settings (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,

                               private_account BOOLEAN NOT NULL DEFAULT FALSE,
                               allow_tagging BOOLEAN NOT NULL DEFAULT TRUE,
                               language VARCHAR(10) NOT NULL DEFAULT 'vi',
                               theme VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',

                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP NOT NULL,

                               CONSTRAINT uk_user_settings_user_id UNIQUE (user_id),

                               CONSTRAINT fk_user_settings_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES users(id)
);