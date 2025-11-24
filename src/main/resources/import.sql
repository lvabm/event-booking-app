-- ================================
-- IMPORT SAMPLE DATA FOR Event Booking App
-- MySQL-compatible, place file at src/main/resources/import.sql (UTF-8 no BOM)
-- Tables (mapping entity -> table):
--   User       -> users
--   Event      -> events
--   Booking    -> bookings
--   Payment    -> payments
--   Reminder   -> reminders
-- Enums used (as strings):
--   role (users): USER, ADMIN
--   booking.status: PENDING, PAID, CANCELLED
--   payment.status: PAID, FAILED
-- ================================


-- ================================
-- USERS (4 USER, 1 ADMIN)
-- Table: `users`
-- Columns: id, full_name, email, password, avatar, role, created_at
-- ================================
-- Password cho tất cả users: password123
-- QUAN TRỌNG: Hash BCrypt cần được generate mới cho password "password123"
-- Chạy: mvn compile exec:java -Dexec.mainClass="com.eventbooking.util.PasswordHashGenerator"
-- Sau đó copy hash và update vào đây hoặc chạy SQL UPDATE trực tiếp trong database
-- 
-- Hash tạm thời (CẦN THAY THẾ bằng hash hợp lệ):
-- Để test, hãy chạy SQL UPDATE trong database với hash mới được generate
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `avatar`, `role`, `created_at`) VALUES (1, 'Nguyen Van A', 'user1@example.com', '$2a$10$0Om.lUnjY7p2EP7O9GbtPu7Jh5ZW8MgCSFRTdBrSMXv0hSoL212JC', NULL, 'USER', '2025-01-01 10:00:00');
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `avatar`, `role`, `created_at`) VALUES (2, 'Tran Thi B', 'user2@example.com', '$2a$10$0Om.lUnjY7p2EP7O9GbtPu7Jh5ZW8MgCSFRTdBrSMXv0hSoL212JC', NULL, 'USER', '2025-01-02 11:00:00');
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `avatar`, `role`, `created_at`) VALUES (3, 'Le Van C', 'user3@example.com', '$2a$10$0Om.lUnjY7p2EP7O9GbtPu7Jh5ZW8MgCSFRTdBrSMXv0hSoL212JC', NULL, 'USER', '2025-01-03 12:00:00');
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `avatar`, `role`, `created_at`) VALUES (4, 'Pham Thi D', 'user4@example.com', '$2a$10$0Om.lUnjY7p2EP7O9GbtPu7Jh5ZW8MgCSFRTdBrSMXv0hSoL212JC', NULL, 'USER', '2025-01-04 13:00:00');
INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `avatar`, `role`, `created_at`) VALUES (5, 'Lê Văn An', 'admin@example.com', '$2a$10$0Om.lUnjY7p2EP7O9GbtPu7Jh5ZW8MgCSFRTdBrSMXv0hSoL212JC', NULL, 'ADMIN', '2025-01-05 14:00:00');


-- ================================
-- REMINDERS (1 reminder per user)
-- Table: `reminders`
-- Columns: id, user_id, event_reminder (TINYINT), created_at
-- ================================
INSERT INTO `reminders` (`id`, `user_id`, `event_reminder`, `created_at`) VALUES (1, 1, 1, '2025-01-01 10:00:00');
INSERT INTO `reminders` (`id`, `user_id`, `event_reminder`, `created_at`) VALUES (2, 2, 1, '2025-01-02 11:00:00');
INSERT INTO `reminders` (`id`, `user_id`, `event_reminder`, `created_at`) VALUES (3, 3, 0, '2025-01-03 12:00:00');
INSERT INTO `reminders` (`id`, `user_id`, `event_reminder`, `created_at`) VALUES (4, 4, 1, '2025-01-04 13:00:00');
INSERT INTO `reminders` (`id`, `user_id`, `event_reminder`, `created_at`) VALUES (5, 5, 1, '2025-01-05 14:00:00');


-- ================================
-- EVENTS (5 sample events)
-- Table: `events`
-- Columns: id, title, date_time (DATETIME), location, latitude, longitude, price, description, image_url, created_at, updated_at
-- ================================
INSERT INTO `events` (`id`, `title`, `date_time`, `location`, `latitude`, `longitude`, `price`, `description`, `image_url`, `created_at`, `updated_at`) VALUES (1, 'Music Festival', '2025-06-12 19:00:00', 'Ho Chi Minh City', 10.77688900, 106.70089700, 49.99, 'A summer music festival.', 'https://cdn.example.com/events/music.jpg', '2025-02-01 09:00:00', '2025-02-01 09:00:00');
INSERT INTO `events` (`id`, `title`, `date_time`, `location`, `latitude`, `longitude`, `price`, `description`, `image_url`, `created_at`, `updated_at`) VALUES (2, 'Tech Conference', '2025-07-20 09:00:00', 'Hanoi', 21.02851100, 105.80481700, 120.00, 'Technology and innovation event.', 'https://cdn.example.com/events/tech.jpg', '2025-02-02 09:00:00', '2025-02-02 09:00:00');
INSERT INTO `events` (`id`, `title`, `date_time`, `location`, `latitude`, `longitude`, `price`, `description`, `image_url`, `created_at`, `updated_at`) VALUES (3, 'Art Exhibition', '2025-05-12 10:00:00', 'Da Nang Art Center', 16.05440700, 108.20216400, 25.00, 'Modern art display.', 'https://cdn.example.com/events/art.jpg', '2025-02-03 09:00:00', '2025-02-03 09:00:00');
INSERT INTO `events` (`id`, `title`, `date_time`, `location`, `latitude`, `longitude`, `price`, `description`, `image_url`, `created_at`, `updated_at`) VALUES (4, 'Football Match', '2025-08-05 17:30:00', 'My Dinh Stadium', 21.02851100, 105.80481700, 35.00, 'Friendly football match.', 'https://cdn.example.com/events/football.jpg', '2025-02-04 09:00:00', '2025-02-04 09:00:00');
INSERT INTO `events` (`id`, `title`, `date_time`, `location`, `latitude`, `longitude`, `price`, `description`, `image_url`, `created_at`, `updated_at`) VALUES (5, 'Startup Pitching', '2025-09-10 14:00:00', 'HCM Innovation Hub', 10.77688900, 106.70089700, 0.00, 'Startup founders pitching ideas.', 'https://cdn.example.com/events/startup.jpg', '2025-02-05 09:00:00', '2025-02-05 09:00:00');


-- ================================
-- BOOKINGS (various statuses)
-- Table: `bookings`
-- Columns: id, user_id, event_id, quantity, total_price, status, created_at
-- status values: PENDING, PAID, CANCELLED
-- ================================
INSERT INTO `bookings` (`id`, `user_id`, `event_id`, `quantity`, `total_price`, `status`, `created_at`) VALUES (1, 1, 1, 2, 99.98, 'PAID', '2025-03-01 10:00:00');
INSERT INTO `bookings` (`id`, `user_id`, `event_id`, `quantity`, `total_price`, `status`, `created_at`) VALUES (2, 2, 2, 1, 120.00, 'PENDING', '2025-03-02 11:00:00');
INSERT INTO `bookings` (`id`, `user_id`, `event_id`, `quantity`, `total_price`, `status`, `created_at`) VALUES (3, 3, 3, 3, 75.00, 'CANCELLED', '2025-03-03 12:00:00');
INSERT INTO `bookings` (`id`, `user_id`, `event_id`, `quantity`, `total_price`, `status`, `created_at`) VALUES (4, 4, 1, 1, 49.99, 'PAID', '2025-03-04 13:00:00');
INSERT INTO `bookings` (`id`, `user_id`, `event_id`, `quantity`, `total_price`, `status`, `created_at`) VALUES (5, 1, 4, 2, 70.00, 'PENDING', '2025-03-05 14:00:00');


-- ================================
-- PAYMENTS (booking_id UNIQUE)
-- Table: `payments`
-- Columns: id, booking_id, amount, status, created_at
-- status values: PAID, FAILED
-- ================================
INSERT INTO `payments` (`id`, `booking_id`, `amount`, `status`, `created_at`) VALUES (1, 1, 99.98, 'PAID', '2025-03-01 10:05:00');
INSERT INTO `payments` (`id`, `booking_id`, `amount`, `status`, `created_at`) VALUES (2, 4, 49.99, 'PAID', '2025-03-04 13:05:00');
INSERT INTO `payments` (`id`, `booking_id`, `amount`, `status`, `created_at`) VALUES (3, 3, 75.00, 'FAILED', '2025-03-03 12:05:00');
