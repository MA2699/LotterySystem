package com.jit.edu.lottery.model;

import java.util.Date;

public class WinningRecord {
    private int id;              // 中奖记录ID，数据库主键
    private int userId;          // 中奖用户ID
    private int drawId;          // 开奖期号ID
    private int ticketId;        // 中奖彩票ID
    private String prizeLevel;   // 中奖等级："特等奖"、"一等奖"等
    private double prizeAmount;  // 奖金金额，单位：元
    private int matchCount;      // 匹配号码数量（0-7）
    private Date notifyTime;     // 通知时间，null表示未通知
    private boolean isNotified;  // 是否已通知用户

    public WinningRecord() {}  // 默认构造方法，创建空的中奖记录对象

    // 带参数的构造方法
    public WinningRecord(int id, int userId, int drawId, int ticketId,
                         String prizeLevel, double prizeAmount,
                         int matchCount, Date notifyTime, boolean isNotified) {
        this.id = id;                 // 设置记录ID
        this.userId = userId;         // 设置用户ID
        this.drawId = drawId;         // 设置开奖期号ID
        this.ticketId = ticketId;     // 设置彩票ID
        this.prizeLevel = prizeLevel; // 设置中奖等级
        this.prizeAmount = prizeAmount; // 设置奖金金额
        this.matchCount = matchCount; // 设置匹配号码数量
        this.notifyTime = notifyTime; // 设置通知时间
        this.isNotified = isNotified; // 设置是否已通知
    }

    // Getters and Setters

    public int getId() { return id; }  // 获取记录ID
    public void setId(int id) { this.id = id; }  // 设置记录ID

    public int getUserId() { return userId; }  // 获取用户ID
    public void setUserId(int userId) { this.userId = userId; }  // 设置用户ID

    public int getDrawId() { return drawId; }  // 获取开奖期号ID
    public void setDrawId(int drawId) { this.drawId = drawId; }  // 设置开奖期号ID

    public int getTicketId() { return ticketId; }  // 获取彩票ID
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }  // 设置彩票ID

    public String getPrizeLevel() { return prizeLevel; }  // 获取中奖等级
    public void setPrizeLevel(String prizeLevel) { this.prizeLevel = prizeLevel; }  // 设置中奖等级

    public double getPrizeAmount() { return prizeAmount; }  // 获取奖金金额
    public void setPrizeAmount(double prizeAmount) { this.prizeAmount = prizeAmount; }  // 设置奖金金额

    public int getMatchCount() { return matchCount; }  // 获取匹配号码数量
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }  // 设置匹配号码数量

    public Date getNotifyTime() { return notifyTime; }  // 获取通知时间
    public void setNotifyTime(Date notifyTime) { this.notifyTime = notifyTime; }  // 设置通知时间

    public boolean isNotified() { return isNotified; }  // 获取是否已通知
    public void setNotified(boolean notified) { isNotified = notified; }  // 设置是否已通知

    // 重写toString方法，便于调试和日志输出
    @Override
    public String toString() {
        return "WinningRecord{" +
                "id=" + id +
                ", userId=" + userId +
                ", drawId=" + drawId +
                ", ticketId=" + ticketId +
                ", prizeLevel='" + prizeLevel + '\'' +
                ", prizeAmount=" + prizeAmount +
                ", matchCount=" + matchCount +
                ", notifyTime=" + notifyTime +
                ", isNotified=" + isNotified +
                '}';
    }
}