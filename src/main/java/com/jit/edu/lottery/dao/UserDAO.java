package com.jit.edu.lottery.dao;

import com.jit.edu.lottery.model.User;
import com.jit.edu.lottery.utils.DatabaseUtil;
import java.sql.*;

/**
 * UserDAO（用户数据访问对象）类
 * 负责处理与用户表相关的所有数据库操作
 */
public class UserDAO {

    /**
     * 创建用户表的方法
     * 在数据库中创建一个名为'user'的表（如果不存在）
     * 使用MySQL语法，包含用户的基本信息字段
     */
    public void createTable() {
        // 定义创建用户表的SQL语句，使用反引号避免关键字冲突
        String sql = "CREATE TABLE IF NOT EXISTS `user` (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +  // 主键，自增
                "username VARCHAR(50) UNIQUE NOT NULL," +  // 用户名，唯一且非空
                "`password` VARCHAR(100) NOT NULL," +  // 密码字段，使用反引号因为password是MySQL关键字
                "balance DECIMAL(10,2) DEFAULT 0.0," +  // 余额，默认0.0
                "phone VARCHAR(20)," +  // 手机号码
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +  // 创建时间，默认当前时间
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";  // 使用InnoDB引擎和utf8mb4字符集

        // 使用try-with-resources确保连接和语句正确关闭
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            // 执行建表SQL语句
            stmt.execute(sql);
        } catch (SQLException e) {
            // 打印异常堆栈信息，便于调试
            e.printStackTrace();
        }
    }

    /**
     * 添加新用户到数据库
     *
     * @param user 包含用户信息的User对象
     * @return 新插入用户的ID，如果失败返回-1
     */
    public int addUser(User user) {
        // 插入用户的SQL语句
        String sql = "INSERT INTO `user` (username, `password`, balance, phone) VALUES (?, ?, ?, ?)";

        // 使用try-with-resources确保资源正确释放
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 设置SQL语句的参数
            pstmt.setString(1, user.getUsername());      // 用户名
            pstmt.setString(2, user.getPassword());      // 密码
            pstmt.setDouble(3, user.getBalance());       // 余额
            pstmt.setString(4, user.getPhone());         // 手机号

            // 执行插入操作并获取受影响的行数
            int affectedRows = pstmt.executeUpdate();

            // 如果插入成功（受影响行数大于0）
            if (affectedRows > 0) {
                // 获取自动生成的主键（用户ID）
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);  // 返回生成的新用户ID
                }
            }

        } catch (SQLException e) {
            // 打印异常堆栈信息
            e.printStackTrace();
        }
        return -1;  // 插入失败，返回-1
    }

    /**
     * 根据用户ID从数据库获取用户信息
     *
     * @param id 要查询的用户ID
     * @return 对应的User对象，如果不存在返回null
     */
    public User getUserById(int id) {
        // 按ID查询用户的SQL语句
        String sql = "SELECT * FROM `user` WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置查询参数
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            // 如果查询到结果
            if (rs.next()) {
                // 从ResultSet中提取用户数据并创建User对象
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // 未找到用户，返回null
    }

    /**
     * 根据用户名从数据库获取用户信息
     *
     * @param username 要查询的用户名
     * @return 对应的User对象，如果不存在返回null
     */
    public User getUserByUsername(String username) {
        // 按用户名查询用户的SQL语句
        String sql = "SELECT * FROM `user` WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置查询参数
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            // 如果查询到结果
            if (rs.next()) {
                // 从ResultSet中提取用户数据并创建User对象
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // 未找到用户，返回null
    }

    /**
     * 检查用户名是否已存在于数据库中
     *
     * @param username 要检查的用户名
     * @return true-用户名已存在，false-用户名不存在
     */
    public boolean isUsernameExists(String username) {
        // 通过查询用户是否存在来判断用户名是否已存在
        return getUserByUsername(username) != null;
    }

    /**
     * 更新用户余额
     * 可以为正数（充值/中奖）或负数（扣款购买）
     *
     * @param userId 要更新余额的用户ID
     * @param amount 要变动的金额，正数表示增加，负数表示减少
     * @return true-更新成功，false-更新失败
     */
    public boolean updateBalance(int userId, double amount) {
        // 打印调试信息
        System.out.println("\n=== UserDAO.updateBalance() ===");
        System.out.println("用户ID: " + userId);
        System.out.println("变动金额: " + amount);
        System.out.println("操作: " + (amount > 0 ? "充值/奖励" : "扣款"));

        // 更新用户余额的SQL语句，使用余额累加的方式
        String sql = "UPDATE `user` SET balance = balance + ? WHERE id = ?";

        System.out.println("SQL: " + sql);
        System.out.println("参数: balance + " + amount + ", id = " + userId);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置SQL语句的参数
            pstmt.setDouble(1, amount);  // 变动金额
            pstmt.setInt(2, userId);     // 用户ID

            System.out.println("执行数据库更新...");
            // 执行更新操作并获取受影响的行数
            int affectedRows = pstmt.executeUpdate();
            System.out.println("影响行数: " + affectedRows);

            // 如果有受影响的行，表示更新成功
            if (affectedRows > 0) {
                System.out.println("✅ 余额更新成功");

                // 验证更新后的余额
                String checkSql = "SELECT balance FROM `user` WHERE id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, userId);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        double newBalance = rs.getDouble("balance");
                        System.out.println("更新后余额: " + newBalance);
                    }
                }
            } else {
                // 没有受影响的行，表示更新失败
                System.out.println("❌ 余额更新失败，没有影响行");
                System.out.println("可能原因：");
                System.out.println("1. 用户ID " + userId + " 不存在");
                System.out.println("2. 数据库连接问题");
            }

            // 返回更新是否成功（受影响行数大于0表示成功）
            return affectedRows > 0;

        } catch (SQLException e) {
            // 打印异常信息
            System.err.println("❌ 数据库更新异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从ResultSet中提取用户数据并创建User对象
     * 这是一个私有辅助方法，用于封装从结果集到对象的转换逻辑
     *
     * @param rs 包含用户数据的ResultSet
     * @return 创建好的User对象
     * @throws SQLException 如果数据库操作出现异常
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        // 创建新的User对象
        User user = new User();

        // 从ResultSet中提取各个字段的值，并设置到User对象中
        user.setId(rs.getInt("id"));                 // 用户ID
        user.setUsername(rs.getString("username"));  // 用户名
        user.setPassword(rs.getString("password"));  // 密码
        user.setBalance(rs.getDouble("balance"));    // 余额
        user.setPhone(rs.getString("phone"));        // 手机号

        // 返回填充好的User对象
        return user;
    }
}