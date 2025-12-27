package com.jit.edu.lottery.controller;

import com.jit.edu.lottery.view.AdminPanel;
import com.jit.edu.lottery.view.MainFrame;
import com.jit.edu.lottery.service.LotteryService;

/**
 * 管理员控制器
 * 处理管理员界面和抽奖功能的控制逻辑
 */
public class AdminController {
    private MainFrame mainFrame;       // 主窗口，用于界面切换
    private AdminPanel adminPanel;     // 管理员面板
    private LotteryService lotteryService; // 抽奖服务

    /**
     * 构造函数：初始化管理员控制器
     */
    public AdminController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.lotteryService = new LotteryService();
        this.adminPanel = new AdminPanel();
    }

    /**
     * 构造函数：使用指定的管理员面板
     */
    public AdminController(MainFrame mainFrame, AdminPanel adminPanel) {
        this.mainFrame = mainFrame;
        this.lotteryService = new LotteryService();
        this.adminPanel = adminPanel;
    }

    /**
     * 显示管理员面板
     * 将管理员界面设置为主窗口内容
     */
    public void showAdminPanel() {
        if (adminPanel == null) {
            adminPanel = new AdminPanel();
        }
        mainFrame.setContentPane(adminPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * 执行抽奖操作
     * 注意：抽奖功能实际在DrawPanel中实现
     */
    public void performDraw() {
        // 抽奖功能在DrawPanel中实现
    }

    /**
     * 获取抽奖通知信息
     */
    public String getDrawNotifications() {
        return "管理员通知功能";
    }
}