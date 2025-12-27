package com.jit.edu.lottery.controller;

import com.jit.edu.lottery.model.User;
import com.jit.edu.lottery.service.UserService;
import com.jit.edu.lottery.view.LoginPanel;
import com.jit.edu.lottery.view.MainFrame;
import javax.swing.JOptionPane;

/**
 * 登录控制器
 * 处理用户登录、注册和退出等操作
 */
public class LoginController {
    private UserService userService;   // 用户服务
    private LoginPanel loginPanel;     // 登录面板
    private MainFrame mainFrame;       // 主窗口

    /**
     * 构造函数：初始化登录控制器
     */
    public LoginController(MainFrame mainFrame) {
        this.userService = new UserService();
        this.mainFrame = mainFrame;
    }

    /**
     * 设置登录面板
     */
    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录是否成功
     */
    public boolean login(String username, String password) {
        // 检查输入是否为空
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "用户名和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 调用用户服务验证登录
        User user = userService.login(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(null, "登录成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

            // 检查是否有中奖通知
            String notification = userService.getUserNotification(user.getId());
            if (notification != null) {
                JOptionPane.showMessageDialog(null, notification, "🎉 中奖通知", JOptionPane.INFORMATION_MESSAGE);
                userService.clearUserNotification(user.getId());
            }

            return true;
        } else {
            JOptionPane.showMessageDialog(null, "用户名或密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 显示注册面板
     */
    public void showRegisterPanel() {
        mainFrame.showRegisterPanel();
    }

    /**
     * 退出应用程序
     */
    public void exitApplication() {
        int result = JOptionPane.showConfirmDialog(null, "确定要退出吗？", "退出",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}