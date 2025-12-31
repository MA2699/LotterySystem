-- 1. 关闭外键约束（避免删除数据时冲突）
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 清空业务表（保留user表的admin用户）
TRUNCATE TABLE lottery_ticket;
DELETE FROM `user` WHERE username != 'admin';

-- 3. 重置自增ID
ALTER TABLE lottery_ticket AUTO_INCREMENT = 1;
ALTER TABLE `user` AUTO_INCREMENT = 2;

-- 4. 恢复admin用户初始余额
UPDATE `user` SET balance = 1000.00 WHERE username = 'admin';

-- 5. 恢复外键约束
SET FOREIGN_KEY_CHECKS = 1;
