package model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChatbotConfig {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = ChatbotConfig.class.getClassLoader().getResourceAsStream("controller/chatbot/config.properties")) {
            if (input == null) {
                System.out.println("Không thể tìm thấy file config.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            System.out.println("Lỗi khi đọc file config.properties: " + ex.getMessage());
        }
    }
    
    public static String get(String key) {
        return properties.getProperty(key);
    }
    
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    // Chatbot specific getters
    public static String getApiUrl() {
        return get("chatbot.api.url", "https://ai.ftes.vn/api/ai/rag_agent_template/stream");
    }
    
    public static String getApiKey() {
        return get("chatbot.api.key", "AIzaSyAH5Su96L-fRZBAzWH46VD5ICXyf9Jpihs");
    }
    
    public static String getBotId() {
        return get("chatbot.bot.id", "943bf25b42058b8882474ccb");
    }
    
    public static String getAuthToken() {
        return get("chatbot.auth.token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NjI5MDQwMjM5M2MzNTg5Y2QwMjEzMSJ9.8Ze_T_XpEWOI3Mi3pS5XgLHXw92YmqDZIsOJtRILvVw");
    }
    
    public static String getModelName() {
        return get("chatbot.model.name", "gemini-2.5-flash-preview-05-20");
    }
}

