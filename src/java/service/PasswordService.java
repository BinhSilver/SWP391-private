package service;

import DB.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PasswordService {

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        try (Connection conn = new JDBCConnection().getConnection()) {
            // Kiểm tra mật khẩu cũ
            String sqlCheck = "SELECT PasswordHash FROM Users WHERE Email = ?";
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, email);
                try (ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("PasswordHash");

                        if (!storedPassword.equals(oldPassword)) {
                            return false; // Mật khẩu cũ không đúng
                        }
                    } else {
                        return false; // Email không tồn tại
                    }
                }
            }

            // Cập nhật mật khẩu mới (không hash)
            String sqlUpdate = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setString(1, newPassword); // plain text
                pstmtUpdate.setString(2, email);
                pstmtUpdate.executeUpdate();
            }

            return true; // Thay đổi mật khẩu thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Lỗi hệ thống
        }
    }
}
