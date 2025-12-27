package com.jit.edu.lottery.controller;

import com.jit.edu.lottery.service.LotteryService;
import com.jit.edu.lottery.service.UserService;
import com.jit.edu.lottery.view.UserPanel;
import com.jit.edu.lottery.model.LotteryTicket;
import javax.swing.*;
import java.util.*;

/**
 * 用户控制器类
 * 处理用户相关的业务逻辑，包括购买彩票、充值、查询等操作
 */
public class UserController {
    // 彩票服务对象，处理彩票相关业务
    private LotteryService lotteryService;
    // 用户服务对象，处理用户相关业务
    private UserService userService;
    // 用户面板对象，用于更新UI
    private UserPanel userPanel;

    /**
     * 构造函数，初始化服务对象
     */
    public UserController() {
        this.lotteryService = new LotteryService();
        this.userService = new UserService();
    }

    /**
     * 设置用户面板
     * @param panel 用户面板对象
     */
    public void setUserPanel(UserPanel panel) {
        this.userPanel = panel;
    }

    /**
     * 生成随机号码数组（兼容性方法）
     * @return 包含7个随机号码的列表
     */
    public List<Integer> generateRandomNumbersArray() {
        return generateRandomNumbers();
    }

    /**
     * 生成随机号码（允许重复号码）
     * @return 包含7个1-36随机号码的列表，可能包含重复
     */
    public List<Integer> generateRandomNumbers() {
        Random rand = new Random();
        List<Integer> result = new ArrayList<>();

        // 生成7个随机号码（不再检查重复）
        for (int i = 0; i < 7; i++) {
            result.add(rand.nextInt(36) + 1);
        }

        // 保持原始顺序（位置对应），不排序
        return result;
    }

    /**
     * 购买彩票（修复重复扣款问题）
     * @param userId 用户ID
     * @param numbers 选择的号码列表
     * @param betCount 投注倍数
     * @param amount 投注金额
     * @return 购买是否成功
     */
    public boolean buyTicket(int userId, List<Integer> numbers, int betCount, double amount) {
        // 验证投注倍数
        if (betCount <= 0) {
            JOptionPane.showMessageDialog(null, "投注倍数必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 验证金额
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "金额必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 强制修正金额为正确值（2元×倍数）
        double correctAmount = 2.0 * betCount;
        if (Math.abs(amount - correctAmount) > 0.01) {
            System.out.println("金额修正：" + amount + " -> " + correctAmount);
            amount = correctAmount;
        }

        // 检查用户余额是否足够
        double userBalance = userService.getUserBalance(userId);
        if (userBalance < amount) {
            JOptionPane.showMessageDialog(null,
                    String.format("余额不足！当前余额: ¥%.2f, 需要: ¥%.2f", userBalance, amount),
                    "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        System.out.println("调用 LotteryService.buyTicket() 购买彩票...");
        // 调用彩票服务购买彩票
        boolean buyResult = lotteryService.buyTicket(userId, numbers, betCount, amount);

        if (buyResult) {
            // 购买成功后获取最新余额
            double newBalance = userService.getUserBalance(userId);
            JOptionPane.showMessageDialog(null,
                    String.format("购买成功！\n号码：%s\n倍数：%d\n金额：¥%.2f\n余额：¥%.2f",
                            numbers, betCount, amount, newBalance),
                    "成功", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "购买失败！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 获取用户彩票记录（返回字符串列表格式）
     * @param userId 用户ID
     * @return 彩票记录字符串列表
     */
    public List<String> getUserTickets(int userId) {
        // 获取用户的所有彩票记录
        List<LotteryTicket> tickets = lotteryService.getUserTickets(userId);
        List<String> ticketStrings = new ArrayList<>();

        // 将彩票记录格式化为字符串
        for (LotteryTicket ticket : tickets) {
            String status = (ticket.getDrawId() > 0 && "已开奖".equals(ticket.getTicketStatus())) ? "已开奖" : "待开奖";
            String numbersStr = ticket.getNumbersString();
            ticketStrings.add(String.format("第%03d期: %s - 倍数:%d - 状态:%s",
                    ticket.getId(), numbersStr, ticket.getBetCount(), status));
        }

        return ticketStrings;
    }

    /**
     * 用户充值（修复版）
     * @param userId 用户ID
     * @param amount 充值金额
     * @return 充值是否成功
     */
    public boolean recharge(int userId, double amount) {
        System.out.println("\n=== UserController.recharge() 开始 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("充值金额: " + amount);

        // 验证充值金额
        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "充值金额必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        System.out.println("调用 UserService.recharge()...");
        // 调用用户服务进行充值
        boolean success = userService.recharge(userId, amount);

        if (success) {
            // 充值成功后获取最新余额
            double newBalance = userService.getUserBalance(userId);
            JOptionPane.showMessageDialog(null,
                    String.format("充值成功！\n充值金额：¥%.2f\n当前余额：¥%.2f", amount, newBalance),
                    "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "充值失败！\n可能原因：\n1. 用户不存在\n2. 数据库错误",
                    "错误", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("充值结果: " + (success ? "成功" : "失败"));
        return success;
    }

    /**
     * 获取用户余额
     * @param userId 用户ID
     * @return 用户余额
     */
    public double getUserBalance(int userId) {
        return userService.getUserBalance(userId);
    }

    /**
     * 检查用户是否有未读中奖通知
     * @param userId 用户ID
     * @return 中奖通知内容，如果没有则返回null
     */
    public String checkWinningNotification(int userId) {
        return userService.getUserNotification(userId);
    }
}