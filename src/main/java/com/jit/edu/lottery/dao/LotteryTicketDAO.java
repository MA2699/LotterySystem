package com.jit.edu.lottery.dao;

import com.jit.edu.lottery.model.LotteryTicket;
import com.jit.edu.lottery.utils.DatabaseUtil;
import java.sql.*;
import java.util.*;

/**
 * 彩票数据访问对象
 * 负责lottery_ticket表的所有数据库操作
 */
public class LotteryTicketDAO {

    /**
     * 创建彩票表（如果不存在）
     * 定义彩票表的结构和约束
     */
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS lottery_ticket (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT NOT NULL," +
                "numbers TEXT NOT NULL," +
                "bet_count INT DEFAULT 1," +
                "amount DECIMAL(10,2) NOT NULL," +
                "purchase_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "draw_id INT NULL," +  // 允许为NULL
                "ticket_status VARCHAR(20) DEFAULT '已购买'," +
                "FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据开奖期号获取彩票
     * @param drawId 开奖期号
     * @return 该期所有彩票列表
     */
    public List<LotteryTicket> getTicketsByDrawId(int drawId) {
        List<LotteryTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM lottery_ticket WHERE draw_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, drawId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LotteryTicket ticket = extractTicketFromResultSet(rs);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * 保存彩票记录到数据库
     * @param ticket 彩票对象
     * @return 保存是否成功
     */
    public boolean saveTicket(LotteryTicket ticket) {
        String sql = "INSERT INTO lottery_ticket (user_id, numbers, bet_count, amount, draw_id, ticket_status) VALUES (?, ?, ?, ?, ?, ?)";

        System.out.println("正在保存彩票记录...");
        System.out.println("用户ID: " + ticket.getUserId());
        System.out.println("号码: " + ticket.getNumbersString());
        System.out.println("倍数: " + ticket.getBetCount());
        System.out.println("金额: " + ticket.getAmount());
        System.out.println("开奖ID: " + ticket.getDrawId());

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, ticket.getUserId());
            pstmt.setString(2, ticket.getNumbersString());
            pstmt.setInt(3, ticket.getBetCount());
            pstmt.setDouble(4, ticket.getAmount());

            if (ticket.getDrawId() > 0) {
                pstmt.setInt(5, ticket.getDrawId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            pstmt.setString(6, ticket.getTicketStatus() != null ? ticket.getTicketStatus() : "已购买");

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int ticketId = generatedKeys.getInt(1);
                        ticket.setId(ticketId);
                        System.out.println("✅ 保存彩票记录成功，ID: " + ticketId);
                    }
                }
                return true;
            } else {
                System.out.println("❌ 保存彩票记录失败，没有受影响的行");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ 保存彩票记录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取指定用户的所有彩票
     * @param userId 用户ID
     * @return 用户彩票列表，按购买时间倒序排列
     */
    public List<LotteryTicket> getTicketsByUserId(int userId) {
        List<LotteryTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM lottery_ticket WHERE user_id = ? ORDER BY purchase_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LotteryTicket ticket = extractTicketFromResultSet(rs);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * 获取未开奖的彩票（draw_id为NULL）
     * @return 未开奖彩票列表，按购买时间正序排列
     */
    public List<LotteryTicket> getUnprocessedTickets() {
        List<LotteryTicket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM lottery_ticket WHERE draw_id IS NULL ORDER BY purchase_time ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LotteryTicket ticket = extractTicketFromResultSet(rs);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    /**
     * 更新彩票的开奖ID
     * @param ticketId 彩票ID
     * @param drawId 开奖期号
     * @return 更新是否成功
     */
    public boolean updateTicketDrawId(int ticketId, int drawId) {
        String sql = "UPDATE lottery_ticket SET draw_id = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, drawId);
            pstmt.setInt(2, ticketId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从ResultSet提取彩票对象
     * @param rs 数据库查询结果集
     * @return 彩票对象
     * @throws SQLException 数据库异常
     */
    private LotteryTicket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        LotteryTicket ticket = new LotteryTicket();
        ticket.setId(rs.getInt("id"));
        ticket.setUserId(rs.getInt("user_id"));

        String numbersStr = rs.getString("numbers");
        ticket.setNumbersFromString(numbersStr);

        ticket.setBetCount(rs.getInt("bet_count"));
        ticket.setAmount(rs.getDouble("amount"));
        ticket.setPurchaseTime(new java.util.Date(rs.getTimestamp("purchase_time").getTime()));

        int drawId = rs.getInt("draw_id");
        if (!rs.wasNull()) {
            ticket.setDrawId(drawId);
        } else {
            ticket.setDrawId(0);
        }

        ticket.setTicketStatus(rs.getString("ticket_status"));

        return ticket;
    }
}