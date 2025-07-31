package Dao;

// ===== IMPORT STATEMENTS =====
import DB.JDBCConnection;                   // Database connection utility
import com.google.gson.JsonArray;           // JSON array utility
import com.google.gson.JsonObject;          // JSON object utility
import model.User;                          // User model

import java.sql.*;                          // SQL database operations
import java.util.*;                         // Collections framework
import java.io.InputStream;                 // Input stream
import controller.Email.EmailUtil;          // Email utility

// ===== USER DATA ACCESS OBJECT =====
/**
 * UserDAO - Data Access Object cho Users
 * Quản lý tất cả các thao tác CRUD với bảng Users trong database
 * Bao gồm: thêm, sửa, xóa, tìm kiếm, lấy danh sách user
 * Hỗ trợ: Google OAuth, Teacher approval, Profile management
 */
public class UserDAO {

    // ===== INSERT USER =====
    /**
     * Thêm user mới vào database
     * @param user User object cần thêm
     * @throws SQLException nếu có lỗi database
     */
    public void insertUser(User user) throws SQLException {
        // SQL query để insert user mới
        String sql = "INSERT INTO [dbo].[Users] (RoleID, Email, PasswordHash, GoogleID, FullName, IsActive, IsLocked, CreatedAt, Gender) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setInt(1, user.getRoleID());           // Role ID
            stmt.setString(2, user.getEmail());         // Email
            stmt.setString(3, user.getPasswordHash());  // Password hash
            stmt.setString(4, user.getGoogleID());      // Google ID (có thể null)
            stmt.setString(5, user.getFullName());      // Full name
            stmt.setBoolean(6, user.isActive());        // Is active
            stmt.setBoolean(7, user.isLocked());        // Is locked
            stmt.setString(8, user.getGender() != null ? user.getGender() : "Khác");  // Gender với default
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== GET USER BY ID =====
    /**
     * Lấy user theo ID
     * @param userID ID của user cần lấy
     * @return User object hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi database
     */
    public User getUserById(int userID) throws SQLException {
        // SQL query để lấy user theo ID
        String sql = "SELECT * FROM [dbo].[Users] WHERE UserID = ?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);  // Set UserID parameter
            
            // Thực thi query và xử lý kết quả
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);  // Extract user từ ResultSet
            }
        }
        return null;  // Không tìm thấy user
    }

    // ===== GET USER BY EMAIL =====
    /**
     * Lấy user theo email
     * @param email Email của user cần lấy
     * @return User object hoặc null nếu không tìm thấy
     */
    public User getUserByEmail(String email) {
        // SQL query để lấy user theo email
        String sql = "SELECT * FROM Users WHERE email = ?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);  // Set email parameter
            
            // Thực thi query và xử lý kết quả
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);  // Extract user từ ResultSet
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Không tìm thấy user
    }

    // ===== GET ALL USERS =====
    /**
     * Lấy tất cả users từ database
     * @return List tất cả users
     * @throws SQLException nếu có lỗi database
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        
        // SQL query để lấy tất cả users
        String sql = "SELECT * FROM [dbo].[Users]";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql); 
             ResultSet rs = stmt.executeQuery()) {
            
            // Lặp qua kết quả và tạo User objects
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    // ===== UPDATE USER =====
    /**
     * Cập nhật thông tin user
     * @param user User object cần cập nhật
     * @throws SQLException nếu có lỗi database
     */
    public void updateUser(User user) throws SQLException {
        // SQL query để update user
        String sql = "UPDATE [dbo].[Users] SET RoleID = ?, Email = ?, PasswordHash = ?, GoogleID = ?, FullName = ?, "
                + "IsActive = ?, IsLocked = ?, BirthDate = ?, PhoneNumber = ?, JapaneseLevel = ?, Address = ?, "
                + "Country = ?, Avatar = ?, IsTeacherPending = ?, CertificatePath = ? WHERE UserID = ?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setInt(1, user.getRoleID());           // Role ID
            stmt.setString(2, user.getEmail());         // Email
            stmt.setString(3, user.getPasswordHash());  // Password hash
            stmt.setString(4, user.getGoogleID());      // Google ID
            stmt.setString(5, user.getFullName());      // Full name
            stmt.setBoolean(6, user.isActive());        // Is active
            stmt.setBoolean(7, user.isLocked());        // Is locked
            stmt.setDate(8, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);  // Birth date
            stmt.setString(9, user.getPhoneNumber());   // Phone number
            stmt.setString(10, user.getJapaneseLevel()); // Japanese level
            stmt.setString(11, user.getAddress());      // Address
            stmt.setString(12, user.getCountry());      // Country
            stmt.setString(13, user.getAvatar());       // Avatar
            stmt.setBoolean(14, user.isTeacherPending()); // Is teacher pending
            stmt.setString(15, user.getCertificatePath()); // Certificate path
            stmt.setInt(16, user.getUserID());          // User ID
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== UPDATE PROFILE =====
    /**
     * Cập nhật profile của user (thông tin cá nhân)
     * @param user User object cần cập nhật profile
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi database
     */
    public boolean updateProfile(User user) throws SQLException {
        // SQL query để update profile
        String sql = "UPDATE Users SET Email = ?, FullName = ?, PhoneNumber = ?, BirthDate = ?, "
                + "JapaneseLevel = ?, Address = ?, Country = ?, Avatar = ? WHERE UserID = ?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setString(1, user.getEmail());         // Email
            stmt.setString(2, user.getFullName());      // Full name
            stmt.setString(3, user.getPhoneNumber());   // Phone number
            stmt.setDate(4, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);  // Birth date
            stmt.setString(5, user.getJapaneseLevel()); // Japanese level
            stmt.setString(6, user.getAddress());      // Address
            stmt.setString(7, user.getCountry());      // Country
            stmt.setString(8, user.getAvatar());       // Avatar
            stmt.setInt(9, user.getUserID());          // User ID
            return stmt.executeUpdate() > 0;
        }
    }

    // ===== DELETE USER =====
    /**
     * Xóa user khỏi database
     * @param userID ID của user cần xóa
     * @throws SQLException nếu có lỗi database
     */
    public void deleteUser(int userID) throws SQLException {
        // SQL query để xóa user
        String sql = "DELETE FROM [dbo].[Users] WHERE UserID = ?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userID);  // Set UserID parameter
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== CREATE NEW USER (DEFAULT ROLE) =====
    /**
     * Tạo user mới với vai trò mặc định (User)
     * @param email Email của user mới
     * @param password Password của user mới
     * @param gender Giới tính của user mới
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createNewUser(String email, String password, String gender) {
        // SQL query để tạo user mới với vai trò mặc định
        String sql = "INSERT INTO Users (RoleID, Email, PasswordHash, Gender) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1); // default role
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, gender);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CREATE NEW USER (SPECIFIC ROLE) =====
    /**
     * Tạo user mới với vai trò được chỉ định (Teacher/Student)
     * @param email Email của user mới
     * @param password Password của user mới
     * @param gender Giới tính của user mới
     * @param role Vai trò của user (teacher, student)
     * @param isTeacherPending true nếu là giáo viên chờ xác nhận
     * @param certificatePath Đường dẫn file chứng chỉ (nếu có)
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createNewUser(String email, String password, String gender, String role, boolean isTeacherPending, String certificatePath) {
        // SQL query để tạo user mới với vai trò được chỉ định
        String sql = "INSERT INTO Users (RoleID, Email, PasswordHash, Gender, IsTeacherPending, CertificatePath) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int roleId = 1; // default user
            if ("teacher".equals(role)) roleId = 1; // vẫn là user thường, chờ xác nhận
            if ("student".equals(role)) roleId = 1;
            pstmt.setInt(1, roleId);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, gender);
            pstmt.setBoolean(5, isTeacherPending);
            pstmt.setString(6, certificatePath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CREATE NEW USER (FULL DETAILS) =====
    /**
     * Tạo user mới với tất cả thông tin chi tiết
     * @param email Email của user mới
     * @param fullName Họ và tên của user mới
     * @param password Password của user mới
     * @param gender Giới tính của user mới
     * @param role Vai trò của user (teacher, student)
     * @param isTeacherPending true nếu là giáo viên chờ xác nhận
     * @param certificatePath Đường dẫn file chứng chỉ (nếu có)
     * @return true nếu tạo thành công, false nếu thất bại
     */
    public boolean createNewUser(String email, String fullName, String password, String gender, String role, boolean isTeacherPending, String certificatePath) {
        // SQL query để tạo user mới với tất cả thông tin chi tiết
        String sql = "INSERT INTO Users (RoleID, Email, FullName, PasswordHash, Gender, IsTeacherPending, CertificatePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int roleId = 1; // default user
            if ("teacher".equals(role)) roleId = 1; // vẫn là user thường, chờ xác nhận
            if ("student".equals(role)) roleId = 1;
            pstmt.setInt(1, roleId);
            pstmt.setString(2, email);
            pstmt.setString(3, fullName);
            pstmt.setString(4, password);
            pstmt.setString(5, gender);
            pstmt.setBoolean(6, isTeacherPending);
            pstmt.setString(7, certificatePath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE PASSWORD =====
    /**
     * Cập nhật password của user theo email
     * @param email Email của user cần cập nhật password
     * @param newPassword Password mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public static boolean updatePassword(String email, String newPassword) {
        // SQL query để cập nhật password
        String sql = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET TOTAL USERS =====
    /**
     * Lấy tổng số lượng user trong database
     * @return Tổng số user
     * @throws SQLException nếu có lỗi database
     */
    public int getTotalUsers() throws SQLException {
        // SQL query để lấy tổng số user
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Users]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    // ===== GET USERS BY MONTH AND YEAR =====
    /**
     * Lấy số lượng user được tạo trong một tháng và năm cụ thể
     * @param month Tháng cần lấy
     * @param year Năm cần lấy
     * @return Số lượng user
     * @throws SQLException nếu có lỗi database
     */
    public int getUsersByMonthAndYear(int month, int year) throws SQLException {
        // SQL query để lấy số lượng user theo tháng và năm
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Users] WHERE MONTH(CreatedAt) = ? AND YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count");
                }
            }
        }
        return 0;
    }

    // ===== GET USER COUNT BY MONTH =====
    /**
     * Lấy số lượng user được tạo theo từng tháng trong một năm
     * @param year Năm cần lấy
     * @return List các đối tượng JSON chứa thông tin tháng và số lượng user
     * @throws SQLException nếu có lỗi database
     */
    public List<JsonObject> getUserCountByMonth(int year) throws SQLException {
        List<JsonObject> list = new ArrayList<>();
        // SQL query để lấy số lượng user theo từng tháng
        String sql = "SELECT MONTH(CreatedAt) AS Period, COUNT(*) AS Count FROM Users "
                + "WHERE YEAR(CreatedAt) = ? GROUP BY MONTH(CreatedAt) ORDER BY MONTH(CreatedAt)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", "Tháng " + rs.getInt("Period"));
                obj.addProperty("count", rs.getInt("Count"));
                list.add(obj);
            }
        }
        return list;
    }

    // ===== GET USER COUNT BY YEAR =====
    /**
     * Lấy số lượng user được tạo theo từng năm
     * @return List các đối tượng JSON chứa thông tin năm và số lượng user
     * @throws SQLException nếu có lỗi database
     */
    public List<JsonObject> getUserCountByYear() throws SQLException {
        List<JsonObject> list = new ArrayList<>();
        // SQL query để lấy số lượng user theo từng năm
        String sql = "SELECT YEAR(CreatedAt) AS Period, COUNT(*) AS Count FROM Users "
                + "GROUP BY YEAR(CreatedAt) ORDER BY YEAR(CreatedAt)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", String.valueOf(rs.getInt("Period")));
                obj.addProperty("count", rs.getInt("Count"));
                list.add(obj);
            }
        }
        return list;
    }

    // ===== GET REGISTRATIONS BY PERIOD =====
    /**
     * Lấy số lượng đăng ký (registrations) theo khoảng thời gian (tháng hoặc năm)
     * @param periodType Loại khoảng thời gian ("month" hoặc "year")
     * @return JsonArray chứa thông tin khoảng thời gian và số lượng đăng ký
     */
    public JsonArray getRegistrationsByPeriod(String periodType) {
        JsonArray jsonArray = new JsonArray();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        String sql;
        if (periodType.equalsIgnoreCase("month")) {
            sql = "SELECT DATENAME(MONTH, CreatedAt) AS Period, COUNT(*) AS RegistrationCount "
                    + "FROM [dbo].[Users] WHERE YEAR(CreatedAt) = ? "
                    + "GROUP BY DATENAME(MONTH, CreatedAt), MONTH(CreatedAt) "
                    + "ORDER BY MONTH(CreatedAt)";
        } else {
            sql = "SELECT YEAR(CreatedAt) AS Period, COUNT(*) AS RegistrationCount "
                    + "FROM [dbo].[Users] GROUP BY YEAR(CreatedAt) ORDER BY YEAR(CreatedAt)";
        }

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (periodType.equalsIgnoreCase("month")) {
                stmt.setInt(1, currentYear);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("period", rs.getString("Period"));
                    obj.addProperty("count", rs.getInt("RegistrationCount"));
                    jsonArray.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (periodType.equalsIgnoreCase("month")) {
            JsonArray fullYearArray = new JsonArray();
            String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
            for (String month : months) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", month);
                obj.addProperty("count", 0);
                for (int j = 0; j < jsonArray.size(); j++) {
                    JsonObject existing = jsonArray.get(j).getAsJsonObject();
                    if (existing.get("period").getAsString().equalsIgnoreCase(month)) {
                        obj.addProperty("count", existing.get("count").getAsInt());
                        break;
                    }
                }
                fullYearArray.add(obj);
            }
            return fullYearArray;
        }

        return jsonArray;
    }

    // ===== UPDATE AVATAR =====
    /**
     * Cập nhật URL avatar cho user
     * @param userId ID của user cần cập nhật avatar
     * @param avatarUrl URL mới của avatar
     * @throws SQLException nếu có lỗi database
     */
    public void updateAvatar(int userId, String avatarUrl) throws SQLException {
        // SQL query để cập nhật avatar
        String sql = "UPDATE Users SET Avatar = ? WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avatarUrl);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    // ===== EXTRACT USER FROM RESULT SET =====
    /**
     * Chuyển đổi ResultSet thành User object
     * @param rs ResultSet chứa dữ liệu user
     * @return User object được tạo từ ResultSet
     * @throws SQLException nếu có lỗi khi trích xuất dữ liệu
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setRoleID(rs.getInt("RoleID"));
        user.setEmail(rs.getString("Email"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setGoogleID(rs.getString("GoogleID"));
        user.setFullName(rs.getString("FullName"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));
        user.setActive(rs.getBoolean("IsActive"));
        user.setLocked(rs.getBoolean("IsLocked"));
        try {
            user.setBirthDate(rs.getDate("BirthDate"));
            user.setPhoneNumber(rs.getString("PhoneNumber"));
            user.setJapaneseLevel(rs.getString("JapaneseLevel"));
            user.setAddress(rs.getString("Address"));
            user.setCountry(rs.getString("Country"));
            user.setAvatar(rs.getString("Avatar")); // Sử dụng setAvatar thay vì setAvatarUrl
            user.setGender(rs.getString("Gender"));
            user.setCertificatePath(rs.getString("CertificatePath"));
            user.setTeacherPending(rs.getBoolean("IsTeacherPending"));
        } catch (SQLException | NullPointerException ignored) {
        }
        return user;
    }

    // ===== GET USER COUNT BY ROLE =====
    /**
     * Lấy số lượng user theo từng vai trò và tính phần trăm
     * @return JsonArray chứa thông tin vai trò và số lượng user
     * @throws SQLException nếu có lỗi database
     */
    public JsonArray getUserCountByRole() throws SQLException {
        JsonArray jsonArray = new JsonArray();
        // Lấy tổng số người dùng trước
        String totalSql = "SELECT COUNT(*) AS Total FROM [dbo].[Users]";
        int totalUsers = 0;
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(totalSql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalUsers = rs.getInt("Total");
            }
        }

        // Lấy số lượng người dùng theo vai trò
        String sql = "SELECT r.RoleName, COUNT(u.UserID) AS UserCount "
                + "FROM [dbo].[Roles] r LEFT JOIN [dbo].[Users] u ON r.RoleID = u.RoleID "
                + "GROUP BY r.RoleName";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                String roleName = rs.getString("RoleName");
                int count = rs.getInt("UserCount");
                double percent = totalUsers > 0 ? ((double) count / totalUsers * 100) : 0;
                obj.addProperty("role", roleName);
                obj.addProperty("count", count);
                obj.addProperty("percent", String.format("%.1f", percent)); // Làm tròn đến 1 chữ số thập phân
                jsonArray.add(obj);
                System.out.println("Role: " + roleName + ", Count: " + count + ", Percent: " + percent);
            }
        }
        System.out.println("JSON trả về: " + jsonArray.toString());
        return jsonArray;
    }

    // ===== GET USERS BY ROLES =====
    /**
     * Lấy danh sách user theo một danh sách các vai trò
     * @param roleNames Danh sách tên vai trò cần lấy
     * @return List các User objects
     * @throws SQLException nếu có lỗi database
     */
    public List<User> getUsersByRoles(List<String> roleNames) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM [dbo].[Users] u JOIN [dbo].[Roles] r ON u.RoleID = r.RoleID WHERE r.RoleName IN (";
        // Build placeholders for role names
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < roleNames.size(); i++) {
            placeholders.append("?");
            if (i < roleNames.size() - 1) {
                placeholders.append(",");
            }
        }
        sql += placeholders.toString() + ")";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set role names as parameters
            for (int i = 0; i < roleNames.size(); i++) {
                stmt.setString(i + 1, roleNames.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        }
        return users;
    }

    // ===== SEARCH USERS BY FULL NAME =====
    /**
     * Tìm kiếm user theo tên đầy đủ (có tính đến tiếng Việt)
     * @param keyword Từ khóa tìm kiếm
     * @return List các User objects tìm thấy
     * @throws SQLException nếu có lỗi database
     */
    public List<User> searchUsersByFullName(String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        // SQL query để tìm kiếm user theo tên đầy đủ và trạng thái hoạt động
        String sql = "SELECT * FROM [dbo].[Users] WHERE dbo.RemoveDiacritics(FullName) LIKE '%' + dbo.RemoveDiacritics(?) + '%' AND IsActive = 1";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    // ===== GET PENDING TEACHERS =====
    /**
     * Lấy danh sách các user đang chờ xác nhận là giáo viên
     * @return List các User objects
     */
    public List<User> getPendingTeachers() {
        List<User> list = new ArrayList<>();
        // SQL query để lấy các user đang chờ xác nhận là giáo viên
        String sql = "SELECT * FROM Users WHERE IsTeacherPending = 1 AND RoleID = 1";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== APPROVE TEACHER =====
    /**
     * Xác nhận giáo viên
     * @param userId ID của user giáo viên cần xác nhận
     */
    public void approveTeacher(int userId) {
        // SQL query để cập nhật vai trò và trạng thái chờ xác nhận của user
        String sql = "UPDATE Users SET RoleID = 3, IsTeacherPending = 0 WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Gửi email xác nhận giáo viên
        User user = getUserByIdSafe(userId);
        if (user != null) {
            try {
                EmailUtil.sendTeacherApprovedMail(user);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // ===== REJECT TEACHER =====
    /**
     * Từ chối giáo viên
     * @param userId ID của user giáo viên cần từ chối
     */
    public void rejectTeacher(int userId) {
        // SQL query để cập nhật trạng thái chờ xác nhận của user
        String sql = "UPDATE Users SET IsTeacherPending = 0 WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Gửi email từ chối giáo viên
        User user = getUserByIdSafe(userId);
        if (user != null) {
            try {
                EmailUtil.sendTeacherRejectedMail(user);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Helper để lấy user không throw exception
    /**
     * Lấy user theo ID mà không throw exception
     * @param userId ID của user cần lấy
     * @return User object hoặc null nếu có lỗi
     */
    private User getUserByIdSafe(int userId) {
        try {
            return getUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ===== IS GOOGLE USER =====
    /**
     * Kiểm tra xem user có phải là user Google không
     * @param email Email của user cần kiểm tra
     * @return true nếu là user Google, false nếu không phải
     */
    public boolean isGoogleUser(String email) {
        // SQL query để kiểm tra user Google
        String sql = "SELECT PasswordHash, GoogleID FROM Users WHERE email = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("PasswordHash");
                String googleID = rs.getString("GoogleID");
                
                // Kiểm tra cả PasswordHash và GoogleID
                boolean isGoogleByPassword = passwordHash != null && passwordHash.startsWith("GOOGLE_LOGIN_");
                boolean isGoogleByID = googleID != null && !googleID.trim().isEmpty();
                
                return isGoogleByPassword || isGoogleByID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===== GET USER BY GOOGLE ID =====
    /**
     * Lấy user theo ID của Google
     * @param googleId ID của Google cần lấy
     * @return User object hoặc null nếu không tìm thấy
     */
    public User getUserByGoogleId(String googleId) {
        // SQL query để lấy user theo ID của Google
        String sql = "SELECT * FROM Users WHERE GoogleID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, googleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        UserDAO dao = new UserDAO();
        User users = dao.getUserById(1);
     
            System.out.println(users);
        
    }
}
