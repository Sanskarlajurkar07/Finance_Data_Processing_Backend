-- Insert default admin user for H2 database
-- Password: Admin@123 (BCrypt hashed with strength 12)
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
