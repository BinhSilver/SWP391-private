package Dao;

import java.sql.*;
import java.util.Date;

import DB.JDBCConnection;
import model.UserPremium;
import org.checkerframework.checker.units.qual.A;

public class UserPremiumDAO {

    public void add(UserPremium up) throws SQLException {
        String sql = "INSERT INTO UserPremium (UserID, PlanID, StartDate, EndDate) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, up.getUserID());
            stmt.setInt(2, up.getPlanID());
            stmt.setTimestamp(3, new Timestamp(up.getStartDate().getTime()));
            stmt.setTimestamp(4, new Timestamp(up.getEndDate().getTime()));
            stmt.executeUpdate();
        }
    }
    public void update(UserPremium up) throws SQLException {
        String sql = "UPDATE UserPremium SET StartDate=?, EndDate=? WHERE UserID=? AND PlanID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(up.getStartDate().getTime()));
            stmt.setTimestamp(2, new Timestamp(up.getEndDate().getTime()));
            stmt.setInt(3, up.getUserID());
            stmt.setInt(4, up.getPlanID());
            stmt.executeUpdate();
        }
    }

    public void delete(int userID, int planID) throws SQLException {
        String sql = "DELETE FROM UserPremium WHERE UserID=? AND PlanID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, planID);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Kiểm tra xem premium của user đã hết hạn chưa
     * @param userID ID của user cần kiểm tra
     * @return true nếu premium đã hết hạn, false nếu còn hạn hoặc không có premium
     */
    public boolean checkPremiumExpired(int userID) throws SQLException {
        String sql = "SELECT EndDate FROM UserPremium WHERE UserID = ? ORDER BY EndDate DESC";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp endDate = rs.getTimestamp("EndDate");
                    return endDate != null && endDate.before(new Timestamp(System.currentTimeMillis()));
                }
            }
        }
        return true; // Nếu không tìm thấy bản ghi nào, coi như đã hết hạn
    }
    
    /**
     * Lấy số người dùng Premium trong một tháng và năm cụ thể từ bảng UserPremium
     */
    public int getPremiumUsersByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[UserPremium] WHERE MONTH(StartDate) = ? AND YEAR(StartDate) = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void addWithConnection(UserPremium userPremium, Connection conn) throws SQLException {
        String sql = "INSERT INTO UserPremium (UserID, PlanID, StartDate, EndDate) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userPremium.getUserID());
            stmt.setInt(2, userPremium.getPlanID());
            stmt.setTimestamp(3, new java.sql.Timestamp(userPremium.getStartDate().getTime()));
            stmt.setTimestamp(4, new java.sql.Timestamp(userPremium.getEndDate().getTime()));
            
            stmt.executeUpdate();
        }
    }

    public static void main(String[] args) throws SQLException {
        UserPremiumDAO d = new UserPremiumDAO();
        System.out.println(d.getPremiumUsersByMonthAndYear(5, 2025));
    }
}
