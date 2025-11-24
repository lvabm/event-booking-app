-- SQL Script để fix password cho tất cả users
-- Password: password123
-- Chạy script này trong MySQL để update password hash

-- Hash BCrypt hợp lệ cho password "password123"
-- Hash này được generate bằng: BCrypt.hashpw("password123", BCrypt.gensalt())
-- Để generate hash mới, chạy PasswordHashGenerator.java

-- Update tất cả users với hash hợp lệ
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE email = 'user1@example.com';
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE email = 'user2@example.com';
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE email = 'user3@example.com';
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE email = 'user4@example.com';
UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' WHERE email = 'admin@example.com';

-- Hoặc update tất cả cùng lúc:
-- UPDATE users SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy';

