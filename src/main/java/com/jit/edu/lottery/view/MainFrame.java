package com.jit.edu.lottery.view;

import com.jit.edu.lottery.controller.AdminController;
import com.jit.edu.lottery.controller.LoginController;
import com.jit.edu.lottery.controller.RegisterController;
import javax.swing.*;

public class MainFrame extends JFrame {
    private LoginPanel loginPanel;         // 登录面板
    private RegisterPanel registerPanel;   // 注册面板
    private AdminPanel adminPanel;         // 管理员面板
    private UserPanel userPanel;           // 用户面板

    private LoginController loginController;        // 登录控制器
    private RegisterController registerController;  // 注册控制器
    private AdminController adminController;        // 管理员控制器

    public MainFrame() {
        initComponents();  // 初始化组件
        setupFrame();      // 设置窗口属性
    }

    private void initComponents() {
        // 初始化控制器
        loginController = new LoginController(this);      // 创建登录控制器
        registerController = new RegisterController(this); // 创建注册控制器
        adminController = new AdminController(this);      // 创建管理员控制器

        // 创建面板
        loginPanel = new LoginPanel(loginController);      // 创建登录面板
        registerPanel = new RegisterPanel(registerController); // 创建注册面板

        // 默认显示登录面板
        setContentPane(loginPanel);  // 设置登录面板为主内容面板
    }

    private void setupFrame() {
        setTitle("彩票购买抽奖系统");          // 设置窗口标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // 设置关闭操作
        setSize(800, 600);           // 设置窗口大小
        setLocationRelativeTo(null); // 居中显示
        setResizable(false);         // 禁止调整大小

        // 设置图标
        try {
            setIconImage(new ImageIcon("resources/icon.png").getImage());  // 尝试设置图标
        } catch (Exception e) {
            // 图标文件不存在，使用默认
        }
    }

    public void showLoginPanel() {
        setContentPane(loginPanel);  // 切换到登录面板
        loginPanel.clearFields();    // 清空登录字段
        revalidate();                // 重新验证布局
        repaint();                   // 重新绘制
    }

    public void showRegisterPanel() {
        setContentPane(registerPanel);  // 切换到注册面板
        registerPanel.clearFields();    // 清空注册字段
        revalidate();                   // 重新验证布局
        repaint();                      // 重新绘制
    }

    public void showAdminPanel() {
        if (adminPanel == null) {
            adminPanel = new AdminPanel();  // 懒加载管理员面板
        }
        setContentPane(adminPanel);  // 切换到管理员面板
        revalidate();                // 重新验证布局
        repaint();                   // 重新绘制
    }

    public void showUserPanel() {
        if (userPanel == null) {
            userPanel = new UserPanel();  // 懒加载用户面板
        }
        setContentPane(userPanel);  // 切换到用户面板
        revalidate();               // 重新验证布局
        repaint();                  // 重新绘制
    }

    public static void main(String[] args) {
        // 设置外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  // 使用系统外观
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化数据库
        com.jit.edu.lottery.utils.DatabaseUtil.initDatabase();  // 初始化数据库表

        // 启动界面
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();  // 创建主窗口
            frame.setVisible(true);             // 显示窗口
        });
    }
}
