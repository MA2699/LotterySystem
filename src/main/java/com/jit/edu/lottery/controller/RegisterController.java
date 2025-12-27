package com.jit.edu.lottery.controller;

import com.jit.edu.lottery.model.User;
import com.jit.edu.lottery.service.UserService;
import com.jit.edu.lottery.view.MainFrame;
import com.jit.edu.lottery.view.RegisterPanel;
import javax.swing.JOptionPane;

/**
 * 注册控制器
 * 处理用户注册和返回登录等操作
 */
public class RegisterController {
    private UserService userService;       // 用户服务
    private RegisterPanel registerPanel;   // 注册面板
    private MainFrame mainFrame;           // 主窗口

    /**
     * 构造函数：初始化注册控制器
     */
    public RegisterController(MainFrame mainFrame) {
        this.userService = new UserService();
        this.mainFrame = mainFrame;
    }

    /**
     * 设置注册面板
     */
    public void setRegisterPanel(RegisterPanel registerPanel) {
        this.registerPanel = registerPanel;
    }

    /**
     * 用户注册
     * @param user 用户对象
     * @return 注册是否成功
     */
    public boolean register(User user) {
        // 验证输入数据
        if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
            JOptionPane.showMessageDialog(null, "用户名和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 验证密码长度
        if (user.getPassword().length() < 6) {
            JOptionPane.showMessageDialog(null, "密码长度至少6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 检查用户名是否已存在
        if (userService.isUsernameExists(user.getUsername())) {
            JOptionPane.showMessageDialog(null, "用户名已存在！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 调用用户服务执行注册
        boolean success = userService.register(user.getUsername(), user.getPassword(), user.getPhone());
        if (success) {
            JOptionPane.showMessageDialog(null, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "注册失败！", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 返回登录界面
     */
    public void backToLogin() {
        mainFrame.showLoginPanel();
    }
}