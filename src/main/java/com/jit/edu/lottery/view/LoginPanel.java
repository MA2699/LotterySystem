package com.jit.edu.lottery.view;

import com.jit.edu.lottery.controller.LoginController;
import javax.swing.*;
import java.awt.*;

/**
 * 登录面板类
 * 提供用户登录界面，支持普通用户和管理员登录
 */
public class LoginPanel extends JPanel {
    // 控制器，处理登录业务逻辑
    private LoginController controller;
    // 用户名和密码输入框
    private JTextField usernameField;
    private JPasswordField passwordField;
    // 登录、注册、退出按钮
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;

    /**
     * 构造函数
     * @param controller 登录控制器
     */
    public LoginPanel(LoginController controller) {
        this.controller = controller;
        controller.setLoginPanel(this);
        initUI();
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // 系统标题
        JLabel titleLabel = new JLabel("彩票购买抽奖系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // 中间区域：登录表单
        add(createLoginForm(), BorderLayout.CENTER);

        // 底部区域：操作按钮
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * 创建登录表单面板
     */
    private JPanel createLoginForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("用户登录"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // 密码输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // 提示信息
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel hintLabel = new JLabel("提示：管理员账号 admin/admin123", SwingConstants.CENTER);
        hintLabel.setFont(new Font("宋体", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        panel.add(hintLabel, gbc);

        return panel;
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 登录按钮
        loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.addActionListener(e -> performLogin());

        // 注册按钮
        registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        registerButton.setPreferredSize(new Dimension(120, 40));
        registerButton.addActionListener(e -> controller.showRegisterPanel());

        // 退出按钮
        exitButton = new JButton("退出");
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        exitButton.setPreferredSize(new Dimension(120, 40));
        exitButton.addActionListener(e -> controller.exitApplication());

        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(exitButton);

        return panel;
    }

    /**
     * 执行登录操作
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // 验证输入是否为空
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 检查是否为管理员账号
        if (com.jit.edu.lottery.utils.ConfigManager.isAdmin(username, password)) {
            JOptionPane.showMessageDialog(this, "管理员登录成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            // 关闭当前窗口，打开管理员界面
            SwingUtilities.getWindowAncestor(this).dispose();
            showAdminFrame();
        } else {
            // 普通用户登录验证
            boolean success = controller.login(username, password);
            if (success) {
                // 关闭当前窗口，打开用户界面
                SwingUtilities.getWindowAncestor(this).dispose();
                showUserFrame(username);
            }
        }
    }

    /**
     * 显示管理员主窗口
     */
    private void showAdminFrame() {
        JFrame adminFrame = new JFrame("彩票系统 - 管理员");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(1000, 700);
        adminFrame.setLocationRelativeTo(null);

        AdminPanel adminPanel = new AdminPanel();
        adminFrame.add(adminPanel);

        adminFrame.setVisible(true);
    }

    /**
     * 显示用户主窗口
     * @param username 登录的用户名
     */
    private void showUserFrame(String username) {
        JFrame userFrame = new JFrame("彩票系统 - 用户: " + username);
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userFrame.setSize(900, 600);
        userFrame.setLocationRelativeTo(null);

        UserPanel userPanel = new UserPanel();
        userFrame.add(userPanel);

        userFrame.setVisible(true);
    }

    /**
     * 清空输入框内容，重置焦点
     */
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }
}
