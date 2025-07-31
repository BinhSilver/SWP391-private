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
            // ===== STEP 1: LOAD CLASSLOADER =====
            // Lấy ClassLoader của class hiện tại để load resources
            ClassLoader classLoader = JDBCConnection.class.getClassLoader();
            
            // ===== STEP 2: LOAD PROPERTIES FILE =====
            // Thử load file properties từ classpath
            InputStream input = classLoader.getResourceAsStream(PROPERTIES_FILE);
            
            // ===== STEP 3: VALIDATE FILE EXISTENCE =====
            // Nếu không tìm thấy file, throw exception với thông tin chi tiết
            if (input == null) {
                String errorMsg = String.format("Cannot find %s in classpath. ClassLoader: %s", 
                    PROPERTIES_FILE, classLoader.getClass().getName());
                throw new RuntimeException(errorMsg);
            }

            // ===== STEP 4: PARSE PROPERTIES =====
            // Load properties từ file
            Properties prop = new Properties();
            prop.load(input);
            input.close();

            // ===== STEP 5: EXTRACT CONNECTION INFO =====
            // Lấy các thông tin kết nối từ properties
            String url = prop.getProperty("db.url");           // Database URL
            String user = prop.getProperty("db.user");         // Database username
            String password = prop.getProperty("db.password"); // Database password

            // ===== STEP 6: VALIDATE CONFIGURATION =====
            // Kiểm tra các thông tin bắt buộc
            if (url == null || user == null || password == null) {
                throw new RuntimeException("Missing required database configuration properties. " +
                    "Please check if db.url, db.user, and db.password are set in " + PROPERTIES_FILE);
            }

            // ===== STEP 7: CREATE CONNECTION =====
            // Load driver và tạo kết nối
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  // Load SQL Server driver
            return DriverManager.getConnection(url, user, password);         // Tạo connection
            
        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Log chi tiết lỗi và throw RuntimeException
            String errorMsg = String.format("Database connection error: %s", e.getMessage());
            throw new RuntimeException(errorMsg, e);
        }
    }
}
