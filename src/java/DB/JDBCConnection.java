package DB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConnection {
    // Đường dẫn tới file properties trong classpath
    private static final String PROPERTIES_FILE = "DB/db.properties";

    public static Connection getConnection() {
        try {
            // Lấy ClassLoader của class hiện tại
            ClassLoader classLoader = JDBCConnection.class.getClassLoader();
            
            // Thử load file properties từ classpath
            InputStream input = classLoader.getResourceAsStream(PROPERTIES_FILE);
            
            // Nếu không tìm thấy file, throw exception với thông tin chi tiết
            if (input == null) {
                String errorMsg = String.format("Cannot find %s in classpath. ClassLoader: %s", 
                    PROPERTIES_FILE, classLoader.getClass().getName());
                throw new RuntimeException(errorMsg);
            }

            // Load properties từ file
            Properties prop = new Properties();
            prop.load(input);
            input.close();

            // Lấy các thông tin kết nối từ properties
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            // Kiểm tra các thông tin bắt buộc
            if (url == null || user == null || password == null) {
                throw new RuntimeException("Missing required database configuration properties. " +
                    "Please check if db.url, db.user, and db.password are set in " + PROPERTIES_FILE);
            }

            // Load driver và tạo kết nối
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            // Log chi tiết lỗi
            String errorMsg = String.format("Database connection error: %s", e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }
    }
}

