CREATE TABLE t_users (
    f_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    f_keycloak_user_id  VARCHAR(36) NOT NULL,
    f_username          VARCHAR(100) NOT NULL,
    f_email             VARCHAR(255),
    f_phone             VARCHAR(20),
    f_status            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    f_last_active_at    TIMESTAMP NULL,
    f_created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    f_updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);