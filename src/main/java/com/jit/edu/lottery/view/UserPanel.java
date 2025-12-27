package com.jit.edu.lottery.view;

import com.jit.edu.lottery.controller.UserController;
import com.jit.edu.lottery.dao.UserDAO;
import com.jit.edu.lottery.model.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 用户面板类
 * 提供用户操作界面，包括购买彩票、查看记录、账户管理等功能
 */
public class UserPanel extends JPanel {
    // 控制器，处理用户相关业务逻辑
    private UserController controller;
    // 余额显示标签
    private JLabel balanceLabel;
    // 彩票记录显示区域
    private JTextArea ticketArea;
    // 号码标签数组（保持兼容性）
    private JLabel[] numberLabels;
    // 投注倍数选择器
    private JSpinner betCountSpinner;
    // 投注金额显示标签
    private JLabel amountLabel;
    // 当前用户ID
    private int currentUserId;
    // 选项卡面板
    private JTabbedPane tabbedPane;
    // 账户管理面板的余额标签
    private JLabel currentBalanceLabel;
    // 号码选择下拉框数组
    private JComboBox<String>[] numberCombos;

    /**
     * 构造函数（指定用户ID）
     * @param userId 用户ID
     */
    public UserPanel(int userId) {
        this.currentUserId = userId;
        this.controller = new UserController();
        controller.setUserPanel(this);
        initUI();
        loadUserBalance(); // 初始化时加载用户余额
    }

    /**
     * 构造函数（使用当前登录用户ID）
     */
    public UserPanel() {
        this(com.jit.edu.lottery.service.UserService.getCurrentUserId());
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部面板：标题和余额显示
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 选项卡面板：组织不同功能模块
        tabbedPane = new JTabbedPane();

        // 标签1：购买彩票
        tabbedPane.addTab("🎫 购买彩票", createBuyTicketPanel());

        // 标签2：我的彩票
        tabbedPane.addTab("📋 我的彩票", createMyTicketsPanel());

        // 标签3：账户管理
        JPanel accountPanel = createAccountPanel();
        tabbedPane.addTab("💰 账户管理", accountPanel);

        // 添加标签切换监听器，当切换到账户管理时更新余额
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 2 && currentBalanceLabel != null) { // 账户管理标签
                updateAccountPanelBalance();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * 创建顶部面板（标题、余额、用户信息）
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("用户中心", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        // 余额显示
        balanceLabel = new JLabel("余额: ¥0.00", SwingConstants.RIGHT);
        balanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        balanceLabel.setForeground(Color.RED);
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        // 显示当前用户信息
        JLabel userLabel = new JLabel();
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userLabel.setForeground(new Color(100, 100, 100));

        // 更新用户显示
        String userInfo = com.jit.edu.lottery.service.UserService.getCurrentUserInfo();
        userLabel.setText("当前用户: " + userInfo);

        // 退出登录按钮
        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(220, 220, 220));
        logoutButton.addActionListener(e -> performLogout());

        userInfoPanel.add(userLabel);
        userInfoPanel.add(logoutButton);
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(balanceLabel, BorderLayout.EAST);
        panel.add(userInfoPanel, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * 执行退出登录操作
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要退出登录吗？\n退出后需要重新登录",
                "确认退出",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // 执行退出登录
            boolean success = com.jit.edu.lottery.service.UserService.logout();

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "已成功退出登录",
                        "退出成功",
                        JOptionPane.INFORMATION_MESSAGE);

                // 返回到登录界面
                returnToLogin();
            } else {
                JOptionPane.showMessageDialog(this,
                        "退出登录失败",
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 返回到登录界面
     */
    private void returnToLogin() {
        // 获取当前窗口
        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        currentFrame.dispose(); // 关闭当前窗口

        // 重新显示主窗口（登录界面）
        SwingUtilities.invokeLater(() -> {
            MainFrame loginFrame = new MainFrame();
            loginFrame.setVisible(true);
        });
    }

    /**
     * 创建购买彩票面板
     */
    private JPanel createBuyTicketPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 号码选择区域
        JPanel numberPanel = createNumberPanel();
        panel.add(numberPanel, BorderLayout.NORTH);

        // 投注设置区域
        JPanel betPanel = createBetPanel();
        panel.add(betPanel, BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = createBuyButtonPanel();
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 创建号码选择面板
     */
    private JPanel createNumberPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder("选择号码 (7个号码，范围1-36)"));

        // 初始化数组
        numberLabels = new JLabel[7];
        numberCombos = new JComboBox[7];

        // 号码选择网格
        JPanel numberGridPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        for (int i = 0; i < 7; i++) {
            // 每个号码的选择器面板
            JPanel singlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            singlePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

            JLabel indexLabel = new JLabel("号码 " + (i + 1));
            indexLabel.setFont(new Font("宋体", Font.PLAIN, 12));

            // 创建下拉框
            numberCombos[i] = new JComboBox<>();
            numberCombos[i].addItem("请选择");
            for (int num = 1; num <= 36; num++) {
                numberCombos[i].addItem(String.format("%02d", num));
            }
            numberCombos[i].setPreferredSize(new Dimension(80, 25));
            numberCombos[i].setFont(new Font("Arial", Font.PLAIN, 14));

            // 初始化numberLabels数组（保持兼容性）
            numberLabels[i] = new JLabel();

            singlePanel.add(indexLabel);
            singlePanel.add(numberCombos[i]);
            numberGridPanel.add(singlePanel);
        }

        mainPanel.add(numberGridPanel, BorderLayout.CENTER);

        // 提示标签
        JLabel hintLabel = new JLabel("提示：点击下拉箭头选择号码（1-36）", SwingConstants.CENTER);
        hintLabel.setFont(new Font("宋体", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * 创建投注设置面板
     */
    private JPanel createBetPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 投注倍数
        JLabel betLabel = new JLabel("投注倍数:");
        betLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        betCountSpinner = new JSpinner(spinnerModel);

        // 投注金额
        JLabel amountTextLabel = new JLabel("投注金额:");
        amountTextLabel.setFont(new Font("宋体", Font.PLAIN, 14));

        amountLabel = new JLabel("¥2.00");
        amountLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        amountLabel.setForeground(Color.RED);

        // 监听倍数变化，实时更新金额
        betCountSpinner.addChangeListener(e -> updateAmount());

        panel.add(betLabel);
        panel.add(betCountSpinner);
        panel.add(amountTextLabel);
        panel.add(amountLabel);

        return panel;
    }

    /**
     * 更新投注金额显示
     */
    private void updateAmount() {
        int betCount = (Integer) betCountSpinner.getValue();
        double amount = betCount * 2.0; // 每注2元
        amountLabel.setText(String.format("¥%.2f", amount));
    }

    /**
     * 创建购买按钮面板
     */
    private JPanel createBuyButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton randomButton = new JButton("随机选号");
        randomButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        randomButton.addActionListener(e -> generateRandomNumbers());

        JButton clearButton = new JButton("清空号码");
        clearButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        clearButton.addActionListener(e -> clearNumbers());

        JButton buyButton = new JButton("购买彩票");
        buyButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        buyButton.setBackground(new Color(76, 175, 80));
        buyButton.setForeground(Color.WHITE);
        buyButton.addActionListener(e -> buyTicket());

        panel.add(randomButton);
        panel.add(clearButton);
        panel.add(buyButton);

        return panel;
    }

    /**
     * 创建我的彩票面板
     */
    private JPanel createMyTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ticketArea = new JTextArea(20, 50);
        ticketArea.setEditable(false);
        ticketArea.setFont(new Font("宋体", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(ticketArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshTickets());

        buttonPanel.add(refreshButton);

        panel.add(new JLabel("我的彩票记录:", SwingConstants.LEFT), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 初始加载彩票记录
        refreshTickets();

        return panel;
    }

    /**
     * 创建账户管理面板
     */
    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 当前余额显示
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("当前余额:"), gbc);

        gbc.gridx = 1;
        currentBalanceLabel = new JLabel("¥0.00");
        currentBalanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        currentBalanceLabel.setForeground(Color.RED);
        panel.add(currentBalanceLabel, gbc);

        // 充值金额输入
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("充值金额:"), gbc);

        gbc.gridx = 1;
        JTextField rechargeField = new JTextField(15);
        panel.add(rechargeField, gbc);

        // 充值按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton rechargeButton = new JButton("充值");
        rechargeButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        rechargeButton.addActionListener(e -> {
            try {
                String amountText = rechargeField.getText().trim();
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "请输入充值金额！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(panel, "充值金额必须大于0！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 调用控制器进行充值
                boolean success = controller.recharge(currentUserId, amount);
                if (success) {
                    JOptionPane.showMessageDialog(panel, "充值成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    rechargeField.setText("");
                    // 更新所有余额显示
                    updateAllBalanceDisplays();
                } else {
                    JOptionPane.showMessageDialog(panel, "充值失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "请输入有效的金额！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(rechargeButton, gbc);

        // 提示信息
        gbc.gridy = 3;
        JLabel hintLabel = new JLabel("提示：每次购买彩票扣除相应金额，中奖后奖金自动到账", SwingConstants.CENTER);
        hintLabel.setFont(new Font("宋体", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        panel.add(hintLabel, gbc);

        return panel;
    }

    /**
     * 生成随机号码并设置到下拉框
     */
    private void generateRandomNumbers() {
        if (numberCombos != null) {
            for (int i = 0; i < numberCombos.length; i++) {
                // 生成1-36的随机数
                int randomNum = (int) (Math.random() * 36) + 1;
                // 设置下拉框选中对应的号码
                numberCombos[i].setSelectedItem(String.format("%02d", randomNum));
            }
        }
    }

    /**
     * 清空所有选择的号码
     */
    private void clearNumbers() {
        if (numberCombos != null) {
            for (JComboBox<String> combo : numberCombos) {
                combo.setSelectedIndex(0); // 设置为"请选择"
            }
        }
        betCountSpinner.setValue(1);
    }

    /**
     * 执行购买彩票操作
     */
    private void buyTicket() {
        // 检查是否选择了号码（从下拉框检查）
        boolean numbersSelected = false;
        if (numberCombos != null) {
            for (JComboBox<String> combo : numberCombos) {
                String selected = (String) combo.getSelectedItem();
                if (selected != null && !selected.equals("请选择")) {
                    numbersSelected = true;
                    break;
                }
            }
        }
        if (!numbersSelected) {
            JOptionPane.showMessageDialog(this, "请先选择号码！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选择的号码（从下拉框获取）
        java.util.List<Integer> numbers = new java.util.ArrayList<>();
        if (numberCombos != null) {
            for (JComboBox<String> combo : numberCombos) {
                String selected = (String) combo.getSelectedItem();
                if (selected != null && !selected.equals("请选择")) {
                    numbers.add(Integer.parseInt(selected));
                }
            }
        }

        // 获取投注倍数和金额
        int betCount = (Integer) betCountSpinner.getValue();
        double amount = betCount * 2.0;

        // 确认购买对话框
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认购买？\n号码：" + numbers +
                        "\n倍数：" + betCount +
                        "\n金额：¥" + String.format("%.2f", amount) +
                        "\n\n确认后将从您的余额中扣除相应金额",
                "确认购买", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.buyTicket(currentUserId, numbers, betCount, amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "购买成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                clearNumbers();
                refreshTickets();
                // 更新所有余额显示
                updateAllBalanceDisplays();
            } else {
                JOptionPane.showMessageDialog(this,
                        "购买失败！\n可能的原因：\n1. 余额不足\n2. 数据库错误",
                        "失败", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 刷新彩票记录显示
     */
    private void refreshTickets() {
        try {
            List<String> tickets = controller.getUserTickets(currentUserId);
            if (tickets == null || tickets.isEmpty()) {
                ticketArea.setText("暂无彩票记录\n提示：您还没有购买任何彩票");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("您共有 ").append(tickets.size()).append(" 张彩票：\n");
                sb.append("========================================\n");
                for (String ticket : tickets) {
                    sb.append(ticket).append("\n");
                }
                ticketArea.setText(sb.toString());
            }
        } catch (Exception e) {
            ticketArea.setText("获取彩票记录失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 加载用户余额（仅更新顶部余额标签）
     */
    private void loadUserBalance() {
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(currentUserId);

            if (user != null) {
                double balance = user.getBalance();
                balanceLabel.setText(String.format("余额: ¥%.2f", balance));
            } else {
                balanceLabel.setText("余额: ¥0.00");
            }
        } catch (Exception e) {
            e.printStackTrace();
            balanceLabel.setText("余额: 加载失败");
        }
    }

    /**
     * 更新账户管理面板的余额显示
     */
    private void updateAccountPanelBalance() {
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(currentUserId);

            if (user != null && currentBalanceLabel != null) {
                double balance = user.getBalance();
                currentBalanceLabel.setText(String.format("¥%.2f", balance));
            } else if (currentBalanceLabel != null) {
                currentBalanceLabel.setText("¥0.00");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (currentBalanceLabel != null) {
                currentBalanceLabel.setText("加载失败");
            }
        }
    }

    /**
     * 同时更新所有余额显示（顶部和账户管理面板）
     */
    private void updateAllBalanceDisplays() {
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(currentUserId);

            if (user != null) {
                double balance = user.getBalance();

                // 更新顶部余额
                balanceLabel.setText(String.format("余额: ¥%.2f", balance));

                // 更新账户管理面板余额
                if (currentBalanceLabel != null) {
                    currentBalanceLabel.setText(String.format("¥%.2f", balance));
                }
            } else {
                balanceLabel.setText("余额: ¥0.00");
                if (currentBalanceLabel != null) {
                    currentBalanceLabel.setText("¥0.00");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            balanceLabel.setText("余额: 加载失败");
            if (currentBalanceLabel != null) {
                currentBalanceLabel.setText("加载失败");
            }
        }
    }

    /**
     * 获取当前用户ID
     */
    public int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * 设置当前用户ID（用于切换用户）
     */
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        // 更新所有显示
        updateAllBalanceDisplays();
        refreshTickets();
    }

    /**
     * 更新余额显示（供其他类调用）
     */
    public void updateBalanceDisplay() {
        updateAllBalanceDisplays();
    }
}