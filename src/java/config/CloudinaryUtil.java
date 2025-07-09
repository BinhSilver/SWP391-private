package config;

import com.cloudinary.Cloudinary;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CloudinaryUtil {
    private static final String PROPERTIES_FILE = "/resources/cloud.properties";
    private static Cloudinary cloudinary = null;

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            try {
                // Load properties từ file
                Properties prop = new Properties();
                InputStream input = CloudinaryUtil.class.getResourceAsStream(PROPERTIES_FILE);
                
                if (input == null) {
                    throw new RuntimeException("Cannot find " + PROPERTIES_FILE + " in classpath");
                }
                
                prop.load(input);
                input.close();

                // Lấy thông tin cấu hình
                String cloudName = prop.getProperty("cloud.name");
                String apiKey = prop.getProperty("cloud.api_key");
                String apiSecret = prop.getProperty("cloud.api_secret");

                // Kiểm tra các thông tin bắt buộc
                if (cloudName == null || apiKey == null || apiSecret == null) {
                    throw new RuntimeException("Missing required Cloudinary configuration in " + PROPERTIES_FILE);
                }

                // Tạo config cho Cloudinary
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", cloudName);
                config.put("api_key", apiKey);
                config.put("api_secret", apiSecret);

                cloudinary = new Cloudinary(config);
            } catch (Exception e) {
                throw new RuntimeException("Error initializing Cloudinary: " + e.getMessage(), e);
            }
        }
        return cloudinary;
    }
} 