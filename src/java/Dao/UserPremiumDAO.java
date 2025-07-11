package Dao;

import java.sql.*;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;

import DB.JDBCConnection;
import model.UserPremium;
import org.checkerframework.checker.units.qual.A;

public class UserPremiumDAO {

    // Vietnam timezone
    private static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");

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
     * Lấy thông tin premium hiện tại của user (gói có thời hạn muộn nhất)
     * @param userID ID của user
     * @return UserPremium object hoặc null nếu không có
     */
    public UserPremium getCurrentUserPremium(int userID) throws SQLException {
        String sql = "SELECT TOP 1 * FROM UserPremium WHERE UserID = ? ORDER BY EndDate DESC";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserPremium userPremium = new UserPremium();
                    userPremium.setUserID(rs.getInt("UserID"));
                    userPremium.setPlanID(rs.getInt("PlanID"));
                    userPremium.setStartDate(rs.getTimestamp("StartDate"));
                    userPremium.setEndDate(rs.getTimestamp("EndDate"));
                    return userPremium;
                }
            }
        }
        return null;
    }
    
    /**
     * Cộng dồn thời gian premium cho user
     * @param userID ID của user
     * @param planID ID của gói premium mới
     * @param durationInMonths Số tháng cần thêm
     * @param conn Connection để sử dụng trong transaction
     */
    public void extendOrCreatePremium(int userID, int planID, int durationInMonths, Connection conn) throws SQLException {
        // Lấy current time theo timezone Việt Nam
        Calendar calendar = Calendar.getInstance(VIETNAM_TIMEZONE);
        Date currentTime = calendar.getTime();
        
        System.out.println("ExtendOrCreatePremium: UserID=" + userID + ", PlanID=" + planID + ", Duration=" + durationInMonths + " months");
        System.out.println("Current Vietnam time: " + currentTime);
        
        // Lấy premium hiện tại của user
        UserPremium currentPremium = getCurrentUserPremiumWithConnection(userID, conn);
        
        Date startDate;
        Date endDate;
        
        if (currentPremium != null) {
            // User đã có premium, kiểm tra xem còn hạn không
            Date currentEndDate = currentPremium.getEndDate();
            
            System.out.println("Found existing premium - End Date: " + currentEndDate);
            System.out.println("Is expired: " + currentEndDate.before(currentTime));
            
            if (currentEndDate.after(currentTime)) {
                // Còn hạn: cộng dồn từ ngày hết hạn hiện tại
                startDate = currentPremium.getStartDate(); // Giữ nguyên ngày bắt đầu ban đầu
                calendar.setTime(currentEndDate);
                calendar.add(Calendar.MONTH, durationInMonths);
                endDate = calendar.getTime();
                
                System.out.println("Extending existing premium from: " + currentEndDate + " to: " + endDate);
                
                // Update premium hiện tại
                updatePremiumWithConnection(currentPremium.getUserID(), currentPremium.getPlanID(), startDate, endDate, conn);
                System.out.println("✓ Premium updated successfully");
            } else {
                // Hết hạn: tạo mới từ thời điểm hiện tại
                startDate = currentTime;
                calendar.setTime(currentTime);
                calendar.add(Calendar.MONTH, durationInMonths);
                endDate = calendar.getTime();
                
                System.out.println("Creating new premium from: " + startDate + " to: " + endDate);
                
                // Xóa premium cũ và tạo mới
                deletePremiumWithConnection(userID, currentPremium.getPlanID(), conn);
                System.out.println("✓ Old premium deleted");
                addPremiumWithConnection(userID, planID, startDate, endDate, conn);
                System.out.println("✓ New premium created");
            }
        } else {
            // User chưa có premium: tạo mới từ thời điểm hiện tại
            startDate = currentTime;
            calendar.setTime(currentTime);
            calendar.add(Calendar.MONTH, durationInMonths);
            endDate = calendar.getTime();
            
            System.out.println("Creating first premium from: " + startDate + " to: " + endDate);
            
            // Tạo premium mới
            addPremiumWithConnection(userID, planID, startDate, endDate, conn);
            System.out.println("✓ First premium created successfully");
        }
        
        // Verify data was actually saved
        UserPremium verifyPremium = getCurrentUserPremiumWithConnection(userID, conn);
        if (verifyPremium != null) {
            System.out.println("✓ VERIFICATION SUCCESS:");
            System.out.println("  - UserID: " + verifyPremium.getUserID());
            System.out.println("  - PlanID: " + verifyPremium.getPlanID());
            System.out.println("  - StartDate: " + verifyPremium.getStartDate());
            System.out.println("  - EndDate: " + verifyPremium.getEndDate());
        } else {
            System.out.println("✗ VERIFICATION FAILED: No premium found after operation!");
            throw new SQLException("Failed to save premium data to database");
        }
        
        System.out.println("Premium updated successfully for UserID: " + userID);
    }
    
    /**
     * Lấy thông tin premium hiện tại với connection (for transaction)
     */
    private UserPremium getCurrentUserPremiumWithConnection(int userID, Connection conn) throws SQLException {
        String sql = "SELECT TOP 1 * FROM UserPremium WHERE UserID = ? ORDER BY EndDate DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UserPremium userPremium = new UserPremium();
                    userPremium.setUserID(rs.getInt("UserID"));
                    userPremium.setPlanID(rs.getInt("PlanID"));
                    userPremium.setStartDate(rs.getTimestamp("StartDate"));
                    userPremium.setEndDate(rs.getTimestamp("EndDate"));
                    return userPremium;
                }
            }
        }
        return null;
    }
    
    /**
     * Update premium với connection (for transaction)
     */
    private void updatePremiumWithConnection(int userID, int planID, Date startDate, Date endDate, Connection conn) throws SQLException {
        String sql = "UPDATE UserPremium SET StartDate=?, EndDate=? WHERE UserID=? AND PlanID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new Timestamp(endDate.getTime()));
            stmt.setInt(3, userID);
            stmt.setInt(4, planID);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Add premium với connection (for transaction)
     */
    private void addPremiumWithConnection(int userID, int planID, Date startDate, Date endDate, Connection conn) throws SQLException {
        String sql = "INSERT INTO UserPremium (UserID, PlanID, StartDate, EndDate) VALUES (?, ?, ?, ?)";
        
        System.out.println("addPremiumWithConnection - Executing SQL: " + sql);
        System.out.println("Parameters: UserID=" + userID + ", PlanID=" + planID + ", StartDate=" + startDate + ", EndDate=" + endDate);
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, planID);
            stmt.setTimestamp(3, new Timestamp(startDate.getTime()));
            stmt.setTimestamp(4, new Timestamp(endDate.getTime()));
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("addPremiumWithConnection - Rows affected: " + rowsAffected);
            
            if (rowsAffected == 0) {
                throw new SQLException("Failed to insert premium data - no rows affected");
            }
            
            System.out.println("✓ addPremiumWithConnection completed successfully");
        } catch (SQLException e) {
            System.out.println("✗ addPremiumWithConnection failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Delete premium với connection (for transaction)
     */
    private void deletePremiumWithConnection(int userID, int planID, Connection conn) throws SQLException {
        String sql = "DELETE FROM UserPremium WHERE UserID=? AND PlanID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                    Calendar currentCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                    return endDate != null && endDate.before(new Timestamp(currentCal.getTimeInMillis()));
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
