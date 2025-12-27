package com.jit.edu.lottery.utils;

public class ConfigManagerTest {
    public static void main(String[] args) {
        System.out.println("测试配置管理器...\n");

        // 测试读取配置
        System.out.println("1. 读取配置值:");
        System.out.println("   数据库URL: " + ConfigManager.getDatabaseUrl());  // 获取数据库连接URL
        System.out.println("   彩票单价: " + ConfigManager.getTicketPrice());   // 获取每张彩票价格
        System.out.println("   特等奖奖金: " + ConfigManager.getSpecialPrize()); // 获取特等奖奖金金额
        System.out.println("   一等奖奖金: " + ConfigManager.getFirstPrize());  // 获取一等奖奖金金额
        System.out.println("   最大投注倍数: " + ConfigManager.getMaxBetCount()); // 获取最大投注倍数

        // 测试设置配置
        System.out.println("\n2. 测试设置配置值:");
        ConfigManager.setProperty("test.key", "test.value");  // 设置测试配置项
        System.out.println("   测试键值: " + ConfigManager.getProperty("test.key", "not found"));  // 读取测试配置项

        // 测试管理员验证
        System.out.println("\n3. 测试管理员验证:");
        boolean isAdmin = ConfigManager.isAdmin("admin", "admin123");  // 验证管理员账号
        System.out.println("   用户名admin, 密码admin123: " + (isAdmin ? "是管理员" : "不是管理员"));  // 输出验证结果

        isAdmin = ConfigManager.isAdmin("user", "user123");  // 验证普通用户账号
        System.out.println("   用户名user, 密码user123: " + (isAdmin ? "是管理员" : "不是管理员"));  // 输出验证结果

        // 测试默认值
        System.out.println("\n4. 测试默认值:");
        String notExist = ConfigManager.getProperty("not.exist.key", "默认值");  // 测试不存在的字符串配置
        System.out.println("   不存在的键: " + notExist);  // 应返回"默认值"

        int intValue = ConfigManager.getIntProperty("not.exist.int", 999);  // 测试不存在的整数配置
        System.out.println("   不存在的整数键: " + intValue);  // 应返回999

        double doubleValue = ConfigManager.getDoubleProperty("not.exist.double", 888.88);  // 测试不存在的浮点数配置
        System.out.println("   不存在的浮点数键: " + doubleValue);  // 应返回888.88

        System.out.println("\n✅ 配置管理器测试完成！");  // 测试完成提示
    }
}