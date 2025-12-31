package com.jit.edu.lottery.model;

public class User {
    private int id;           // 用户唯一ID，数据库主键
    private String username;  // 用户名，用于登录和显示
    private String password;  // 密码，用于身份验证
    private double balance;   // 账户余额，单位：元
    private String phone;     // 手机号码，用于联系

    public User() {}  // 默认构造方法，创建空的用户对象

    // 带参数的构造方法
    public User(int id, String username, String password, double balance, String phone) {
        this.id = id;           // 设置用户ID
        this.username = username;  // 设置用户名
        this.password = password;  // 设置密码
        this.balance = balance;    // 设置账户余额
        this.phone = phone;        // 设置手机号码
    }

    // Getters and Setters

    public int getId() { return id; }  // 获取用户ID
    public void setId(int id) { this.id = id; }  // 设置用户ID

    public String getUsername() { return username; }  // 获取用户名
    public void setUsername(String username) { this.username = username; }  // 设置用户名

    public String getPassword() { return password; }  // 获取密码
    public void setPassword(String password) { this.password = password; }  // 设置密码

    public double getBalance() { return balance; }  // 获取账户余额
    public void setBalance(double balance) { this.balance = balance; }  // 设置账户余额

    public String getPhone() { return phone; }  // 获取手机号码
    public void setPhone(String phone) { this.phone = phone; }  // 设置手机号码

    // 重写toString方法，便于调试和日志输出
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", balance=" + balance +
                ", phone='" + phone + '\'' +
                '}';
    }
}
