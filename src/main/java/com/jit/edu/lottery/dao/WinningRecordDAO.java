package com.jit.edu.lottery.dao;

import com.jit.edu.lottery.model.WinningRecord;
import com.jit.edu.lottery.utils.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * WinningRecordDAO（中奖记录数据访问对象）类
 * 负责处理与中奖记录表相关的所有数据库操作
 */
public class WinningRecordDAO {

    /**
     * 创建中奖记录表的方法
     * 在数据库中创建一个名为'winning_record'的表（如果不存在）
     * 包含中奖记录的完整信息，并设置外键约束关联其他相关表
     */
    public void createTable() {
        // 定义创建中奖记录表的SQL语句
        String sql = "CREATE TABLE IF NOT EXISTS winning_record (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +                      // 主键，自增ID
                "user_id INT NOT NULL," +                                   // 中奖用户ID，外键关联user表
                "draw_id INT NOT NULL," +                                   // 开奖期号ID，外键关联draw_record表
                "ticket_id INT NOT NULL," +                                 // 彩票ID，外键关联lottery_ticket表
                "prize_level VARCHAR(20) NOT NULL," +                       // 中奖等级（特等奖、一等奖等）
                "prize_amount DECIMAL(10,2) NOT NULL," +                    // 奖金金额，10位数字，2位小数
                "match_count INT NOT NULL," +                               // 匹配号码数量
                "notify_time TIMESTAMP NULL," +                             // 通知时间，可为空
                "is_notified BOOLEAN DEFAULT FALSE," +                      // 是否已通知，默认未通知
                "FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE," +    // 用户表外键，级联删除
                "FOREIGN KEY (draw_id) REFERENCES draw_record(id) ON DELETE CASCADE," + // 开奖记录表外键，级联删除
                "FOREIGN KEY (ticket_id) REFERENCES lottery_ticket(id) ON DELETE CASCADE" + // 彩票表外键，级联删除
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";                  // 使用InnoDB引擎和utf8mb4字符集

        // 使用try-with-resources确保连接和语句正确关闭
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // 执行建表SQL语句
            stmt.execute(sql);
        } catch (SQLException e) {
            // 打印异常堆栈信息，便于调试
            e.printStackTrace();
        }
    }

    /**
     * 保存中奖记录到数据库
     *
     * @param record 包含中奖记录信息的WinningRecord对象
     */
    public void saveWinningRecord(WinningRecord record) {
        // 插入中奖记录的SQL语句
        String sql = "INSERT INTO winning_record (user_id, draw_id, ticket_id, prize_level, " +
                "prize_amount, match_count, notify_time, is_notified) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置SQL语句的参数
            pstmt.setInt(1, record.getUserId());          // 用户ID
            pstmt.setInt(2, record.getDrawId());          // 开奖期号ID
            pstmt.setInt(3, record.getTicketId());        // 彩票ID
            pstmt.setString(4, record.getPrizeLevel());   // 中奖等级
            pstmt.setDouble(5, record.getPrizeAmount());  // 奖金金额
            pstmt.setInt(6, record.getMatchCount());      // 匹配号码数量

            // 处理通知时间（可能为null）
            if (record.getNotifyTime() != null) {
                // 将java.util.Date转换为java.sql.Timestamp
                pstmt.setTimestamp(7, new Timestamp(record.getNotifyTime().getTime()));
            } else {
                pstmt.setTimestamp(7, null);  // 通知时间为空
            }

            pstmt.setBoolean(8, record.isNotified());  // 是否已通知
            pstmt.executeUpdate();  // 执行插入操作

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定用户未通知的中奖记录列表
     * 用于在用户登录时显示未读的中奖通知
     *
     * @param userId 要查询的用户ID
     * @return 该用户所有未通知的中奖记录列表
     */
    public List<WinningRecord> getUnnotifiedWinsByUserId(int userId) {
        List<WinningRecord> records = new ArrayList<>();  // 创建结果列表
        // 查询未通知的中奖记录的SQL语句
        String sql = "SELECT * FROM winning_record WHERE user_id = ? AND is_notified = FALSE";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);  // 设置用户ID参数
            ResultSet rs = pstmt.executeQuery();  // 执行查询

            // 遍历查询结果集
            while (rs.next()) {
                // 从ResultSet中提取中奖记录数据
                WinningRecord record = extractWinningRecordFromResultSet(rs);
                records.add(record);  // 添加到结果列表
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;  // 返回中奖记录列表
    }

    /**
     * 将指定的中奖记录标记为已通知
     * 并设置通知时间为当前时间
     *
     * @param recordId 要标记的中奖记录ID
     */
    public void markAsNotified(int recordId) {
        // 更新中奖记录为已通知的SQL语句
        String sql = "UPDATE winning_record SET is_notified = TRUE, notify_time = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recordId);  // 设置记录ID参数
            pstmt.executeUpdate();  // 执行更新操作

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定开奖期的所有中奖记录
     * 用于显示某期开奖的中奖情况统计
     *
     * @param drawId 要查询的开奖期号ID
     * @return 该期开奖的所有中奖记录列表
     */
    public List<WinningRecord> getWinningRecordsByDrawId(int drawId) {
        List<WinningRecord> records = new ArrayList<>();  // 创建结果列表
        // 按开奖期号查询中奖记录的SQL语句
        String sql = "SELECT * FROM winning_record WHERE draw_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, drawId);  // 设置开奖期号参数
            ResultSet rs = pstmt.executeQuery();  // 执行查询

            // 遍历查询结果集
            while (rs.next()) {
                // 从ResultSet中提取中奖记录数据
                WinningRecord record = extractWinningRecordFromResultSet(rs);
                records.add(record);  // 添加到结果列表
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;  // 返回中奖记录列表
    }

    /**
     * 从ResultSet中提取中奖记录数据并创建WinningRecord对象
     * 这是一个私有辅助方法，用于封装从结果集到对象的转换逻辑
     *
     * @param rs 包含中奖记录数据的ResultSet
     * @return 创建好的WinningRecord对象
     * @throws SQLException 如果数据库操作出现异常
     */
    private WinningRecord extractWinningRecordFromResultSet(ResultSet rs) throws SQLException {
        WinningRecord record = new WinningRecord();  // 创建新的WinningRecord对象

        // 从ResultSet中提取各个字段的值，并设置到WinningRecord对象中
        record.setId(rs.getInt("id"));                     // 中奖记录ID
        record.setUserId(rs.getInt("user_id"));            // 用户ID
        record.setDrawId(rs.getInt("draw_id"));            // 开奖期号ID
        record.setTicketId(rs.getInt("ticket_id"));        // 彩票ID
        record.setPrizeLevel(rs.getString("prize_level")); // 中奖等级
        record.setPrizeAmount(rs.getDouble("prize_amount")); // 奖金金额
        record.setMatchCount(rs.getInt("match_count"));    // 匹配号码数量
        record.setNotified(rs.getBoolean("is_notified"));  // 是否已通知

        // 处理通知时间字段（可能为null）
        Timestamp timestamp = rs.getTimestamp("notify_time");
        if (timestamp != null) {
            // 将java.sql.Timestamp转换为java.util.Date
            record.setNotifyTime(new java.util.Date(timestamp.getTime()));
        }

        return record;  // 返回填充好的WinningRecord对象
    }
}