package com.jit.edu.lottery.view;

import com.jit.edu.lottery.service.LotteryService;
import com.jit.edu.lottery.model.DrawRecord;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Date;
import java.io.File;

/**
 * 管理员面板类
 * 提供管理员专用的抽奖管理功能，包括预设号码、开奖记录、系统日志等
 */
public class AdminPanel extends JPanel {
    // 彩票服务对象
    private LotteryService lotteryService;
    // 抽奖面板
    private DrawPanel drawPanel;
    // 日志和记录显示区域
    private JTextArea logArea;
    private JTextArea recordArea;

    // 预设号码相关组件
    private JComboBox<String>[] presetCombos;
    private JCheckBox usePresetCheckBox;
    private List<Integer> presetNumbers = null;

    // 开奖记录管理
    private List<DrawRecord> drawRecords = new ArrayList<>();
    private int drawRecordId = 1;

    /**
     * 构造函数，初始化管理员面板
     */
    public AdminPanel() {
        lotteryService = new LotteryService();
        initUI();
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部容器面板（标题和退出按钮）
        JPanel topContainerPanel = new JPanel(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("管理员面板 - 彩票抽奖管理", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 102, 204));

        // 退出登录按钮面板
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(220, 220, 220));
        logoutButton.addActionListener(e -> performLogout());
        logoutPanel.add(logoutButton);

        topContainerPanel.add(titleLabel, BorderLayout.CENTER);
        topContainerPanel.add(logoutPanel, BorderLayout.EAST);
        add(topContainerPanel, BorderLayout.NORTH);

        // 选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();

        // 标签1：抽奖面板（包含预设功能）
        JPanel drawContainerPanel = new JPanel(new BorderLayout(10, 10));
        drawContainerPanel.add(createPresetPanel(), BorderLayout.NORTH);
        drawPanel = new DrawPanel();
        drawPanel.setAdminPanel(this);  // 设置引用
        drawContainerPanel.add(drawPanel, BorderLayout.CENTER);
        setupPresetControl();
        tabbedPane.addTab("🎰 抽奖", drawContainerPanel);

        // 标签2：开奖记录
        JPanel recordPanel = createRecordPanel();
        tabbedPane.addTab("📋 开奖记录", recordPanel);

        // 标签3：系统日志
        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("📊 系统日志", logPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // 底部按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshAll());

        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> System.exit(0));

        bottomPanel.add(refreshButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建预设号码面板
     */
    private JPanel createPresetPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("预设中奖号码（测试功能）"));
        panel.setBackground(new Color(240, 248, 255));

        // 顶部：复选框
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usePresetCheckBox = new JCheckBox("使用预设号码进行抽奖测试");
        usePresetCheckBox.setFont(new Font("宋体", Font.PLAIN, 12));
        usePresetCheckBox.setToolTipText("勾选后，抽奖将使用下方预设的号码（部分未预设的号码会随机生成）");
        topPanel.add(usePresetCheckBox);
        panel.add(topPanel, BorderLayout.NORTH);

        // 中间：7个号码选择器
        presetCombos = new JComboBox[7];
        JPanel numberGrid = new JPanel(new GridLayout(1, 7, 5, 5));
        numberGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < 7; i++) {
            JPanel singlePanel = new JPanel(new BorderLayout(5, 5));

            // 标签
            JLabel label = new JLabel("号码" + (i + 1), SwingConstants.CENTER);
            label.setFont(new Font("宋体", Font.BOLD, 12));

            // 下拉框
            presetCombos[i] = new JComboBox<>();
            presetCombos[i].addItem("随机");
            for (int num = 1; num <= 36; num++) {
                presetCombos[i].addItem(String.format("%02d", num));
            }
            presetCombos[i].setPreferredSize(new Dimension(70, 25));
            presetCombos[i].setFont(new Font("Arial", Font.PLAIN, 12));

            singlePanel.add(label, BorderLayout.NORTH);
            singlePanel.add(presetCombos[i], BorderLayout.CENTER);
            numberGrid.add(singlePanel);
        }

        panel.add(numberGrid, BorderLayout.CENTER);

        // 底部：控制按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton randomAllBtn = new JButton("全部随机");
        randomAllBtn.addActionListener(e -> setAllRandom());

        JButton clearBtn = new JButton("清空预设");
        clearBtn.addActionListener(e -> clearPreset());

        JButton quickSetBtn = new JButton("快速设置(1-7)");
        quickSetBtn.addActionListener(e -> quickSetNumbers());

        // 立即开奖按钮
        JButton immediateDrawBtn = new JButton("立即开奖");
        immediateDrawBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        immediateDrawBtn.setBackground(new Color(255, 87, 34));
        immediateDrawBtn.setForeground(Color.WHITE);
        immediateDrawBtn.setPreferredSize(new Dimension(120, 35));
        immediateDrawBtn.setToolTipText("不经过滚动动画，直接使用预设号码开奖（用于测试中奖）");
        immediateDrawBtn.addActionListener(e -> performImmediateDraw());

        buttonPanel.add(randomAllBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(quickSetBtn);
        buttonPanel.add(immediateDrawBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 立即开奖方法（移除重复检查，允许重复号码）
     */
    private void performImmediateDraw() {
        // 检查是否勾选了使用预设
        if (!usePresetCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "请先勾选\"使用预设号码进行抽奖测试\"",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取预设号码
        List<Integer> numbers = getPresetNumbersForDraw();

        if (numbers == null || numbers.size() != 7) {
            JOptionPane.showMessageDialog(this,
                    "无法获取有效的预设号码，请检查设置",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 不再检查重复号码，允许重复
        // 注释掉重复检查代码

        // 确认提示
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定使用以下号码立即开奖吗？\n\n" +
                        "📅 当前期号将直接开奖\n" +
                        "🏆 中奖号码：" + numbers + "\n\n" +
                        "⚠️ 警告：这将跳过滚动动画，直接开奖！\n" +
                        "（用于测试中奖功能）",
                "确认立即开奖",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // 调用DrawPanel的立即开奖方法
            if (drawPanel != null) {
                drawPanel.drawImmediately(numbers);
                addLog("立即开奖执行成功，号码：" + numbers);
            }
        }
    }

    /**
     * 添加日志消息
     * @param message 日志内容
     */
    public void addLog(String message) {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * 设置所有号码选择器为随机
     */
    private void setAllRandom() {
        if (presetCombos != null) {
            for (int i = 0; i < presetCombos.length; i++) {
                if (presetCombos[i] != null) {
                    presetCombos[i].setSelectedIndex(0); // 设置为"随机"
                }
            }
        }
        addLog("预设号码已全部设置为随机");
    }

    /**
     * 清空预设号码
     */
    private void clearPreset() {
        setAllRandom();
        presetNumbers = null;
        usePresetCheckBox.setSelected(false);
        addLog("预设号码已清空");
    }

    /**
     * 快速设置测试号码（1-7）
     */
    private void quickSetNumbers() {
        if (presetCombos != null) {
            for (int i = 0; i < 7; i++) {
                if (presetCombos[i] != null) {
                    presetCombos[i].setSelectedItem(String.format("%02d", i + 1));
                }
            }
        }
        addLog("快速设置号码为：01, 02, 03, 04, 05, 06, 07");
    }

    /**
     * 设置预设控制到DrawPanel
     */
    private void setupPresetControl() {
        // 创建一个定时器检查复选框状态
        Timer checkTimer = new Timer(500, e -> {
            if (usePresetCheckBox.isSelected()) {
                List<Integer> numbers = getPresetNumbers();
                if (numbers != null && numbers.size() == 7) {
                    presetNumbers = numbers;
                }
            } else {
                presetNumbers = null;
            }
        });
        checkTimer.setRepeats(true);
        checkTimer.start();
    }

    /**
     * 获取预设号码
     */
    private List<Integer> getPresetNumbers() {
        List<Integer> numbers = new ArrayList<>();
        if (presetCombos != null) {
            for (JComboBox<String> combo : presetCombos) {
                if (combo != null) {
                    String selected = (String) combo.getSelectedItem();
                    if (selected != null && !selected.equals("随机")) {
                        numbers.add(Integer.parseInt(selected));
                    }
                }
            }
        }
        return numbers;
    }

    /**
     * 检查是否使用预设号码模式
     */
    public boolean shouldUsePreset() {
        return usePresetCheckBox.isSelected();
    }

    /**
     * 获取预设号码（供DrawPanel调用，允许重复号码）
     */
    public List<Integer> getPresetNumbersForDraw() {
        if (!shouldUsePreset()) {
            return null;
        }

        List<Integer> numbers = getPresetNumbers();

        // 如果用户预设了部分号码，补全剩余为随机（允许重复）
        if (numbers != null && numbers.size() < 7) {
            Random random = new Random();
            while (numbers.size() < 7) {
                // 允许重复，不再检查是否已存在
                int randomNum = random.nextInt(36) + 1;
                numbers.add(randomNum);
            }
            // 不再排序，保持原始顺序
            addLog("预设号码已补全为: " + numbers);
        } else if (numbers != null && numbers.size() == 7) {
            // 不再排序
        }

        return numbers;
    }

    /**
     * 添加开奖记录到内存
     */
    public void addDrawRecord(List<Integer> winningNumbers) {
        DrawRecord record = new DrawRecord();
        record.setId(drawRecordId++);
        record.setDrawTime(new Date());
        record.setWinningNumbers(winningNumbers);
        record.setDrawStatus("已开奖");

        // 添加到开头，最新记录在前
        drawRecords.add(0, record);

        // 只保留最近20条记录
        if (drawRecords.size() > 20) {
            drawRecords = drawRecords.subList(0, 20);
        }

        // 更新显示
        updateRecordArea();
    }

    /**
     * 创建开奖记录面板
     */
    private JPanel createRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        recordArea = new JTextArea(20, 60);
        recordArea.setEditable(false);
        recordArea.setFont(new Font("宋体", Font.PLAIN, 14));

        // 显示最近开奖记录
        updateRecordArea();

        JScrollPane scrollPane = new JScrollPane(recordArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("刷新记录");
        refreshButton.addActionListener(e -> updateRecordArea());

        JButton clearButton = new JButton("清空记录");
        clearButton.addActionListener(e -> {
            drawRecords.clear();
            drawRecordId = 1;
            recordArea.setText("");
            addLog("开奖记录已清空");
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(clearButton);

        panel.add(new JLabel("最近开奖记录：", SwingConstants.LEFT), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 更新开奖记录显示
     */
    private void updateRecordArea() {
        StringBuilder sb = new StringBuilder();

        // 优先使用内存中的记录
        if (drawRecords.isEmpty()) {
            // 如果没有内存记录，尝试从服务层获取
            try {
                List<DrawRecord> records = lotteryService.getRecentDrawRecords(10);
                if (records != null && !records.isEmpty()) {
                    drawRecords = records;
                    if (!records.isEmpty()) {
                        drawRecordId = records.get(0).getId() + 1;
                    }
                }
            } catch (Exception e) {
                // 忽略异常，使用空记录
            }
        }

        if (drawRecords.isEmpty()) {
            sb.append("暂无开奖记录\n");
        } else {
            sb.append("共 ").append(drawRecords.size()).append(" 条开奖记录：\n");
            sb.append("========================================\n\n");

            for (DrawRecord record : drawRecords) {
                sb.append("第 ").append(record.getId()).append(" 期\n");
                sb.append("开奖时间: ").append(record.getDrawTime()).append("\n");
                sb.append("中奖号码: ");
                if (record.getWinningNumbers() != null && !record.getWinningNumbers().isEmpty()) {
                    sb.append(record.getWinningNumbers());
                } else {
                    sb.append("[]");
                }
                sb.append("\n");
                sb.append("开奖状态: ").append(record.getDrawStatus() != null ? record.getDrawStatus() : "已开奖").append("\n");
                sb.append("开奖方式: ").append(record.getDrawType() != null ? record.getDrawType() : "正常开奖").append("\n");
                sb.append("----------------------------------------\n");
            }
        }

        recordArea.setText(sb.toString());
    }

    /**
     * 创建系统日志面板
     */
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        logArea = new JTextArea(15, 60);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearButton = new JButton("清空日志");
        clearButton.addActionListener(e -> logArea.setText(""));

        JButton exportButton = new JButton("导出日志");
        exportButton.addActionListener(e -> exportLog());

        buttonPanel.add(clearButton);
        buttonPanel.add(exportButton);

        // 添加示例日志
        addLog("系统启动成功");
        addLog("数据库连接正常");
        addLog("欢迎使用彩票抽奖系统");
        addLog("管理员面板已加载");
        addLog("预设号码功能已启用");
        addLog("立即开奖功能已就绪（用于测试中奖）");

        panel.add(new JLabel("系统操作日志：", SwingConstants.LEFT), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 导出日志到文件
     */
    private void exportLog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出日志文件");
        fileChooser.setSelectedFile(new File("lottery_log_" +
                new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (java.io.FileWriter writer = new java.io.FileWriter(fileToSave)) {
                writer.write(logArea.getText());
                addLog("日志已导出到: " + fileToSave.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "日志导出成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                addLog("日志导出失败: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "日志导出失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 刷新所有面板
     */
    private void refreshAll() {
        if (drawPanel != null) {
            drawPanel.refreshDisplay();
        }
        updateRecordArea();
        addLog("系统已刷新 - " + new java.util.Date());
        JOptionPane.showMessageDialog(this, "刷新完成！", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 退出登录方法
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要退出管理员登录吗？",
                "确认退出",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // 关闭当前管理员窗口
            JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            currentFrame.dispose();

            // 重新打开登录窗口（MainFrame）
            SwingUtilities.invokeLater(() -> {
                MainFrame loginFrame = new MainFrame();
                loginFrame.setVisible(true);
            });
        }
    }

    /**
     * 刷新显示
     */
    public void refreshDisplay() {
        updateRecordArea();
    }
}