-- 1. BẢNG USERS (Auth Domain)
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       email VARCHAR(255), -- NULL để hỗ trợ đăng ký bằng Phone
                       phone VARCHAR(20),  -- NULL để hỗ trợ đăng ký bằng Email
                       password_hash VARCHAR(255) NOT NULL,

                       status VARCHAR(20) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'USER',

                       email_verified BOOLEAN NOT NULL DEFAULT FALSE,
                       phone_verified BOOLEAN NOT NULL DEFAULT FALSE,

                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_email UNIQUE (email),
                       CONSTRAINT uk_users_phone UNIQUE (phone),
                       CONSTRAINT chk_users_role CHECK (role IN ('USER', 'MODERATOR', 'ADMIN'))
);

-- 2. BẢNG USER_PROFILES (Profile Domain)
-- Khóa chính user_id trùng khớp với id của bảng users
CREATE TABLE user_profiles (
                               user_id UUID PRIMARY KEY,

                               full_name VARCHAR(100),
                               avatar_file_id UUID,
                               cover_file_id UUID,
                               bio VARCHAR(500),
                               website VARCHAR(255),
                               location VARCHAR(255),
                               birth_date DATE,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. BẢNG USER_SETTINGS (Setting Domain)
CREATE TABLE user_settings (
                               user_id UUID PRIMARY KEY,

                               private_account BOOLEAN NOT NULL DEFAULT FALSE,
                               allow_tagging BOOLEAN NOT NULL DEFAULT TRUE,
                               language VARCHAR(10) NOT NULL DEFAULT 'vi',
                               theme VARCHAR(20) NOT NULL DEFAULT 'SYSTEM',

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);