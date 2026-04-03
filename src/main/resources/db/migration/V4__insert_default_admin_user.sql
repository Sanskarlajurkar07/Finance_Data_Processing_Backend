-- Insert default admin user for initial setup
-- Password: Admin@123 (BCrypt hashed with strength 12)
-- This should be changed immediately after first login in production
INSERT INTO users (username, email, password_hash, role, is_active, created_at, updated_at)
VALUES (
    'admin',
    'admin@fintech.com',
    '$2a$12$O8IVvxUM9H.Lv./7mSlAvukIWXnypCsw7lJ7cR1n9QmtdFgtb3PLm',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Add comment
COMMENT ON TABLE users IS 'Default admin user created. Password: Admin@123 - CHANGE IMMEDIATELY IN PRODUCTION';
