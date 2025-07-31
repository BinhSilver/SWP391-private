package service;

import model.UserPremium;
import model.PremiumPlan;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import DB.JDBCConnection;

/**
 * PremiumService - Service để quản lý quyền premium và giới hạn user
 * 
 * Chức năng chính:
 * - Kiểm tra trạng thái premium của user
 * - Kiểm tra giới hạn flashcard cho user free
 * - Kiểm tra giới hạn item trong flashcard
 * - Quản lý quyền sử dụng video call và AI call
 */
public class PremiumService {
    
    private static final int FREE_FLASHCARD_LIMIT_PER_WEEK = 2;
    private static final int FREE_ITEM_LIMIT_PER_FLASHCARD = 10;
    
    /**
     * Kiểm tra user có premium không
     * @param userID ID của user
     * @return true nếu user có premium còn hạn, false nếu không
     */
    public boolean isUserPremium(int userID) {
        // Kiểm tra role của user trước
        if (isTeacherOrAdmin(userID)) {
            return true; // Giáo viên và admin luôn có quyền premium
        }
        
        String sql = "SELECT up.*, pp.PlanName FROM UserPremium up " +
                    "INNER JOIN PremiumPlans pp ON up.PlanID = pp.PlanID " +
                    "WHERE up.UserID = ? AND up.EndDate > GETDATE() " +
                    "ORDER BY up.EndDate DESC";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            return rs.next(); // Có premium nếu có kết quả
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra user có phải là giáo viên hoặc admin không
     * @param userID ID của user
     * @return true nếu là giáo viên (roleID = 3) hoặc admin (roleID = 4)
     */
    private boolean isTeacherOrAdmin(int userID) {
        String sql = "SELECT RoleID FROM Users WHERE UserID = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int roleID = rs.getInt("RoleID");
                return roleID == 3 || roleID == 4; // 3: Teacher, 4: Admin
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy thông tin premium hiện tại của user
     * @param userID ID của user
     * @return UserPremium object hoặc null nếu không có premium
     */
    public UserPremium getCurrentPremium(int userID) {
        // Nếu là giáo viên hoặc admin, trả về premium giả
        if (isTeacherOrAdmin(userID)) {
            UserPremium premium = new UserPremium();
            premium.setUserID(userID);
            premium.setPlanID(0); // Plan đặc biệt cho teacher/admin
            premium.setStartDate(new java.util.Date());
            premium.setEndDate(new java.util.Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000)); // 1 năm
            return premium;
        }
        
        String sql = "SELECT up.*, pp.PlanName FROM UserPremium up " +
                    "INNER JOIN PremiumPlans pp ON up.PlanID = pp.PlanID " +
                    "WHERE up.UserID = ? AND up.EndDate > GETDATE() " +
                    "ORDER BY up.EndDate DESC";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                UserPremium premium = new UserPremium();
                premium.setUserID(rs.getInt("UserID"));
                premium.setPlanID(rs.getInt("PlanID"));
                premium.setStartDate(rs.getDate("StartDate"));
                premium.setEndDate(rs.getDate("EndDate"));
                return premium;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Kiểm tra user có thể tạo flashcard mới không (dựa trên giới hạn)
     * @param userID ID của user
     * @return true nếu có thể tạo, false nếu đã hết giới hạn
     */
    public boolean canCreateFlashcard(int userID) {
        // Premium user không bị giới hạn
        if (isUserPremium(userID)) {
            return true;
        }
        
        // Kiểm tra giới hạn cho free user
        return getFlashcardCountThisWeek(userID) < FREE_FLASHCARD_LIMIT_PER_WEEK;
    }
    
    /**
     * Đếm số flashcard đã tạo trong tuần này
     * @param userID ID của user
     * @return Số flashcard đã tạo trong tuần
     */
    public int getFlashcardCountThisWeek(int userID) {
        String sql = "SELECT COUNT(*) FROM Flashcards " +
                    "WHERE UserID = ? AND CreatedAt >= DATEADD(day, -7, GETDATE())";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Kiểm tra user có thể thêm item vào flashcard không
     * @param userID ID của user
     * @param flashcardID ID của flashcard
     * @return true nếu có thể thêm, false nếu đã hết giới hạn
     */
    public boolean canAddFlashcardItem(int userID, int flashcardID) {
        // Premium user không bị giới hạn
        if (isUserPremium(userID)) {
            return true;
        }
        
        // Kiểm tra giới hạn cho free user
        return getFlashcardItemCount(flashcardID) < FREE_ITEM_LIMIT_PER_FLASHCARD;
    }
    
    /**
     * Đếm số item trong flashcard
     * @param flashcardID ID của flashcard
     * @return Số item trong flashcard
     */
    public int getFlashcardItemCount(int flashcardID) {
        String sql = "SELECT COUNT(*) FROM FlashcardItems WHERE FlashcardID = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, flashcardID);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Kiểm tra user có thể sử dụng video call không
     * @param userID ID của user
     * @return true nếu có thể sử dụng, false nếu không
     */
    public boolean canUseVideoCall(int userID) {
        return isUserPremium(userID);
    }
    
    /**
     * Kiểm tra user có thể sử dụng AI call không
     * @param userID ID của user
     * @return true nếu có thể sử dụng, false nếu không
     */
    public boolean canUseAICall(int userID) {
        return isUserPremium(userID);
    }
    
    /**
     * Lấy thông tin giới hạn cho user
     * @param userID ID của user
     * @return String mô tả giới hạn hiện tại
     */
    public String getLimitInfo(int userID) {
        if (isTeacherOrAdmin(userID)) {
            return "Teacher/Admin - Không giới hạn";
        }
        
        if (isUserPremium(userID)) {
            return "Premium User - Không giới hạn";
        }
        
        int flashcardCount = getFlashcardCountThisWeek(userID);
        return String.format("Free User - Đã tạo %d/%d flashcard trong tuần này", 
                           flashcardCount, FREE_FLASHCARD_LIMIT_PER_WEEK);
    }
    
    /**
     * Lấy thông tin giới hạn item cho flashcard
     * @param userID ID của user
     * @param flashcardID ID của flashcard
     * @return String mô tả giới hạn item
     */
    public String getItemLimitInfo(int userID, int flashcardID) {
        if (isTeacherOrAdmin(userID)) {
            return "Teacher/Admin - Không giới hạn item";
        }
        
        if (isUserPremium(userID)) {
            return "Premium User - Không giới hạn item";
        }
        
        int itemCount = getFlashcardItemCount(flashcardID);
        return String.format("Free User - Đã có %d/%d item trong flashcard này", 
                           itemCount, FREE_ITEM_LIMIT_PER_FLASHCARD);
    }
} 