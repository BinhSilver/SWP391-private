package service;

// ===== IMPORT STATEMENTS =====
import DB.JDBCConnection;                   // Database connection utility
import java.sql.Connection;                 // Database connection
import java.sql.PreparedStatement;          // Prepared statement
import java.sql.ResultSet;                  // Result set
import java.sql.SQLException;               // SQL Exception
import java.util.ArrayList;                 // ArrayList collection
import java.util.List;                      // List collection
import model.User;                          // User model

// ===== USER SERVICE =====
/**
 * UserService - Service class để xử lý business logic cho User
 * Quản lý các thao tác liên quan đến user như: lấy danh sách, cập nhật thông tin
 * 
 * Chức năng chính:
 * - Lấy tất cả users
 * - Cập nhật thông tin user
 * - Cập nhật user với connection có sẵn
 * - Xử lý user data validation
 */
public class UserService {

    // ===== GET ALL USERS =====
    /**
     * Lấy tất cả users từ database
     * @return List tất cả users với thông tin cơ bản
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        
        // SQL query để lấy tất cả users với thông tin cơ bản
        String sql = "SELECT UserID, RoleID, Email, PasswordHash, FullName, CreatedAt, IsActive, IsLocked FROM Users";

        try (Connection conn = new JDBCConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Lặp qua kết quả và tạo User objects
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));           // ID user
                user.setRoleID(rs.getInt("RoleID"));           // Role ID
                user.setEmail(rs.getString("Email"));          // Email
                user.setPasswordHash(rs.getString("PasswordHash")); // Password hash
                user.setFullName(rs.getString("FullName"));    // Full name
                user.setCreatedAt(rs.getTimestamp("CreatedAt")); // Created time
                user.setActive(rs.getBoolean("IsActive"));     // Active status
                user.setLocked(rs.getBoolean("IsLocked"));     // Locked status

                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // ===== UPDATE USER =====
    /**
     * Cập nhật thông tin user cơ bản
     * @param user User object cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateUser(User user) {
        // SQL query để update user với thông tin cơ bản
        String sql = "UPDATE Users SET RoleID = ?, Email = ?, PasswordHash = ?, FullName = ?, IsActive = ?, IsLocked = ? WHERE UserID = ?";

        try (Connection conn = new JDBCConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set các tham số cho prepared statement
            pstmt.setInt(1, user.getRoleID());           // Role ID
            pstmt.setString(2, user.getEmail());         // Email
            pstmt.setString(3, user.getPasswordHash());  // Password hash (đảm bảo đã hash trước khi gọi)
            pstmt.setString(4, user.getFullName());      // Full name
            pstmt.setBoolean(5, user.isActive());        // Active status
            pstmt.setBoolean(6, user.isLocked());        // Locked status
            pstmt.setInt(7, user.getUserID());           // User ID

            // Thực thi query và kiểm tra kết quả
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE USER WITH CONNECTION =====
    /**
     * Cập nhật thông tin user đầy đủ với connection có sẵn
     * Sử dụng khi cần transaction hoặc connection được quản lý bên ngoài
     * 
     * @param user User object cần cập nhật
     * @param conn Database connection có sẵn
     * @throws SQLException nếu có lỗi database
     */
    public void updateUserWithConnection(User user, Connection conn) throws SQLException {
        // SQL query để update user với thông tin đầy đủ
        String sql = "UPDATE Users SET RoleID = ?, Email = ?, PasswordHash = ?, GoogleID = ?, FullName = ?, "
                + "BirthDate = ?, PhoneNumber = ?, JapaneseLevel = ?, Address = ?, Country = ?, Avatar = ?, Gender = ? "
                + "WHERE UserID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set các tham số cho prepared statement
            stmt.setInt(1, user.getRoleID());           // Role ID
            stmt.setString(2, user.getEmail());         // Email
            stmt.setString(3, user.getPasswordHash());  // Password hash
            stmt.setString(4, user.getGoogleID());      // Google ID
            stmt.setString(5, user.getFullName());      // Full name
            stmt.setDate(6, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null); // Birth date
            stmt.setString(7, user.getPhoneNumber());   // Phone number
            stmt.setString(8, user.getJapaneseLevel()); // Japanese level
            stmt.setString(9, user.getAddress());       // Address
            stmt.setString(10, user.getCountry());      // Country
            stmt.setString(11, user.getAvatar());       // Avatar
            stmt.setString(12, user.getGender());       // Gender
            stmt.setInt(13, user.getUserID());          // User ID
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== NOTES =====
    // Nếu bạn dùng IDENTITY (auto-increment), không cần hàm generateUserId
    // Nhưng nếu vẫn muốn tạo manual ID kiểu chuỗi, bạn có thể giữ nguyên hoặc viết lại phù hợp với kiểu chuỗi
    // Dưới đây là ví dụ nếu userId là int auto-increment thì bỏ hàm này
}
