-- 1. 创建数据库（首次运行时执行）
CREATE DATABASE IF NOT EXISTS lottery_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
USE lottery_db;

-- 2. 创建用户表（存储注册用户信息）
CREATE TABLE IF NOT EXISTS `user` (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  balance DECIMAL(10,2) DEFAULT 0.00,
  phone VARCHAR(20)
);

-- 3. 创建彩票表（存储用户购彩记录）
CREATE TABLE IF NOT EXISTS lottery_ticket (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  numbers VARCHAR(100) NOT NULL,
  bet_count INT DEFAULT 1,
  FOREIGN KEY (user_id) REFERENCES `user`(id)
);

-- 4. 初始化测试用户（如管理员账号）
INSERT INTO `user` (username, `password`, balance, phone)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', 1000.00, '13800138000');
