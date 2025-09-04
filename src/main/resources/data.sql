-- 사용자 테이블(예시)
CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       provider VARCHAR(20) NOT NULL,         -- 'KAKAO'
                       provider_id VARCHAR(100) NOT NULL,     -- kakao id
                       email VARCHAR(255) UNIQUE,
                       name VARCHAR(100),
                       profile_image_url VARCHAR(500),
                       role VARCHAR(30) NOT NULL,             -- STUDENT/ADMIN
                       is_notification_enabled TINYINT(1) DEFAULT 0,
                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                       updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       UNIQUE KEY uk_provider_provider_id (provider, provider_id)
);

-- 필요 시 인덱스 예시
CREATE INDEX idx_users_email ON users(email);