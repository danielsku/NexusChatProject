-- Drop tables if they exist
DROP TABLE IF EXISTS sent_to;
DROP TABLE IF EXISTS message_delivery;
DROP TABLE IF EXISTS group_request_member;
DROP TABLE IF EXISTS group_request;
DROP TABLE IF EXISTS friend_request;
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS chat;
DROP TABLE IF EXISTS user_account;

SET FOREIGN_KEY_CHECKS=0;

-- =========================
-- BASE TABLES
-- =========================

CREATE TABLE `message` (
  `message_id` VARCHAR(36) NOT NULL,
  `chat_id` VARCHAR(36) NOT NULL,
  `sender_id` VARCHAR(36) NOT NULL,
  `content` VARCHAR(2000) DEFAULT NULL,
  `sent_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Fully normalized message_delivery
CREATE TABLE `message_delivery` (
  `receiver_id` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `friend_request` (
  `request_id` VARCHAR(36) NOT NULL,
  `sender_id` VARCHAR(36) NOT NULL,
  `receiver_id` VARCHAR(36) NOT NULL,
  `username` VARCHAR(36) NOT NULL,
  `stat` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `group_request` (
  `request_id` VARCHAR(36) NOT NULL,
  `sender_id` VARCHAR(36) NOT NULL,
  `chat_name` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- DEPENDENT TABLES
-- =========================

CREATE TABLE `group_request_member` (
  `request_id` VARCHAR(36) NOT NULL,
  `receiver_id` VARCHAR(36) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `stat` ENUM('PENDING','ACCEPTED','DECLINED') DEFAULT 'PENDING',
  PRIMARY KEY (`request_id`,`receiver_id`),
  FOREIGN KEY (`request_id`) REFERENCES `group_request`(`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `sent_to` (
  `message_id` VARCHAR(36) NOT NULL,
  `receiver_id` VARCHAR(36) NOT NULL,
    `stat` ENUM('PENDING','DELIVERED') DEFAULT 'PENDING',
  PRIMARY KEY (`message_id`,`receiver_id`),
  FOREIGN KEY (`message_id`)
    REFERENCES `message`(`message_id`),
  FOREIGN KEY (`receiver_id`)
    REFERENCES `message_delivery`(`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS=1;