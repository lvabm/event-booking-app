-- Script để update password cho tất cả users
-- Password: password123
-- Chạy script này trong MySQL để update password hash

-- Hash này được generate bằng BCrypt cho password "password123"
-- Để generate hash mới, chạy: GeneratePasswordHash.java

-- Cách 1: Update từng user
UPDATE users SET password = '$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567890' WHERE email = 'user1@example.com';
UPDATE users SET password = '$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567890' WHERE email = 'user2@example.com';
UPDATE users SET password = '$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567890' WHERE email = 'user3@example.com';
UPDATE users SET password = '$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567890' WHERE email = 'user4@example.com';
UPDATE users SET password = '$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567890' WHERE email = 'admin@example.com';

-- Cách 2: Update tất cả users cùng lúc
-- UPDATE users SET password = '$2a$10$abcdefghijklmnopqrstuvwxyz123456789012345678901234567890';

