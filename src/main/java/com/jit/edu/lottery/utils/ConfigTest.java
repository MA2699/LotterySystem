package com.jit.edu.lottery.utils;

/**
 * 配置系统测试类
 * 用于测试数据库工具和配置管理器的功能
 */
public class ConfigTest {
    /**
     * 主方法，执行配置系统的各项测试
     */
    public static void main(String[] args) {
        System.out.println("=== 配置系统测试 ===\n");

        // 测试数据库连接功能
        System.out.println("1. 测试数据库工具:");
        try {
            // 尝试获取数据库连接
            DatabaseUtil.getConnection();
            System.out.println("   ✅ 数据库连接成功");

            // 测试连接是否活跃
            if (DatabaseUtil.testConnected()) {
                System.out.println("   ✅ 数据库连接测试成功");
            } else {
                System.out.println("   ⚠️ 数据库连接测试失败");
            }
        } catch (Exception e) {
            System.out.println("   ❌ 数据库连接失败: " + e.getMessage());
        }

        // 测试配置管理器功能
        System.out.println("\n2. 测试配置管理器:");
        // 读取各种配置项
        System.out.println("   彩票单价: ¥" + ConfigManager.getTicketPrice());
        System.out.println("   特等奖奖金: ¥" + ConfigManager.getSpecialPrize());
        System.out.println("   一等奖奖金: ¥" + ConfigManager.getFirstPrize());
        System.out.println("   新用户初始余额: ¥" + ConfigManager.getDoubleProperty("user.init.balance", 100.0));

        // 测试配置文件的读写功能
        System.out.println("\n3. 测试配置文件操作:");
        // 创建测试键值对
        String testKey = "test.runtime.key";
        String testValue = "runtime_value_" + System.currentTimeMillis();

        // 写入配置并读取验证
        ConfigManager.setProperty(testKey, testValue);
        String readValue = ConfigManager.getProperty(testKey, "not_found");

        // 验证读写一致性
        if (testValue.equals(readValue)) {
            System.out.println("   ✅ 配置文件读写测试成功");
        } else {
            System.out.println("   ❌ 配置文件读写测试失败");
            System.out.println("      写入值: " + testValue);
            System.out.println("      读取值: " + readValue);
        }

        // 测试数据库初始化功能
        System.out.println("\n4. 测试数据库初始化:");
        try {
            // 初始化数据库表结构
            DatabaseUtil.initDatabase();
            System.out.println("   ✅ 数据库初始化成功");
        } catch (Exception e) {
            System.out.println("   ❌ 数据库初始化失败: " + e.getMessage());
        }

        System.out.println("\n=== 配置系统测试完成 ===");
    }
}
