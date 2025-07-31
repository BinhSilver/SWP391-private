package DB;

// ===== IMPORT STATEMENTS =====
import java.io.InputStream;                 // Input stream utility
import java.sql.Connection;                 // Database connection
import java.sql.DriverManager;              // Driver manager
import java.util.Properties;                // Properties utility

// ===== JDBC CONNECTION UTILITY =====
/**
 * JDBCConnection - Utility class để quản lý kết nối database
 * Đọc cấu hình database từ file properties và tạo connection
 * 
 * Chức năng chính:
 * - Load database configuration từ file properties
 * - Tạo và quản lý database connection
 * - Error handling và logging
 * - SQL Server driver management
 */
public class JDBCConnection {
    
    // ===== CONSTANTS =====
    // Đường dẫn tới file properties trong classpath
    private static final String PROPERTIES_FILE = "resources/db.properties";

    // ===== GET CONNECTION =====
    /**
     * Tạo và trả về database connection
     * Quy trình:
     * 1. Load file properties từ classpath
     * 2. Đọc thông tin kết nối (URL, user, password)
     * 3. Load SQL Server driver
     * 4. Tạo connection và trả về
     * 
     * @return Database connection
     * @throws RuntimeException nếu có lỗi trong quá trình kết nối
     */
    public static Connection getConnection() {
        try {
            // 🔒 FIX LỖI: Disable retry config logic của driver SQL Server
            System.setProperty("sqlserver.retry.config.disable", "true");
    
            // ===== STEP 1: LOAD CLASSLOADER =====
            ClassLoader classLoader = JDBCConnection.class.getClassLoader();
            
            // ===== STEP 2: LOAD PROPERTIES FILE =====
            InputStream input = classLoader.getResourceAsStream(PROPERTIES_FILE);
            
            // ===== STEP 3: VALIDATE FILE EXISTENCE =====
            if (input == null) {
                String errorMsg = String.format("Cannot find %s in classpath. ClassLoader: %s", 
                    PROPERTIES_FILE, classLoader.getClass().getName());
                throw new RuntimeException(errorMsg);
            }
    
            // ===== STEP 4: PARSE PROPERTIES =====
            Properties prop = new Properties();
            prop.load(input);
            input.close();
    
            // ===== STEP 5: EXTRACT CONNECTION INFO =====
            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
    
            // ===== STEP 6: VALIDATE CONFIGURATION =====
            if (url == null || user == null || password == null) {
                throw new RuntimeException("Missing required database configuration properties. " +
                    "Please check if db.url, db.user, and db.password are set in " + PROPERTIES_FILE);
            }
    
            // ===== STEP 7: CREATE CONNECTION =====
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, user, password);
    
        } catch (Exception e) {
            String errorMsg = String.format("Database connection error: %s", e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }
    }
    
}
