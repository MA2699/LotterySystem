package com.jit.edu.lottery.service;

import com.jit.edu.lottery.model.User;
import javax.swing.JOptionPane;

/**
 * 服务层测试类
 * 用于测试UserService和LotteryService的功能是否正常工作
 */
public class ServiceTest {

    /**
     * 主方法，程序执行的入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("开始服务层测试...");

        // 测试UserService
        testUserService();

        // 测试LotteryService
        testLotteryService();

        System.out.println("服务层测试完成！");
    }

    /**
     * 测试UserService的功能
     * 包括用户注册、登录和充值
     */
    private static void testUserService() {
        System.out.println("\n测试UserService...");
        UserService userService = new UserService();

        // 测试注册功能：创建一个测试用户
        boolean registerResult = userService.register("testuser", "test123", "13800138000");
        if (registerResult) {
            System.out.println("✅ 用户注册测试成功");
        } else {
            System.out.println("⚠️ 用户注册测试失败（可能用户名已存在）");
        }

        // 测试登录功能：验证用户凭据是否正确
        User user = userService.login("testuser", "test123");
        if (user != null) {
            System.out.println("✅ 用户登录测试成功");
            System.out.println("   用户ID: " + user.getId());
            System.out.println("   用户名: " + user.getUsername());
            System.out.println("   余额: " + user.getBalance());
        } else {
            System.out.println("❌ 用户登录测试失败");
        }

        // 测试充值功能：为用户账户增加余额
        if (user != null) {
            boolean rechargeResult = userService.recharge(user.getId(), 100.0);
            if (rechargeResult) {
                System.out.println("✅ 用户充值测试成功");
            } else {
                System.out.println("❌ 用户充值测试失败");
            }
        }
    }

    /**
     * 测试LotteryService的功能
     * 包括彩票购买、中奖通知检查和开奖记录查询
     */
    private static void testLotteryService() {
        System.out.println("\n测试LotteryService...");
        LotteryService lotteryService = new LotteryService();

        // 测试生成随机号码：生成7个1-36的随机数作为彩票号码
        java.util.List<Integer> numbers = new java.util.ArrayList<>();
        for (int i = 0; i < 7; i++) {
            numbers.add((int)(Math.random() * 36) + 1);
        }

        // 测试购买彩票功能：为用户1购买一张彩票
        boolean buyResult = lotteryService.buyTicket(1, numbers, 1, 2.0);
        if (buyResult) {
            System.out.println("✅ 购买彩票测试成功");
        } else {
            System.out.println("❌ 购买彩票测试失败");
        }

        // 测试中奖通知检查功能：检查用户是否有未读的中奖通知
        String notification = lotteryService.checkWinningNotification(1);
        if (notification != null) {
            System.out.println("✅ 中奖通知测试成功");
            System.out.println("   通知内容: " + notification);
        } else {
            System.out.println("✅ 无中奖通知（正常情况）");
        }

        // 测试获取开奖记录功能：查询最近5期开奖记录
        java.util.List<com.jit.edu.lottery.model.DrawRecord> records = lotteryService.getRecentDrawRecords(5);
        System.out.println("✅ 获取最近开奖记录: " + records.size() + " 条");

        // 提示用户需要在图形界面中测试抽奖功能
        System.out.println("\n⚠️ 抽奖滚动测试需要在图形界面中进行");
        JOptionPane.showMessageDialog(null,
                "抽奖功能测试需要在AdminPanel中点击'开始抽奖'和'停止'按钮\n" +
                        "中奖通知功能会在用户登录时自动弹出",
                "测试说明",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
