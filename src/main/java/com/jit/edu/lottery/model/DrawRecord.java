package com.jit.edu.lottery.model;

import java.util.Date;
import java.util.List;

public class DrawRecord {
    private int id;                       // 开奖记录ID，数据库主键
    private Date drawTime;                // 开奖时间
    private List<Integer> winningNumbers; // 中奖号码列表（7个1-36的数字）
    private String drawStatus;            // 开奖状态："未开奖"、"已开奖"、"开奖中"
    private String drawType;              // 开奖类型：normal-正常开奖, immediate-立即开奖, preset-预设开奖

    public DrawRecord() {}  // 默认构造方法，创建空的开奖记录对象

    // 带参数的构造方法
    public DrawRecord(int id, Date drawTime, List<Integer> winningNumbers, String drawStatus) {
        this.id = id;                        // 设置开奖记录ID
        this.drawTime = drawTime;            // 设置开奖时间
        this.winningNumbers = winningNumbers; // 设置中奖号码列表
        this.drawStatus = drawStatus;        // 设置开奖状态
        this.drawType = "normal";            // 默认开奖类型为正常开奖
    }

    // Getter和Setter方法

    public int getId() { return id; }  // 获取开奖记录ID
    public void setId(int id) { this.id = id; }  // 设置开奖记录ID

    public Date getDrawTime() { return drawTime; }  // 获取开奖时间
    public void setDrawTime(Date drawTime) { this.drawTime = drawTime; }  // 设置开奖时间

    public List<Integer> getWinningNumbers() { return winningNumbers; }  // 获取中奖号码列表
    public void setWinningNumbers(List<Integer> winningNumbers) { this.winningNumbers = winningNumbers; }  // 设置中奖号码列表

    public String getDrawStatus() { return drawStatus; }  // 获取开奖状态
    public void setDrawStatus(String drawStatus) { this.drawStatus = drawStatus; }  // 设置开奖状态

    // 开奖类型相关方法
    public String getDrawType() { return drawType; }  // 获取开奖类型
    public void setDrawType(String drawType) { this.drawType = drawType; }  // 设置开奖类型

    // 从逗号分隔的字符串设置中奖号码
    public void setNumbersFromString(String numbersStr) {
        if (numbersStr != null && !numbersStr.isEmpty()) {
            List<Integer> numbers = new java.util.ArrayList<>();
            String[] parts = numbersStr.split(",");  // 按逗号分割字符串
            for (String part : parts) {
                try {
                    numbers.add(Integer.parseInt(part.trim()));  // 将字符串转换为整数
                } catch (NumberFormatException e) {
                    // 忽略格式错误的数字，不添加到列表
                }
            }
            this.winningNumbers = numbers;  // 设置转换后的号码列表
        }
    }

    // 重写toString方法，用于调试和日志输出
    @Override
    public String toString() {
        return "DrawRecord{" +
                "id=" + id +
                ", drawTime=" + drawTime +
                ", winningNumbers=" + winningNumbers +
                ", drawStatus='" + drawStatus + '\'' +
                ", drawType='" + drawType + '\'' +
                '}';
    }
}