package com.jit.edu.lottery.dao;

import com.jit.edu.lottery.model.DrawRecord;
import com.jit.edu.lottery.model.User;
import com.jit.edu.lottery.utils.DatabaseUtil;

import java.util.List;

/**
 * DAO层测试类
 * 用于测试数据访问对象(UserDAO, DrawRecordDAO)的功能
 */
public class DAOTest {

    /**
     * 主方法：执行DAO层测试
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("开始DAO层测试...");

        // 测试数据库连接
        testDatabaseConnection();

        // 测试UserDAO
        testUserDAO();

        // 测试DrawRecordDAO
        testDrawRecordDAO();

        System.out.println("DAO层测试完成！");
    }

    /**
     * 测试数据库连接功能
     * 验证能否成功连接到MySQL数据库
     */
    private static void testDatabaseConnection() {
        System.out.println("测试数据库连接...");
        try {
            DatabaseUtil.getConnection(); // 尝试获取数据库连接
            System.out.println("✅ 数据库连接成功！");
        } catch (Exception e) {
            System.out.println("❌ 数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 测试UserDAO功能
     * 包括添加用户和查询用户两个测试用例
     */
    private static void testUserDAO() {
        System.out.println("\n测试UserDAO...");
        UserDAO userDAO = new UserDAO();

        // 创建测试用户对象
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        testUser.setBalance(100.0);
        testUser.setPhone("13800138000");

        // 测试添加用户功能
        int userId = userDAO.addUser(testUser);
        if (userId > 0) {
            System.out.println("✅ 添加用户成功，用户ID: " + userId);
        } else {
            System.out.println("❌ 添加用户失败");
        }

        // 测试查询用户功能
        User foundUser = userDAO.getUserByUsername("testuser");
        if (foundUser != null) {
            System.out.println("✅ 查询用户成功: " + foundUser.getUsername());
        } else {
            System.out.println("❌ 查询用户失败");
        }
    }

    /**
     * 测试DrawRecordDAO功能
     * 包括获取最新开奖记录和统计开奖总数两个测试用例
     */
    private static void testDrawRecordDAO() {
        System.out.println("\n测试DrawRecordDAO...");
        DrawRecordDAO drawRecordDAO = new DrawRecordDAO();

        // 测试获取最新开奖记录功能
        DrawRecord latestDraw = drawRecordDAO.getLatestDraw();
        if (latestDraw != null) {
            System.out.println("✅ 获取最新开奖成功");
            System.out.println("   期号: " + latestDraw.getId());
            System.out.println("   号码: " + latestDraw.getWinningNumbers());
        } else {
            System.out.println("✅ 暂无开奖记录（正常情况）");
        }

        // 测试统计开奖总数功能
        int drawCount = drawRecordDAO.getDrawCount();
        System.out.println("✅ 开奖总期数: " + drawCount);
    }
}