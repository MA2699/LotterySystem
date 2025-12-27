package com.jit.edu.lottery.service;

import com.jit.edu.lottery.dao.UserDAO;
import com.jit.edu.lottery.dao.WinningRecordDAO;
import com.jit.edu.lottery.model.User;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private UserDAO userDAO;                // 用户数据访问对象
    private WinningRecordDAO winningRecordDAO;  // 中奖记录数据访问对象
    private LotteryService lotteryService;      // 抽奖服务对象

    // 用于存储用户通知的临时Map
    private Map<Integer, String> userNotifications = new HashMap<>();

    // =============== 新增：保存当前登录用户 ===============
    private static int currentUserId = 0;      // 当前登录用户的ID
    private static String currentUsername = ""; // 当前登录用户的用户名
    // ==================================================

    public UserService() {
        this.userDAO = new UserDAO();
        this.winningRecordDAO = new WinningRecordDAO();
        // 移除这里的LotteryService实例化，改为延迟初始化
        // this.lotteryService = new LotteryService(); // 删除这行

        // 初始化用户表
        userDAO.createTable();
    }

    // =============== 新增：当前用户相关方法 ===============
    /**
     * 获取当前登录用户的ID
     */
    public static int getCurrentUserId() {
        return currentUserId;
    }

    /**
     * 获取当前登录用户的用户名
     */
    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * 设置当前登录用户
     */
    public static void setCurrentUser(int userId, String username) {
        currentUserId = userId;
        currentUsername = username;
        System.out.println("当前用户已设置为: ID=" + userId + ", 用户名=" + username);
    }

    /**
     * 清空当前用户（用于退出登录）
     */
    public static void clearCurrentUser() {
        currentUserId = 0;
        currentUsername = "";
        System.out.println("当前用户已清空");
    }

    /**
     * 检查是否有用户登录
     */
    public static boolean isUserLoggedIn() {
        return currentUserId > 0;  // ID大于0表示有用户登录
    }
    // ==================================================

    // 延迟初始化LotteryService
    private LotteryService getLotteryService() {
        if (lotteryService == null) {
            lotteryService = new LotteryService();  // 需要时才创建
        }
        return lotteryService;
    }

    // 用户登录 - 修改：登录成功后自动设置当前用户
    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            // =============== 新增：保存当前用户 ===============
            setCurrentUser(user.getId(), user.getUsername());
            // ==================================================

            // 检查是否有中奖通知 - 使用延迟初始化
            String notification = getLotteryService().checkWinningNotification(user.getId());
            if (notification != null) {
                userNotifications.put(user.getId(), notification);  // 保存通知
            }
            return user;  // 登录成功，返回用户对象
        }
        return null;  // 登录失败
    }

    // 用户注册
    public boolean register(String username, String password, String phone) {
        // 检查用户名是否已存在
        if (userDAO.isUsernameExists(username)) {
            return false;  // 用户名已存在
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setBalance(100.0); // 新用户赠送100元

        int result = userDAO.addUser(user);
        return result > 0;  // 返回注册是否成功
    }

    // 使用User对象注册（重载方法）
    public boolean register(User user) {
        return register(user.getUsername(), user.getPassword(), user.getPhone());
    }

    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }

    // 获取用户通知
    public String getUserNotification(int userId) {
        return userNotifications.getOrDefault(userId, null);  // 获取或返回null
    }

    // 清除用户通知
    public void clearUserNotification(int userId) {
        userNotifications.remove(userId);  // 移除通知
    }

    // 用户充值 - 修复的方法
    public boolean recharge(int userId, double amount) {
        System.out.println("\n=== UserService.recharge() 开始 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("充值金额: " + amount);

        if (amount <= 0) {
            System.out.println("❌ 充值金额必须大于0");
            return false;
        }

        // 先检查用户是否存在
        User user = getUserById(userId);
        if (user == null) {
            System.out.println("❌ 用户不存在，ID: " + userId);
            return false;
        }

        System.out.println("用户存在: " + user.getUsername());
        System.out.println("当前余额: " + user.getBalance());

        // 执行充值
        boolean success = userDAO.updateBalance(userId, amount);

        if (success) {
            double newBalance = getUserBalance(userId);
            System.out.println("✅ 充值成功！");
            System.out.println("充值前余额: " + user.getBalance());
            System.out.println("充值金额: " + amount);
            System.out.println("充值后余额: " + newBalance);
        } else {
            System.out.println("❌ 充值失败，数据库操作返回false");
        }

        return success;  // 返回充值结果
    }

    // =============== 新增：使用当前用户ID的充值方法 ===============
    /**
     * 为当前登录用户充值（更方便的方法）
     */
    public boolean rechargeForCurrentUser(double amount) {
        if (!isUserLoggedIn()) {
            System.out.println("❌ 没有用户登录，无法充值");
            return false;
        }
        return recharge(currentUserId, amount);  // 使用当前用户ID充值
    }
    // ==================================================

    // 获取用户信息
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    // 获取用户余额
    public double getUserBalance(int userId) {
        User user = getUserById(userId);
        return user != null ? user.getBalance() : 0.0;  // 返回余额或0
    }

    // =============== 新增：获取当前用户余额 ===============
    /**
     * 获取当前登录用户的余额
     */
    public double getCurrentUserBalance() {
        if (!isUserLoggedIn()) {
            return 0.0;  // 未登录返回0
        }
        return getUserBalance(currentUserId);  // 获取当前用户余额
    }

    /**
     * 退出登录 - 新增方法
     */
    public static boolean logout() {
        if (!isUserLoggedIn()) {
            return false;  // 未登录无需退出
        }

        System.out.println("用户退出登录: " + currentUsername + "(ID:" + currentUserId + ")");

        // 清除当前用户信息
        clearCurrentUser();

        return true;  // 退出成功
    }

    /**
     * 获取当前用户信息（用于显示）
     */
    public static String getCurrentUserInfo() {
        if (!isUserLoggedIn()) {
            return "未登录";
        }
        return currentUsername + "(ID:" + currentUserId + ")";  // 格式化用户信息
    }
    // ==================================================

    // 更新用户余额
    public boolean updateUserBalance(int userId, double amount) {
        return userDAO.updateBalance(userId, amount);  // 委托给DAO
    }
}