package Dao;

import DB.JDBCConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.PremiumPlan;

public class PremiumPlanDAO {
    
    /**
     * Lấy thời hạn (số tháng) của gói premium
     * @param planId ID của gói premium
     * @return số tháng của gói premium
     */
    public int getPlanDuration(int planId) throws SQLException {
        String sql = "SELECT DurationInMonths FROM PremiumPlans WHERE PlanID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, planId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("DurationInMonths");
                }
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy plan
    }
    
    /**
     * Lấy danh sách tất cả các gói premium
     */
    public List<PremiumPlan> getAllPremiumPlans() throws SQLException {
        List<PremiumPlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM PremiumPlans";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PremiumPlan plan = new PremiumPlan();
                plan.setPlanID(rs.getInt("PlanID"));
                plan.setPlanName(rs.getString("PlanName"));
                plan.setPrice(rs.getDouble("Price"));
                plan.setDurationInMonths(rs.getInt("DurationInMonths"));
                plan.setDescription(rs.getString("Description"));
                plans.add(plan);
            }
        }
        return plans;
    }
    
    /**
     * Lấy thông tin gói premium theo ID
     */
    public PremiumPlan getPremiumPlanByID(int planId) throws SQLException {
        String sql = "SELECT * FROM PremiumPlans WHERE PlanID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, planId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PremiumPlan plan = new PremiumPlan();
                    plan.setPlanID(rs.getInt("PlanID"));
                    plan.setPlanName(rs.getString("PlanName"));
                    plan.setPrice(rs.getDouble("Price"));
                    plan.setDurationInMonths(rs.getInt("DurationInMonths"));
                    plan.setDescription(rs.getString("Description"));
                    return plan;
                }
            }
        }
        return null;
    }

    public PremiumPlan getPlanById(int planId) throws SQLException {
        String sql = "SELECT * FROM PremiumPlans WHERE PlanID = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PremiumPlan plan = new PremiumPlan();
                    plan.setPlanID(rs.getInt("PlanID"));
                    plan.setPlanName(rs.getString("PlanName"));
                    plan.setPrice(rs.getDouble("Price"));
                    plan.setDurationInMonths(rs.getInt("DurationInMonths"));
                    plan.setDescription(rs.getString("Description"));
                    return plan;
                }
            }
        }
        return null;
    }
    
    /**
     * Thêm gói premium mới
     */
    public boolean addPremiumPlan(String planName, double price, int durationInMonths, String description) throws SQLException {
        String sql = "INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, planName);
            stmt.setDouble(2, price);
            stmt.setInt(3, durationInMonths);
            stmt.setString(4, description);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật thông tin gói premium
     */
    public boolean updatePremiumPlan(int planId, String planName, double price, int durationInMonths, String description) throws SQLException {
        String sql = "UPDATE PremiumPlans SET PlanName=?, Price=?, DurationInMonths=?, Description=? WHERE PlanID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, planName);
            stmt.setDouble(2, price);
            stmt.setInt(3, durationInMonths);
            stmt.setString(4, description);
            stmt.setInt(5, planId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa gói premium
     */
    public boolean deletePremiumPlan(int planId) throws SQLException {
        String sql = "DELETE FROM PremiumPlans WHERE PlanID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, planId);
            return stmt.executeUpdate() > 0;
        }
    }
} 