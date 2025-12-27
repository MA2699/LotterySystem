package com.jit.edu.lottery.utils;

import java.io.*;
import java.util.Properties;

/**
 * 配置文件管理器
 * 负责加载、保存和管理系统的配置信息
 */
public class ConfigManager {
    // 配置文件名称
    private static final String CONFIG_FILE = "config.properties";
    // 存储配置项的Properties对象
    private static Properties properties;

    // 静态代码块，类加载时初始化配置
    static {
        properties = new Properties();
        loadConfig();
    }

    /**
     * 加载配置文件
     * 如果配置文件不存在，则使用默认配置并保存
     */
    private static void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            // 配置文件不存在，使用默认值
            setDefaultProperties();
            saveConfig();
        } catch (IOException e) {
            e.printStackTrace();
            setDefaultProperties();
        }
    }

    /**
     * 设置默认配置项
     * 当配置文件不存在时调用
     */
    private static void setDefaultProperties() {
        properties.setProperty("database.url", "jdbc:mysql://localhost:3306/lottery_db?useSSL=false&serverTimezone=UTC");
        properties.setProperty("database.username", "root");
        properties.setProperty("database.password", "root");
        properties.setProperty("ticket.price", "2.0");
        properties.setProperty("prize.special", "5000000");
        properties.setProperty("prize.first", "500000");
        properties.setProperty("max.bet.count", "10");
        properties.setProperty("admin.username", "admin");
        properties.setProperty("admin.password", "admin123");
        properties.setProperty("user.init.balance", "100.0");
    }

    /**
     * 保存配置到文件
     */
    private static void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Lottery System Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取配置值
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取配置值，如果不存在则返回默认值
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 获取整数类型的配置值，如果不存在或格式错误则返回默认值
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * 获取浮点数类型的配置值，如果不存在或格式错误则返回默认值
     */
    public static double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    /**
     * 设置配置值并保存到文件
     */
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }

    // 获取彩票单价
    public static double getTicketPrice() {
        return getDoubleProperty("ticket.price", 2.0);
    }

    // 获取特等奖奖金
    public static double getSpecialPrize() {
        return getDoubleProperty("prize.special", 5000000.0);
    }

    // 获取一等奖奖金
    public static double getFirstPrize() {
        return getDoubleProperty("prize.first", 500000.0);
    }

    // 获取最大投注倍数
    public static int getMaxBetCount() {
        return getIntProperty("max.bet.count", 10);
    }

    // 获取数据库URL
    public static String getDatabaseUrl() {
        return getProperty("database.url", "jdbc:mysql://localhost:3306/lottery_db?useSSL=false&serverTimezone=UTC");
    }

    /**
     * 检查用户是否为管理员
     */
    public static boolean isAdmin(String username, String password) {
        String adminUser = getProperty("admin.username", "admin");
        String adminPass = getProperty("admin.password", "admin123");
        return adminUser.equals(username) && adminPass.equals(password);
    }
}