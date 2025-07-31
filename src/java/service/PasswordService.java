package service;

// ===== IMPORT STATEMENTS =====
import DB.JDBCConnection;                   // Database connection utility
import java.sql.Connection;                 // Database connection
import java.sql.PreparedStatement;          // Prepared statement
import java.sql.ResultSet;                  // Result set

// ===== PASSWORD SERVICE =====
/**
 * PasswordService - Service class để xử lý các thao tác liên quan đến password
 * Bao gồm: đổi mật khẩu, kiểm tra mật khẩu cũ, cập nhật mật khẩu mới
 * 
 * Lưu ý: Hiện tại password được lưu dưới dạng plain text (không hash)
 * Cần cải thiện để hash password trước khi lưu vào database
 */
public class PasswordService {

    // ===== CHANGE PASSWORD =====
    /**
     * Đổi mật khẩu cho user
     * Quy trình:
     * 1. Kiểm tra email có tồn tại không
     * 2. Kiểm tra mật khẩu cũ có đúng không
     * 3. Cập nhật mật khẩu mới
     * 
     * @param email Email của user cần đổi mật khẩu
     * @param oldPassword Mật khẩu cũ
     * @param newPassword Mật khẩu mới
     * @return true nếu đổi mật khẩu thành công, false nếu thất bại
     */
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        try (Connection conn = new JDBCConnection().getConnection()) {
            
            // ===== STEP 1: CHECK OLD PASSWORD =====
            // Kiểm tra mật khẩu cũ có đúng không
            String sqlCheck = "SELECT PasswordHash FROM Users WHERE Email = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, email);  // Set email parameter
                
                // Thực thi query và kiểm tra kết quả
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        // Lấy password hash từ database
                        String storedPassword = rs.getString("PasswordHash");

                        // ===== VALIDATE OLD PASSWORD =====
                        // Kiểm tra mật khẩu cũ có đúng không
                        if (!storedPassword.equals(oldPassword)) {
                            return false; // Mật khẩu cũ không đúng
                        }
                    } else {
                        return false; // Email không tồn tại
                    }
                }
            }

            // ===== STEP 2: UPDATE NEW PASSWORD =====
            // Cập nhật mật khẩu mới (không hash - cần cải thiện)
            String sqlUpdate = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setString(1, newPassword); // plain text - cần hash
                pstmtUpdate.setString(2, email);
                pstmtUpdate.executeUpdate();
            }

            return true; // Thay đổi mật khẩu thành công
            
        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi và log
            e.printStackTrace();
            return false; // Lỗi hệ thống
        }
    }
}
