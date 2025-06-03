package service;

import DB.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserService {

    // Lấy tất cả người dùng
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT UserID, RoleID, Email, PasswordHash, FullName, CreatedAt, IsActive, IsLocked FROM Users";

        try (Connection conn = new JDBCConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("UserID"));
                user.setRoleID(rs.getInt("RoleID"));
                user.setEmail(rs.getString("Email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setFullName(rs.getString("FullName"));
                user.setCreatedAt(rs.getTimestamp("CreatedAt"));
                user.setActive(rs.getBoolean("IsActive"));
                user.setLocked(rs.getBoolean("IsLocked"));

                userList.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    // Cập nhật thông tin người dùng
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET RoleID = ?, Email = ?, PasswordHash = ?, FullName = ?, IsActive = ?, IsLocked = ? WHERE UserID = ?";

        try (Connection conn = new JDBCConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getRoleID());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash()); // Nếu cần, hãy đảm bảo password đã được hash trước khi gọi hàm này
            pstmt.setString(4, user.getFullName());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setBoolean(6, user.isLocked());
            pstmt.setInt(7, user.getUserID());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Nếu bạn dùng IDENTITY (auto-increment), không cần hàm generateUserId
    // Nhưng nếu vẫn muốn tạo manual ID kiểu chuỗi, bạn có thể giữ nguyên hoặc viết lại phù hợp với kiểu chuỗi
    // Dưới đây là ví dụ nếu userId là int auto-increment thì bỏ hàm này

}
