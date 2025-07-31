package model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ChatbotConfig - Entity model cho bảng ChatbotConfigs trong database
 * Đại diện cho cấu hình của chatbot AI
 *
 * Các thuộc tính:
 * - configID: ID duy nhất của cấu hình
 * - configKey: Khóa cấu hình (ví dụ: "api_key", "model_name")
 * - configValue: Giá trị cấu hình
 * - description: Mô tả về cấu hình này
 * - isActive: Trạng thái hoạt động
 *
 * Sử dụng để:
 * - Lưu trữ các thông số cấu hình chatbot
 * - Quản lý API key, model name, temperature, etc.
 * - Bật/tắt các tính năng chatbot
 * - Tùy chỉnh hành vi của chatbot
 */
public class ChatbotConfig {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = ChatbotConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }
    
    public static String getApiUrl() {
        return properties.getProperty("chatbot.api.url");
    }
    
    public static String getApiKey() {
        return properties.getProperty("chatbot.api.key");
    }
    
    public static String getBotId() {
        return properties.getProperty("chatbot.bot.id");
    }
    
    public static String getAuthToken() {
        return properties.getProperty("chatbot.auth.token");
    }
    
    public static String getModelName() {
        return properties.getProperty("chatbot.model.name");
    }
}

