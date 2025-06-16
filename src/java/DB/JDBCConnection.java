package DB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConnection {
    private static final String PROPERTIES_FILE = "DB/db.properties";

    public static Connection getConnection() {
        try {
            // First try to load from classpath
            InputStream input = JDBCConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
            
            // If not found in classpath, try to load from file system
            if (input == null) {
                input = JDBCConnection.class.getResourceAsStream("/" + PROPERTIES_FILE);
            }
            
            if (input == null) {
                throw new RuntimeException("Unable to find " + PROPERTIES_FILE + " in classpath or file system");
            }

            Properties prop = new Properties();
            prop.load(input);
            input.close();

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("Missing required database configuration properties");
            }

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load DB config or connect to database: " + e.getMessage(), e);
        }
    }
}

