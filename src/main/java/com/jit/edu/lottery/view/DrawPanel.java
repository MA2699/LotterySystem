package com.jit.edu.lottery.view;

import com.jit.edu.lottery.service.LotteryService;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * 抽奖面板类
 * 显示抽奖动画和结果，提供开始/停止抽奖和立即开奖功能
 */
public class DrawPanel extends JPanel {
    // 彩票服务对象，处理抽奖逻辑
    private LotteryService lotteryService;
    // 显示抽奖号码的标签数组
    private JLabel[] numberLabels;
    // 显示开奖结果的文本区域
    private JTextArea resultArea;
    // 开始和停止按钮
    private JButton startButton;
    private JButton stopButton;

    // 预设号码相关变量
    private javax.swing.Timer drawTimer;
    private boolean isDrawing = false;
    private List<Integer> finalNumbers = null;
    private Random random = new Random();
    private AdminPanel adminPanel;

    // 动画控制变量
    private int animationCount = 0;
    private final int ANIMATION_STEPS = 30;

    /**
     * 构造函数，初始化抽奖面板
     */
    public DrawPanel() {
        lotteryService = new LotteryService();
        initUI();
    }

    /**
     * 设置AdminPanel引用，用于获取预设号码
     * @param adminPanel 管理员面板对象
     */
    public void setAdminPanel(AdminPanel adminPanel) {
        this.adminPanel = adminPanel;
    }

    /**
     * 初始化用户界面
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题标签
        JLabel titleLabel = new JLabel("🎰 彩票抽奖系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // 号码显示面板
        JPanel numberPanel = createNumberPanel();
        add(numberPanel, BorderLayout.CENTER);

        // 底部面板（按钮和结果区域）
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 创建号码显示面板
     */
    private JPanel createNumberPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 9, 15, 15));
        panel.setBorder(BorderFactory.createTitledBorder("抽奖号码"));

        numberLabels = new JLabel[7];
        for (int i = 0; i < 7; i++) {
            JPanel singlePanel = new JPanel(new BorderLayout());
            singlePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            singlePanel.setBackground(Color.WHITE);

            // 号码索引标签
            JLabel indexLabel = new JLabel("号码 " + (i + 1), SwingConstants.CENTER);
            indexLabel.setFont(new Font("宋体", Font.PLAIN, 12));
            indexLabel.setForeground(Color.DARK_GRAY);

            // 号码显示标签
            numberLabels[i] = new JLabel("00", SwingConstants.CENTER);
            numberLabels[i].setFont(new Font("Arial", Font.BOLD, 36));
            numberLabels[i].setForeground(Color.RED);
            numberLabels[i].setBackground(Color.YELLOW);
            numberLabels[i].setOpaque(true);
            numberLabels[i].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

            singlePanel.add(indexLabel, BorderLayout.NORTH);
            singlePanel.add(numberLabels[i], BorderLayout.CENTER);
            panel.add(singlePanel);
        }

        // 添加占位符，使布局更美观
        JLabel empty1 = new JLabel();
        JLabel empty2 = new JLabel();
        panel.add(empty1);
        panel.add(empty2);

        return panel;
    }

    /**
     * 创建底部面板（包含按钮和结果区域）
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // 开始抽奖按钮
        startButton = new JButton("开始抽奖");
        startButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        startButton.setBackground(new Color(76, 175, 80));
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(150, 50));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startDraw();
            }
        });

        // 停止按钮
        stopButton = new JButton("停止");
        stopButton.setFont(new Font("微软雅黑", Font.BOLD, 18));
        stopButton.setBackground(new Color(244, 67, 54));
        stopButton.setForeground(Color.WHITE);
        stopButton.setPreferredSize(new Dimension(150, 50));
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopDraw();
            }
        });

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        // 结果文本区域
        resultArea = new JTextArea(10, 60);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("宋体", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createTitledBorder("开奖结果"));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(800, 250));

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 开始抽奖动画
     */
    private void startDraw() {
        // 检查是否使用预设号码
        boolean usePreset = false;
        List<Integer> presetNumbers = null;

        if (adminPanel != null) {
            usePreset = adminPanel.shouldUsePreset();
            if (usePreset) {
                presetNumbers = adminPanel.getPresetNumbersForDraw();
            }
        }

        // 更新按钮状态
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        isDrawing = true;
        animationCount = 0;

        // 清空结果区域并显示开始信息
        resultArea.setText("🎰 抽奖开始！号码滚动中...\n");

        if (usePreset && presetNumbers != null) {
            resultArea.append("📝 使用预设号码模式\n");
            finalNumbers = presetNumbers;
        } else {
            resultArea.append("🎲 使用随机号码模式\n");
            // 生成随机号码作为最终开奖结果
            finalNumbers = generateRandomNumbers();
        }

        // 启动动画计时器，每100ms更新一次显示
        drawTimer = new javax.swing.Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (animationCount < ANIMATION_STEPS) {
                    // 动画阶段：显示随机滚动的号码
                    for (int i = 0; i < 7; i++) {
                        int randomNum = random.nextInt(36) + 1;
                        numberLabels[i].setText(String.format("%02d", randomNum));
                    }
                    animationCount++;
                } else {
                    // 动画结束：显示最终号码
                    drawTimer.stop();
                    for (int i = 0; i < 7; i++) {
                        numberLabels[i].setText(String.format("%02d", finalNumbers.get(i)));
                    }

                    // 恢复按钮状态
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    isDrawing = false;

                    // 执行开奖逻辑
                    performDraw(finalNumbers);
                }
            }
        });

        drawTimer.start();
    }

    /**
     * 生成7个随机号码（1-36，允许重复）
     */
    private List<Integer> generateRandomNumbers() {
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();

        // 生成7个随机号码（允许重复）
        for (int i = 0; i < 7; i++) {
            numbers.add(rand.nextInt(36) + 1);
        }

        // 保持原始顺序（位置对应），不排序
        return numbers;
    }

    /**
     * 手动停止抽奖动画
     */
    private void stopDraw() {
        if (drawTimer != null && drawTimer.isRunning()) {
            drawTimer.stop();

            // 立即显示最终号码
            if (finalNumbers != null) {
                for (int i = 0; i < 7; i++) {
                    numberLabels[i].setText(String.format("%02d", finalNumbers.get(i)));
                }
            }

            // 更新按钮状态
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            isDrawing = false;

            // 执行开奖逻辑
            if (finalNumbers != null) {
                performDraw(finalNumbers);
            }
        }
    }

    /**
     * 执行开奖逻辑并显示结果
     */
    private void performDraw(List<Integer> winningNumbers) {
        // 记录开奖结果
        resultArea.append("\n🎉 抽奖完成！\n");
        resultArea.append("--- 开奖结果 ---\n");
        resultArea.append("中奖号码: " + winningNumbers + "\n");

        try {
            // 调用服务层执行开奖计算
            List<Integer> result = lotteryService.stopDrawRolling();

            if (result != null && result.equals(winningNumbers)) {
                resultArea.append("✅ 开奖成功！\n");
                resultArea.append("已计算中奖结果并发放奖金\n");
            } else {
                resultArea.append("⚠️ 开奖过程完成，但返回结果不一致\n");
            }

            // 显示成功消息对话框
            JOptionPane.showMessageDialog(this,
                    "🎉 抽奖完成！\n中奖号码：" + winningNumbers + "\n\n请查看下方详细结果",
                    "抽奖结果",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            resultArea.append("❌ 开奖过程出错: " + e.getMessage() + "\n");
            JOptionPane.showMessageDialog(this,
                    "开奖过程出错: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * 刷新显示，重置所有状态
     */
    public void refreshDisplay() {
        // 停止动画计时器
        if (drawTimer != null && drawTimer.isRunning()) {
            drawTimer.stop();
        }

        // 重置状态变量
        isDrawing = false;
        finalNumbers = null;
        animationCount = 0;

        // 重置按钮状态
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        // 重置号码显示为"00"
        for (JLabel label : numberLabels) {
            label.setText("00");
        }

        // 清空结果区域并显示当前模式
        resultArea.setText("就绪...\n点击'开始抽奖'按钮开始抽奖\n\n");
        if (adminPanel != null && adminPanel.shouldUsePreset()) {
            resultArea.append("📝 当前模式：预设号码测试模式\n");
        } else {
            resultArea.append("🎲 当前模式：随机号码模式\n");
        }
    }

    /**
     * 检查是否正在抽奖中
     */
    public boolean isDrawing() {
        return isDrawing;
    }

    /**
     * 立即开奖（不经过动画过程，不检查重复号码）
     */
    public void drawImmediately(List<Integer> numbers) {
        // 停止动画计时器
        if (drawTimer != null && drawTimer.isRunning()) {
            drawTimer.stop();
        }

        // 验证号码数量
        if (numbers == null || numbers.size() != 7) {
            JOptionPane.showMessageDialog(this,
                    "号码数量必须为7个！",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 不再检查重复号码，允许重复
        // 不再排序，保持原始顺序（位置对应）

        // 直接显示最终号码
        for (int i = 0; i < 7; i++) {
            numberLabels[i].setText(String.format("%02d", numbers.get(i)));
        }

        // 调用服务层的立即开奖方法
        List<Integer> result = lotteryService.performImmediateDraw(numbers, resultArea);

        if (result != null) {
            // 更新状态
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            isDrawing = false;
            animationCount = ANIMATION_STEPS;

            // 记录日志
            if (adminPanel != null) {
                adminPanel.addLog("立即开奖成功，号码：" + numbers);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "立即开奖失败！",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 获取结果文本区域，供外部调用
     */
    public JTextArea getResultArea() {
        return resultArea;
    }
}
