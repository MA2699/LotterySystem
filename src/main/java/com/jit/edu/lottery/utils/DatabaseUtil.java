package com.jit.edu.lottery.utils;

import java.sql.*;

/**
 * 数据库工具类
 * 提供数据库连接、初始化和基本操作功能
 */
public class DatabaseUtil {
    // 数据库连接对象
    private static Connection connection = null;

    // 静态代码块，加载MySQL JDBC驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC驱动未找到: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * 使用单例模式，如果连接不存在或已关闭则创建新连接
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = ConfigManager.getProperty("database.url");
            String username = ConfigManager.getProperty("database.username", "root");
            String password = ConfigManager.getProperty("database.password", "");

            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     * 释放连接资源
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化数据库
     * 创建数据库和所有必要的表结构
     */
    public static void initDatabase() {
        try {
            // 连接到MySQL服务器（不指定数据库）
            String baseUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String username = ConfigManager.getProperty("database.username", "root");
            String password = ConfigManager.getProperty("database.password", "");

            Connection conn = DriverManager.getConnection(baseUrl, username, password);
            Statement stmt = conn.createStatement();

            // 1. 创建数据库（如果不存在）
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS lottery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.executeUpdate("USE lottery_db");

            // 2. 创建用户表
            String createUserTable =
                    "CREATE TABLE IF NOT EXISTS `user` (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "username VARCHAR(50) UNIQUE NOT NULL," +
                            "`password` VARCHAR(100) NOT NULL," +
                            "balance DECIMAL(10,2) DEFAULT 0.00," +
                            "phone VARCHAR(20)," +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.executeUpdate(createUserTable);

            // 3. 创建彩票表 - 允许重复号码
            String createLotteryTable =
                    "CREATE TABLE IF NOT EXISTS lottery_ticket (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "user_id INT NOT NULL," +
                            "numbers VARCHAR(100) NOT NULL," +
                            "bet_count INT DEFAULT 1," +
                            "amount DECIMAL(10,2) NOT NULL," +
                            "draw_id INT," +
                            "ticket_status VARCHAR(20) DEFAULT '已购买'," +
                            "purchase_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (user_id) REFERENCES `user`(id)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.executeUpdate(createLotteryTable);

            // 4. 创建开奖记录表 - 允许重复号码
            String createDrawRecordTable =
                    "CREATE TABLE IF NOT EXISTS draw_record (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "draw_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "winning_numbers VARCHAR(100) NOT NULL," +
                            "draw_status VARCHAR(20) DEFAULT '未开奖'" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.executeUpdate(createDrawRecordTable);

            // 5. 创建中奖记录表
            String createWinningRecordTable =
                    "CREATE TABLE IF NOT EXISTS winning_record (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "user_id INT NOT NULL," +
                            "draw_id INT NOT NULL," +
                            "ticket_id INT NOT NULL," +
                            "prize_level VARCHAR(20) NOT NULL," +
                            "prize_amount DECIMAL(10,2) NOT NULL," +
                            "match_count INT NOT NULL," +
                            "notify_time TIMESTAMP NULL," +
                            "is_notified BOOLEAN DEFAULT FALSE," +
                            "FOREIGN KEY (user_id) REFERENCES `user`(id)," +
                            "FOREIGN KEY (draw_id) REFERENCES draw_record(id)," +
                            "FOREIGN KEY (ticket_id) REFERENCES lottery_ticket(id)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
            stmt.executeUpdate(createWinningRecordTable);

            stmt.close();
            conn.close();

            System.out.println("✅ MySQL数据库初始化成功！");

        } catch (SQLException e) {
            System.err.println("❌ 数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试数据库连接是否正常
     * 执行一个简单的查询来验证连接
     */
    public static boolean testConnected() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * 执行更新操作（INSERT、UPDATE、DELETE）
     * 使用try-with-resources确保资源自动关闭
     */
    public static void executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    /**
     * 执行查询操作
     * 返回ResultSet结果集，需要手动关闭资源
     */
    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    /**
     * 关闭数据库资源
     * 手动关闭ResultSet和Statement对象
     */
    public static void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}