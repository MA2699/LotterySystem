package com.jit.edu.lottery.utils;

import java.sql.*;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== 数据库测试 ===\n");

        // 测试连接
        System.out.println("1. 测试数据库连接:");
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();  // 获取数据库连接
            if (conn != null && !conn.isClosed()) {
                System.out.println("   ✅ 数据库连接成功");
                System.out.println("      数据库: " + conn.getMetaData().getURL());        // 输出数据库URL
                System.out.println("      驱动: " + conn.getMetaData().getDriverName());   // 输出驱动名称
                System.out.println("      版本: " + conn.getMetaData().getDriverVersion()); // 输出驱动版本
            }
        } catch (SQLException e) {
            System.out.println("   ❌ 数据库连接失败: " + e.getMessage());  // 连接失败提示
        }

        // 测试创建表
        System.out.println("\n2. 测试创建表:");
        try {
            if (conn != null) {
                createTestTables(conn);  // 创建测试表
                System.out.println("   ✅ 测试表创建成功");
            }
        } catch (SQLException e) {
            System.out.println("   ❌ 测试表创建失败: " + e.getMessage());  // 创建失败提示
        }

        // 测试插入数据
        System.out.println("\n3. 测试数据操作:");
        try {
            if (conn != null) {
                testDataOperations(conn);  // 测试数据插入操作
                System.out.println("   ✅ 数据操作测试成功");
            }
        } catch (SQLException e) {
            System.out.println("   ❌ 数据操作测试失败: " + e.getMessage());  // 操作失败提示
        }

        // 测试查询
        System.out.println("\n4. 测试数据查询:");
        try {
            if (conn != null) {
                testDataQuery(conn);  // 测试数据查询操作
                System.out.println("   ✅ 数据查询测试成功");
            }
        } catch (SQLException e) {
            System.out.println("   ❌ 数据查询测试失败: " + e.getMessage());  // 查询失败提示
        }

        // 清理测试表
        System.out.println("\n5. 清理测试环境:");
        try {
            if (conn != null) {
                cleanupTestTables(conn);  // 清理测试表
                System.out.println("   ✅ 测试环境清理成功");
            }
        } catch (SQLException e) {
            System.out.println("   ⚠️ 测试环境清理失败: " + e.getMessage());  // 清理失败提示
        }

        // 关闭连接
        System.out.println("\n6. 关闭数据库连接:");
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();  // 关闭数据库连接
                System.out.println("   ✅ 数据库连接已关闭");
            }
        } catch (SQLException e) {
            System.out.println("   ⚠️ 关闭连接失败: " + e.getMessage());  // 关闭失败提示
        }

        System.out.println("\n=== 数据库测试完成 ===");  // 测试完成提示
    }

    private static void createTestTables(Connection conn) throws SQLException {
        String createTestTable = "CREATE TABLE IF NOT EXISTS test_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +  // 自增主键
                "name TEXT NOT NULL," +                    // 名称字段，不能为空
                "value INTEGER," +                         // 数值字段
                "created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";  // 创建时间，默认当前时间

        Statement stmt = conn.createStatement();
        stmt.execute(createTestTable);  // 执行建表SQL
        stmt.close();  // 关闭Statement
    }

    private static void testDataOperations(Connection conn) throws SQLException {
        // 插入测试数据
        String insertSQL = "INSERT INTO test_table (name, value) VALUES (?, ?)";  // 插入SQL语句
        PreparedStatement pstmt = conn.prepareStatement(insertSQL);

        pstmt.setString(1, "测试数据1");  // 设置第一个参数：名称
        pstmt.setInt(2, 100);            // 设置第二个参数：数值
        pstmt.executeUpdate();            // 执行插入操作

        pstmt.setString(1, "测试数据2");  // 设置第二个数据
        pstmt.setInt(2, 200);            // 设置第二个数值
        pstmt.executeUpdate();            // 执行插入操作

        pstmt.close();  // 关闭PreparedStatement
    }

    private static void testDataQuery(Connection conn) throws SQLException {
        String querySQL = "SELECT * FROM test_table ORDER BY id";  // 查询所有数据按ID排序
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(querySQL);  // 执行查询

        System.out.println("      ID | 名称         | 值   | 创建时间");  // 表头
        System.out.println("      ------------------------------------");  // 分隔线
        while (rs.next()) {  // 遍历查询结果
            int id = rs.getInt("id");                 // 获取ID
            String name = rs.getString("name");       // 获取名称
            int value = rs.getInt("value");           // 获取数值
            Timestamp created = rs.getTimestamp("created_time");  // 获取创建时间

            System.out.printf("      %2d | %-10s | %4d | %s\n",  // 格式化输出
                    id, name, value, created.toString());
        }

        rs.close();   // 关闭ResultSet
        stmt.close(); // 关闭Statement
    }

    private static void cleanupTestTables(Connection conn) throws SQLException {
        String dropSQL = "DROP TABLE IF EXISTS test_table";  // 删除表的SQL语句
        Statement stmt = conn.createStatement();
        stmt.execute(dropSQL);  // 执行删除操作
        stmt.close();           // 关闭Statement
    }
}