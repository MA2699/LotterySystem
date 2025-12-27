package com.jit.edu.lottery;

import com.jit.edu.lottery.view.MainFrame;
import com.jit.edu.lottery.utils.DatabaseUtil;
import com.jit.edu.lottery.utils.ConfigManager;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 彩票购买抽奖系统启动 ===");
        System.out.println("作者: 课程设计项目");
        System.out.println("版本: 1.0.0");
        System.out.println("========================\n");

        // 初始化配置
        System.out.println("1. 加载配置文件...");
        String dbUrl = ConfigManager.getDatabaseUrl();  // 获取数据库URL
        System.out.println("   数据库: " + dbUrl);  // 打印数据库信息
        System.out.println("   彩票单价: ¥" + ConfigManager.getTicketPrice());  // 打印彩票单价
        System.out.println("   特等奖奖金: ¥" + ConfigManager.getSpecialPrize());  // 打印特等奖奖金
        System.out.println("   一等奖奖金: ¥" + ConfigManager.getFirstPrize());  // 打印一等奖奖金

        // 初始化数据库
        System.out.println("\n2. 初始化数据库...");
        try {
            DatabaseUtil.initDatabase();  // 初始化数据库表
            if (DatabaseUtil.testConnected()) {
                System.out.println("   ✅ 数据库初始化成功");  // 数据库连接成功
            } else {
                System.out.println("   ⚠️ 数据库连接测试失败");  // 数据库连接失败
            }
        } catch (Exception e) {
            System.out.println("   ❌ 数据库初始化失败: " + e.getMessage());  // 初始化失败提示
            JOptionPane.showMessageDialog(null,
                    "数据库初始化失败: " + e.getMessage() + "\n程序将继续运行，但部分功能可能受限。",
                    "警告", JOptionPane.WARNING_MESSAGE);  // 显示警告对话框
        }

        // 创建并显示GUI
        System.out.println("\n3. 启动图形界面...");
        try {
            // 设置系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  // 使用系统默认外观
        } catch (Exception e) {
            System.out.println("   ⚠️ 无法设置系统外观: " + e.getMessage());  // 外观设置失败
        }

        SwingUtilities.invokeLater(() -> {  // 在事件分发线程中运行GUI
            try {
                MainFrame frame = new MainFrame();  // 创建主窗口
                frame.setVisible(true);  // 显示窗口
                System.out.println("   ✅ 主窗口创建成功");  // 窗口创建成功
                System.out.println("   🎉 系统启动完成！");  // 系统启动完成

                // 显示欢迎信息
                showWelcomeMessage();  // 显示欢迎对话框
            } catch (Exception e) {
                System.out.println("   ❌ 创建主窗口失败: " + e.getMessage());  // 窗口创建失败
                e.printStackTrace();  // 打印异常堆栈
                JOptionPane.showMessageDialog(null,
                        "启动图形界面失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);  // 显示错误对话框
            }
        });
    }

    private static void showWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {  // 在事件分发线程中显示对话框
            String message =
                    "🎉 欢迎使用彩票购买抽奖系统！\n\n" +
                            "📋 系统功能：\n" +
                            "   • 用户注册登录\n" +
                            "   • 购买彩票（手动/随机选号）\n" +
                            "   • 抽奖功能（动态号码滚动）\n" +
                            "   • 中奖通知（登录时提醒）\n" +
                            "   • 账户充值管理\n\n" +
                            "🔑 测试账号：\n" +
                            "   管理员：admin / admin123\n" +
                            "   普通用户：可自行注册\n\n" +
                            "💡 使用说明：\n" +
                            "   1. 首次使用请先注册账号\n" +
                            "   2. 登录后可购买彩票\n" +
                            "   3. 管理员可进行抽奖操作\n" +
                            "   4. 中奖后奖金自动到账";

            JOptionPane.showMessageDialog(null, message, "欢迎使用", JOptionPane.INFORMATION_MESSAGE);  // 显示欢迎对话框
        });
    }
}