package com.jit.edu.lottery.view;

import com.jit.edu.lottery.controller.RegisterController;
import com.jit.edu.lottery.model.User;
import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {
    private RegisterController controller;                // 注册控制器
    private JTextField usernameField;                     // 用户名输入框
    private JPasswordField passwordField;                 // 密码输入框
    private JPasswordField confirmPasswordField;          // 确认密码输入框
    private JTextField phoneField;                        // 手机号输入框
    private JButton registerButton;                       // 注册按钮
    private JButton backButton;                           // 返回按钮

    public RegisterPanel(RegisterController controller) {
        this.controller = controller;
        controller.setRegisterPanel(this);  // 设置面板引用
        initUI();  // 初始化界面
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));  // 使用边界布局
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));  // 设置内边距

        // 标题
        JLabel titleLabel = new JLabel("用户注册", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);  // 添加标题到顶部

        // 中间：注册表单
        add(createRegisterForm(), BorderLayout.CENTER);  // 添加注册表单

        // 底部：按钮
        add(createButtonPanel(), BorderLayout.SOUTH);  // 添加按钮面板
    }

    private JPanel createRegisterForm() {
        JPanel panel = new JPanel(new GridBagLayout());  // 使用网格袋布局
        panel.setBorder(BorderFactory.createTitledBorder("填写注册信息"));  // 设置带标题边框
        GridBagConstraints gbc = new GridBagConstraints();  // 布局约束
        gbc.insets = new Insets(10, 10, 10, 10);  // 设置组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL;  // 水平填充

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:*"), gbc);  // 用户名标签

        gbc.gridx = 1;
        usernameField = new JTextField(20);  // 用户名输入框
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:*"), gbc);  // 密码标签

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);  // 密码输入框
        panel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("确认密码:*"), gbc);  // 确认密码标签

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);  // 确认密码输入框
        panel.add(confirmPasswordField, gbc);

        // 手机号
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("手机号:"), gbc);  // 手机号标签

        gbc.gridx = 1;
        phoneField = new JTextField(20);  // 手机号输入框
        panel.add(phoneField, gbc);

        // 提示标签
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel hintLabel = new JLabel("注：*为必填项，新用户注册赠送100元余额", SwingConstants.CENTER);
        hintLabel.setFont(new Font("宋体", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        panel.add(hintLabel, gbc);  // 添加提示信息

        return panel;  // 返回表单面板
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));  // 流式布局

        registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        registerButton.setPreferredSize(new Dimension(120, 40));  // 设置按钮大小
        registerButton.addActionListener(e -> performRegister());  // 注册按钮点击事件

        backButton = new JButton("返回登录");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        backButton.setPreferredSize(new Dimension(120, 40));  // 设置按钮大小
        backButton.addActionListener(e -> controller.backToLogin());  // 返回按钮点击事件

        panel.add(registerButton);  // 添加注册按钮
        panel.add(backButton);      // 添加返回按钮

        return panel;  // 返回按钮面板
    }

    private void performRegister() {
        String username = usernameField.getText().trim();  // 获取用户名
        String password = new String(passwordField.getPassword());  // 获取密码
        String confirmPassword = new String(confirmPasswordField.getPassword());  // 获取确认密码
        String phone = phoneField.getText().trim();  // 获取手机号

        // 验证输入
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
            return;  // 输入为空，返回
        }

        if (!password.equals(confirmPassword)) {  // 检查密码一致性
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");  // 清空密码框
            confirmPasswordField.setText("");  // 清空确认密码框
            passwordField.requestFocus();  // 焦点回到密码框
            return;
        }

        if (password.length() < 6) {  // 检查密码长度
            JOptionPane.showMessageDialog(this, "密码长度至少6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);  // 设置用户名
        user.setPassword(password);  // 设置密码
        user.setPhone(phone.isEmpty() ? "未填写" : phone);  // 设置手机号
        user.setBalance(100.0);  // 设置初始余额100元

        // 调用控制器注册
        boolean success = controller.register(user);  // 执行注册
        if (success) {
            clearFields();  // 注册成功清空字段
        }
    }

    public void clearFields() {
        usernameField.setText("");            // 清空用户名
        passwordField.setText("");            // 清空密码
        confirmPasswordField.setText("");     // 清空确认密码
        phoneField.setText("");               // 清空手机号
        usernameField.requestFocus();         // 焦点回到用户名输入框
    }
}
