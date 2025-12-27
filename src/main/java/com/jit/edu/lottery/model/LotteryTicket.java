// LotteryTicket.java
package com.jit.edu.lottery.model;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
public class LotteryTicket {
    private int id;                // 彩票唯一ID
    private int userId;            // 购买用户ID
    private List<Integer> numbers; // 选中的7个号码（1-36）
    private int betCount;          // 投注倍数（默认为1）
    private double amount;         // 购买金额（彩票单价×倍数）
    private Date purchaseTime;     // 购买时间
    private int drawId;            // 所属开奖期号（0表示未开奖）
    private String ticketStatus;   // 彩票状态："已购买"、"已开奖"、"已中奖"等

    // 构造方法
    public LotteryTicket() {}  // 默认构造方法，创建空的彩票对象

    // Getters and Setters

    public int getId() { return id; }  // 获取彩票ID
    public void setId(int id) { this.id = id; }  // 设置彩票ID

    public int getUserId() { return userId; }  // 获取用户ID
    public void setUserId(int userId) { this.userId = userId; }  // 设置用户ID

    public List<Integer> getNumbers() { return numbers; }  // 获取号码列表
    public void setNumbers(List<Integer> numbers) { this.numbers = numbers; }  // 设置号码列表

    public int getBetCount() { return betCount; }  // 获取投注倍数
    public void setBetCount(int betCount) { this.betCount = betCount; }  // 设置投注倍数

    public double getAmount() { return amount; }  // 获取购买金额
    public void setAmount(double amount) { this.amount = amount; }  // 设置购买金额

    public Date getPurchaseTime() { return purchaseTime; }  // 获取购买时间
    public void setPurchaseTime(Date purchaseTime) { this.purchaseTime = purchaseTime; }  // 设置购买时间

    public int getDrawId() { return drawId; }  // 获取开奖期号
    public void setDrawId(int drawId) { this.drawId = drawId; }  // 设置开奖期号

    public String getTicketStatus() { return ticketStatus; }  // 获取彩票状态
    public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }  // 设置彩票状态

    // 将号码列表转为字符串存储（用于数据库存储）
    public String getNumbersString() {
        if (numbers == null || numbers.isEmpty()) {
            return "";  // 空号码列表返回空字符串
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numbers.size(); i++) {
            if (i > 0) sb.append(",");  // 数字间用逗号分隔
            sb.append(numbers.get(i));   // 添加号码
        }
        return sb.toString();  // 返回格式化后的字符串
    }

    // 从字符串设置号码（用于从数据库读取）
    public void setNumbersFromString(String numbersStr) {
        if (numbersStr != null && !numbersStr.trim().isEmpty()) {
            List<Integer> numList = new ArrayList<>();
            String[] parts = numbersStr.split(",");  // 按逗号分割
            for (String part : parts) {
                try {
                    numList.add(Integer.parseInt(part.trim()));  // 转换为整数
                } catch (NumberFormatException e) {
                    // 忽略格式错误的数字，打印错误信息
                    System.err.println("号码格式错误: " + part);
                }
            }
            this.numbers = numList;  // 设置转换后的号码列表
        } else {
            this.numbers = new ArrayList<>();  // 空字符串时创建空列表
        }
    }

}