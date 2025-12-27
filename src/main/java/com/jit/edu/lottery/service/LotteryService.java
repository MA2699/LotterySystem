package com.jit.edu.lottery.service;

import com.jit.edu.lottery.dao.*;
import com.jit.edu.lottery.model.*;
import com.jit.edu.lottery.utils.DatabaseUtil;
import javax.swing.*;
import java.util.*;
import java.sql.*;

/**
 * 彩票服务类
 * 处理彩票相关的核心业务逻辑，包括抽奖、开奖、中奖计算等
 */
public class LotteryService {
    // 数据访问对象
    private LotteryTicketDAO lotteryTicketDAO;
    private UserDAO userDAO;
    private WinningRecordDAO winningRecordDAO;
    private DrawRecordDAO drawRecordDAO;

    // 抽奖动画相关变量
    private java.util.Timer rollingTimer;
    private volatile boolean isRolling = false;
    private List<Integer> currentRollingNumbers = new ArrayList<>();
    private JLabel[] numberLabels;
    private JTextArea resultArea;

    /**
     * 构造函数，初始化服务对象
     */
    public LotteryService() {
        this.lotteryTicketDAO = new LotteryTicketDAO();
        this.userDAO = new UserDAO();
        this.winningRecordDAO = new WinningRecordDAO();
        this.drawRecordDAO = new DrawRecordDAO();

        initDatabaseTables();
    }

    /**
     * 初始化数据库表
     */
    private void initDatabaseTables() {
        userDAO.createTable();
        lotteryTicketDAO.createTable();
        winningRecordDAO.createTable();
    }

    /**
     * 开始抽奖动画
     * @param labels 号码显示标签数组
     * @param area 结果显示文本区域
     */
    public void startDrawRolling(JLabel[] labels, JTextArea area) {
        if (isRolling) return;

        this.numberLabels = labels;
        this.resultArea = area;
        isRolling = true;

        resultArea.setText("🎰 抽奖进行中...\n点击停止按钮确定中奖号码\n");

        // 初始化滚动号码
        currentRollingNumbers.clear();
        Random rand = new Random();
        for (int i = 0; i < 7; i++) {
            currentRollingNumbers.add(rand.nextInt(36) + 1);
        }

        updateNumberLabels();

        // 创建计时器，每150ms更新一次号码显示
        rollingTimer = new java.util.Timer();
        rollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    Random rand = new Random();
                    for (int i = 0; i < 7; i++) {
                        currentRollingNumbers.set(i, rand.nextInt(36) + 1);
                    }
                    updateNumberLabels();
                });
            }
        }, 0, 150);
    }

    /**
     * 更新号码显示标签
     */
    private void updateNumberLabels() {
        if (numberLabels != null && numberLabels.length >= 7) {
            for (int i = 0; i < 7; i++) {
                numberLabels[i].setText(String.format("%02d", currentRollingNumbers.get(i)));
                numberLabels[i].setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
                numberLabels[i].setForeground(java.awt.Color.RED);
                numberLabels[i].setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLUE, 2));
            }
        }
    }

    /**
     * 停止抽奖，确定最终中奖号码
     * @return 中奖号码列表
     */
    public List<Integer> stopDrawRolling() {
        if (!isRolling || rollingTimer == null) return null;

        // 停止计时器
        rollingTimer.cancel();
        rollingTimer = null;
        isRolling = false;

        // 生成最终中奖号码
        List<Integer> winningNumbers = generateWinningNumbers();
        currentRollingNumbers = new ArrayList<>(winningNumbers);
        updateNumberLabels();

        System.out.println("=== 开始开奖 ===");

        // 获取当前期号
        int currentDrawId = getCurrentDrawId();
        System.out.println("将要开奖的期号: " + currentDrawId);

        if (currentDrawId <= 0) {
            System.out.println("错误：无法获取有效期号");
            return null;
        }

        // 更新开奖记录
        boolean updateSuccess = updateDrawRecord(currentDrawId, winningNumbers, "normal");

        if (updateSuccess) {
            System.out.println("开奖记录更新成功，期号: " + currentDrawId);

            // 更新彩票状态
            updateTicketsStatus(currentDrawId);

            // 计算中奖结果
            calculateWinningResults(currentDrawId, winningNumbers);
        } else {
            System.out.println("开奖记录更新失败");
        }

        return winningNumbers;
    }

    /**
     * 立即开奖（不经过动画过程）
     * @param presetNumbers 预设号码
     * @param resultArea 结果显示区域
     * @return 中奖号码列表
     */
    public List<Integer> performImmediateDraw(List<Integer> presetNumbers, JTextArea resultArea) {
        System.out.println("=== 执行立即开奖 ===");
        System.out.println("预设号码: " + presetNumbers);

        // 验证号码数量
        if (presetNumbers == null || presetNumbers.size() != 7) {
            System.out.println("错误：预设号码必须为7个");
            return null;
        }

        // 保持原始顺序，不排序
        // Collections.sort(presetNumbers); // 已注释掉

        // 获取当前期号
        int currentDrawId = getCurrentDrawId();
        System.out.println("立即开奖的期号: " + currentDrawId);

        if (currentDrawId <= 0) {
            System.out.println("错误：无法获取有效期号");
            return null;
        }

        // 更新开奖记录
        boolean updateSuccess = updateDrawRecord(currentDrawId, presetNumbers, "immediate");

        if (updateSuccess) {
            System.out.println("立即开奖记录更新成功");

            // 更新彩票状态
            updateTicketsStatus(currentDrawId);

            // 计算中奖结果
            calculateWinningResults(currentDrawId, presetNumbers);

            // 更新结果显示
            if (resultArea != null) {
                resultArea.setText("🎯 立即开奖完成！\n");
                resultArea.append("📅 期号: " + currentDrawId + "\n");
                resultArea.append("🏆 中奖号码: " + presetNumbers + "\n");
                resultArea.append("🎪 开奖方式: 立即开奖（测试模式）\n\n");
                resultArea.append("✅ 中奖计算已完成，奖金已发放\n");
                resultArea.append("💡 用户登录时会收到中奖通知\n");
            }

            // 显示成功对话框
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "✅ 立即开奖完成！\n" +
                                "期号: " + currentDrawId + "\n" +
                                "中奖号码: " + presetNumbers + "\n" +
                                "\n已计算中奖结果并发放奖金",
                        "立即开奖成功",
                        JOptionPane.INFORMATION_MESSAGE);
            });

            return presetNumbers;
        } else {
            System.out.println("立即开奖失败");
            return null;
        }
    }

    /**
     * 更新开奖记录到数据库
     */
    private boolean updateDrawRecord(int drawId, List<Integer> winningNumbers, String drawType) {
        try {
            String sql = "UPDATE draw_record SET winning_numbers = ?, draw_status = '已开奖', draw_time = NOW() WHERE id = ?";

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // 将号码列表转为逗号分隔的字符串
                StringBuilder numbersStr = new StringBuilder();
                for (int i = 0; i < winningNumbers.size(); i++) {
                    if (i > 0) numbersStr.append(",");
                    numbersStr.append(winningNumbers.get(i));
                }

                pstmt.setString(1, numbersStr.toString());
                pstmt.setInt(2, drawId);

                int updated = pstmt.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            System.err.println("更新开奖记录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新本期所有彩票状态为"已开奖"
     */
    private void updateTicketsStatus(int drawId) {
        System.out.println("=== 开始更新彩票状态 ===");
        System.out.println("要更新的期号: " + drawId);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            // 先统计本期彩票数量
            String countSql = "SELECT COUNT(*) as count FROM lottery_ticket WHERE draw_id = ?";
            pstmt = conn.prepareStatement(countSql);
            pstmt.setInt(1, drawId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("数据库中有 " + count + " 张第 " + drawId + " 期的彩票");

                if (count == 0) {
                    System.out.println("警告：没有找到第 " + drawId + " 期的彩票！");
                    showAllTicketsForDebug(conn);
                    return;
                }
            }

            rs.close();
            pstmt.close();

            // 更新彩票状态
            String updateSql = "UPDATE lottery_ticket SET ticket_status = '已开奖' WHERE draw_id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setInt(1, drawId);
            int updatedRows = pstmt.executeUpdate();

            System.out.println("成功更新了 " + updatedRows + " 张彩票状态为'已开奖'");

        } catch (SQLException e) {
            System.err.println("更新彩票状态失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭数据库资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 调试方法：显示所有彩票信息
     */
    private void showAllTicketsForDebug(Connection conn) {
        try {
            System.out.println("=== 所有彩票信息（用于调试）===");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT id, user_id, draw_id, ticket_status, numbers FROM lottery_ticket ORDER BY id DESC"
            );

            System.out.println("ID\t用户ID\t期号\t状态\t\t号码");
            System.out.println("----------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                int userId = rs.getInt("user_id");
                int drawId = rs.getInt("draw_id");
                String status = rs.getString("ticket_status");
                String numbers = rs.getString("numbers");

                System.out.printf("%d\t%d\t%d\t%s\t%s%n",
                        id, userId, drawId,
                        (status == null ? "null" : status),
                        (numbers == null ? "null" : numbers));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("显示调试信息失败: " + e.getMessage());
        }
    }

    /**
     * 生成中奖号码（允许重复号码）
     */
    private List<Integer> generateWinningNumbers() {
        List<Integer> result = new ArrayList<>();
        Random rand = new Random();

        // 允许重复号码
        for (int i = 0; i < 7; i++) {
            result.add(rand.nextInt(36) + 1);
        }

        // 保持原始顺序，不排序
        return result;
    }

    /**
     * 保存开奖记录到数据库
     */
    private int saveDrawRecord(List<Integer> winningNumbers, String drawType) {
        DrawRecord drawRecord = new DrawRecord();
        drawRecord.setDrawTime(new java.util.Date());
        drawRecord.setWinningNumbers(winningNumbers);
        drawRecord.setDrawStatus("已开奖");
        drawRecord.setDrawType(drawType);
        return drawRecordDAO.saveDrawRecord(drawRecord);
    }

    /**
     * 计算中奖结果并发放奖金
     */
    private void calculateWinningResults(int drawId, List<Integer> winningNumbers) {
        System.out.println("=== 开始计算中奖结果 ===");
        System.out.println("期号: " + drawId);
        System.out.println("中奖号码: " + winningNumbers);

        // 获取本期所有彩票
        List<LotteryTicket> tickets = lotteryTicketDAO.getTicketsByDrawId(drawId);

        System.out.println("本期共有 " + tickets.size() + " 张彩票需要计算");

        // 构建结果显示字符串
        StringBuilder result = new StringBuilder();
        result.append("================== 第 ").append(drawId).append(" 期开奖结果 ==================\n");
        result.append("🏆 中奖号码: ").append(winningNumbers).append("\n\n");

        if (tickets.isEmpty()) {
            result.append("本期没有彩票售出\n");
            System.out.println("本期没有彩票售出");
        } else {
            result.append("📊 本期共售出 ").append(tickets.size()).append(" 张彩票\n\n");

            // 按奖项等级分类
            Map<String, List<WinningRecord>> prizeMap = new HashMap<>();
            prizeMap.put("特等奖", new ArrayList<>());
            prizeMap.put("一等奖", new ArrayList<>());
            prizeMap.put("二等奖", new ArrayList<>());
            prizeMap.put("三等奖", new ArrayList<>());

            // 遍历所有彩票计算中奖情况
            for (LotteryTicket ticket : tickets) {
                // 计算匹配号码数量（位置对应）
                int matchCount = calculateMatchCount(ticket.getNumbers(), winningNumbers);

                System.out.println("彩票ID " + ticket.getId() + " 匹配了 " + matchCount + " 个号码");

                // 确定奖项等级
                String prizeLevel = null;
                if (matchCount == 7) {
                    prizeLevel = "特等奖";
                } else if (matchCount == 6) {
                    prizeLevel = "一等奖";
                } else if (matchCount == 5) {
                    prizeLevel = "二等奖";
                } else if (matchCount == 4) {
                    prizeLevel = "三等奖";
                }

                if (prizeLevel != null) {
                    // 计算奖金金额
                    double prizeAmount = calculatePrizeAmount(prizeLevel, ticket.getBetCount());

                    // 创建中奖记录
                    WinningRecord winningRecord = new WinningRecord();
                    winningRecord.setUserId(ticket.getUserId());
                    winningRecord.setDrawId(drawId);
                    winningRecord.setTicketId(ticket.getId());
                    winningRecord.setPrizeLevel(prizeLevel);
                    winningRecord.setPrizeAmount(prizeAmount);
                    winningRecord.setMatchCount(matchCount);
                    winningRecord.setNotified(false);

                    // 保存中奖记录
                    winningRecordDAO.saveWinningRecord(winningRecord);

                    // 发放奖金到用户账户
                    userDAO.updateBalance(ticket.getUserId(), prizeAmount);

                    // 添加到分类结果
                    prizeMap.get(prizeLevel).add(winningRecord);

                    // 获取用户名
                    User user = userDAO.getUserById(ticket.getUserId());
                    String username = (user != null) ? user.getUsername() : "用户" + ticket.getUserId();

                    result.append("🎉 ").append(username)
                            .append(" 中了").append(prizeLevel)
                            .append("！投注倍数: ").append(ticket.getBetCount())
                            .append("，奖金: ¥").append(String.format("%.2f", prizeAmount))
                            .append("，匹配号码: ").append(matchCount).append("个\n");

                    System.out.println(username + " 中了 " + prizeLevel + "，奖金: ¥" + prizeAmount);
                }
            }

            // 统计各奖项中奖人数
            result.append("\n📈 中奖统计：\n");
            for (Map.Entry<String, List<WinningRecord>> entry : prizeMap.entrySet()) {
                result.append(entry.getKey()).append(": ").append(entry.getValue().size()).append(" 人\n");
            }
        }

        result.append("\n✅ 奖金已自动发放到中奖用户账户\n");
        result.append("💡 用户登录时会收到中奖通知\n");

        // 更新结果显示
        if (resultArea != null) {
            resultArea.setText(result.toString());
        }
    }

    /**
     * 计算匹配号码数量（位置对应匹配）
     */
    private int calculateMatchCount(List<Integer> ticketNumbers, List<Integer> winningNumbers) {
        if (ticketNumbers == null || winningNumbers == null) return 0;
        if (ticketNumbers.size() != 7 || winningNumbers.size() != 7) return 0;

        int count = 0;
        // 按位置对应比较
        for (int i = 0; i < 7; i++) {
            if (ticketNumbers.get(i).equals(winningNumbers.get(i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * 计算奖金金额
     */
    private double calculatePrizeAmount(String prizeLevel, int betCount) {
        double baseAmount;
        switch (prizeLevel) {
            case "特等奖":
                baseAmount = 5000000; // 500万
                break;
            case "一等奖":
                baseAmount = 500000; // 50万
                break;
            case "二等奖":
                baseAmount = 10000; // 1万
                break;
            case "三等奖":
                baseAmount = 1000; // 1千
                break;
            default:
                baseAmount = 0;
        }
        return baseAmount * betCount;
    }

    /**
     * 检查用户是否有未读中奖通知
     */
    public String checkWinningNotification(int userId) {
        List<WinningRecord> unnotifiedWins = winningRecordDAO.getUnnotifiedWinsByUserId(userId);

        if (unnotifiedWins.isEmpty()) {
            return null;
        }

        // 构建通知消息
        StringBuilder notification = new StringBuilder();
        notification.append("🎊 恭喜！您有以下中奖记录：\n\n");

        double totalAmount = 0;
        for (WinningRecord record : unnotifiedWins) {
            notification.append("第 ").append(record.getDrawId()).append(" 期：")
                    .append(record.getPrizeLevel())
                    .append(" ¥").append(String.format("%.2f", record.getPrizeAmount()))
                    .append("\n");

            totalAmount += record.getPrizeAmount();

            // 标记为已通知
            winningRecordDAO.markAsNotified(record.getId());
        }

        notification.append("\n💰 总计奖金: ¥").append(String.format("%.2f", totalAmount));
        notification.append("\n✅ 奖金已到账，请查收！");

        return notification.toString();
    }

    /**
     * 获取最近的开奖记录
     */
    public List<DrawRecord> getRecentDrawRecords(int limit) {
        return drawRecordDAO.getRecentDrawRecords(limit);
    }

    /**
     * 购买彩票
     */
    public boolean buyTicket(int userId, List<Integer> numbers, int betCount, double amount) {
        try {
            System.out.println("=== 开始购买彩票 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("号码: " + numbers);
            System.out.println("倍数: " + betCount);
            System.out.println("金额: " + amount);

            // 检查用户是否存在
            User user = userDAO.getUserById(userId);

            if (user == null) {
                System.out.println("❌ 用户不存在: " + userId);
                return false;
            }

            System.out.println("用户余额: " + user.getBalance() + ", 需要: " + amount);

            // 检查余额是否足够
            if (user.getBalance() < amount) {
                System.out.println("❌ 余额不足！用户余额: " + user.getBalance() + ", 需要: " + amount);
                return false;
            }

            // 扣款
            System.out.println("正在扣款...");
            boolean deductionSuccess = userDAO.updateBalance(userId, -amount);
            if (!deductionSuccess) {
                System.out.println("❌ 扣款失败");
                return false;
            }

            System.out.println("✅ 扣款成功！扣除金额: " + amount);

            // 获取当前期号
            int currentDrawId = getCurrentDrawId();
            System.out.println("当前期号: " + currentDrawId);

            // 创建彩票记录
            LotteryTicket ticket = new LotteryTicket();
            ticket.setUserId(userId);
            ticket.setNumbers(numbers);
            ticket.setBetCount(betCount);
            ticket.setAmount(amount);
            ticket.setPurchaseTime(new java.util.Date());
            ticket.setTicketStatus("已购买");
            ticket.setDrawId(currentDrawId);

            // 保存彩票记录
            System.out.println("正在保存彩票记录...");
            boolean saveSuccess = lotteryTicketDAO.saveTicket(ticket);

            if (!saveSuccess) {
                System.out.println("❌ 保存彩票记录失败，退回金额");
                userDAO.updateBalance(userId, amount);
                return false;
            }

            System.out.println("✅ 购买成功！");
            System.out.println("用户ID: " + userId);
            System.out.println("期号: " + currentDrawId);
            System.out.println("号码: " + numbers);
            System.out.println("金额: " + amount);

            return true;

        } catch (Exception e) {
            System.err.println("❌ 购买彩票失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取当前期号（未开奖的期号）
     */
    private int getCurrentDrawId() {
        try {
            // 查找未开奖的期号
            DrawRecord unfinishedDraw = getUnfinishedDraw();

            if (unfinishedDraw != null) {
                System.out.println("找到未开奖的期号: " + unfinishedDraw.getId());
                return unfinishedDraw.getId();
            }

            // 创建新一期
            System.out.println("所有期都已开奖，创建新一期");
            return createNewDrawPeriod();

        } catch (Exception e) {
            System.err.println("获取当前期号失败: " + e.getMessage());
            e.printStackTrace();
            return 1; // 默认返回1
        }
    }

    /**
     * 获取未开奖的期号
     */
    private DrawRecord getUnfinishedDraw() {
        try {
            String sql = "SELECT * FROM draw_record WHERE draw_status = '未开奖' ORDER BY id ASC LIMIT 1";

            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                if (rs.next()) {
                    DrawRecord record = new DrawRecord();
                    record.setId(rs.getInt("id"));
                    record.setDrawTime(new java.util.Date(rs.getTimestamp("draw_time").getTime()));
                    record.setDrawStatus(rs.getString("draw_status"));
                    record.setDrawType("normal");

                    // 解析号码字符串
                    String numbersStr = rs.getString("winning_numbers");
                    if (numbersStr != null && !numbersStr.trim().isEmpty()) {
                        record.setNumbersFromString(numbersStr);
                    }

                    return record;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建新的开奖期号
     */
    private int createNewDrawPeriod() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            // 获取最大期号
            String maxSql = "SELECT COALESCE(MAX(id), 0) as max_id FROM draw_record";
            pstmt = conn.prepareStatement(maxSql);
            rs = pstmt.executeQuery();

            int maxDrawId = 0;
            if (rs.next()) {
                maxDrawId = rs.getInt("max_id");
            }

            int newDrawId = maxDrawId + 1;
            System.out.println("当前最大期号: " + maxDrawId + ", 创建新期号: " + newDrawId);

            rs.close();
            pstmt.close();

            // 插入新一期开奖记录
            String insertSql = "INSERT INTO draw_record " +
                    "(draw_status, winning_numbers, draw_time) " +
                    "VALUES (?, ?, NOW())";

            pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "未开奖");
            pstmt.setString(2, ""); // 初始为空字符串

            pstmt.executeUpdate();

            // 获取生成的ID
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("数据库生成的期号: " + generatedId);
                return generatedId;
            }

        } catch (Exception e) {
            System.err.println("创建新期号失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }

    /**
     * 获取用户的彩票记录
     */
    public List<LotteryTicket> getUserTickets(int userId) {
        return lotteryTicketDAO.getTicketsByUserId(userId);
    }
}