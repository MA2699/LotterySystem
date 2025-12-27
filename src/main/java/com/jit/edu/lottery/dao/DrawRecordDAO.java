package com.jit.edu.lottery.dao;

import com.jit.edu.lottery.model.DrawRecord;
import com.jit.edu.lottery.utils.DatabaseUtil;
import java.sql.*;
import java.util.*;

/**
 * 开奖记录数据访问对象
 * 负责draw_record表的所有数据库操作
 */
public class DrawRecordDAO {

    /**
     * 保存开奖记录到数据库
     * @param drawRecord 开奖记录对象
     * @return 保存成功返回记录的ID，失败返回-1
     */
    public int saveDrawRecord(DrawRecord drawRecord) {
        String sql = "INSERT INTO draw_record (draw_time, winning_numbers, draw_status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, new Timestamp(drawRecord.getDrawTime().getTime()));

            StringBuilder numbersStr = new StringBuilder();
            if (drawRecord.getWinningNumbers() != null && !drawRecord.getWinningNumbers().isEmpty()) {
                for (Integer num : drawRecord.getWinningNumbers()) {
                    if (numbersStr.length() > 0) numbersStr.append(",");
                    numbersStr.append(num);
                }
            }
            pstmt.setString(2, numbersStr.toString());
            pstmt.setString(3, drawRecord.getDrawStatus() != null ? drawRecord.getDrawStatus() : "未开奖");

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("保存开奖记录成功，ID: " + id);
                return id;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取最近的开奖记录
     * @param limit 获取的记录数量
     * @return 开奖记录列表，按时间倒序排列
     */
    public List<DrawRecord> getRecentDrawRecords(int limit) {
        List<DrawRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM draw_record ORDER BY draw_time DESC LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DrawRecord record = extractDrawRecordFromResultSet(rs);
                records.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    /**
     * 获取最新一期开奖记录
     * @return 最新的开奖记录对象，没有则返回null
     */
    public DrawRecord getLatestDraw() {
        String sql = "SELECT * FROM draw_record ORDER BY draw_time DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractDrawRecordFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取开奖总期数
     * @return 开奖记录的总数量
     */
    public int getDrawCount() {
        String sql = "SELECT COUNT(*) as count FROM draw_record";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 添加开奖记录（调用saveDrawRecord方法）
     * @param drawRecord 开奖记录对象
     */
    public void addDrawRecord(DrawRecord drawRecord) {
        saveDrawRecord(drawRecord);
    }

    /**
     * 从数据库结果集中提取开奖记录对象
     * @param rs 数据库查询结果集
     * @return 开奖记录对象
     * @throws SQLException 数据库异常
     */
    private DrawRecord extractDrawRecordFromResultSet(ResultSet rs) throws SQLException {
        DrawRecord record = new DrawRecord();
        record.setId(rs.getInt("id"));
        record.setDrawTime(new java.util.Date(rs.getTimestamp("draw_time").getTime()));

        String numbersStr = rs.getString("winning_numbers");
        record.setNumbersFromString(numbersStr);

        record.setDrawStatus(rs.getString("draw_status"));
        return record;
    }
}