package Dao;

import DB.JDBCConnection;
import java.sql.*;
import model.PremiumPlan;

public class PremiumPlansDAO {

    public void add(PremiumPlan plan) throws SQLException {
        String sql = "INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plan.getPlanName());
            stmt.setDouble(2, plan.getPrice());
            stmt.setInt(3, plan.getDurationInMonths());
            stmt.setString(4, plan.getDescription());
            stmt.executeUpdate();
        }
    }

    public void update(PremiumPlan plan) throws SQLException {
        String sql = "UPDATE PremiumPlans SET PlanName=?, Price=?, DurationInMonths=?, Description=? WHERE PlanID=?";
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plan.getPlanName());
            stmt.setDouble(2, plan.getPrice());
            stmt.setInt(3, plan.getDurationInMonths());
            stmt.setString(4, plan.getDescription());
            stmt.setInt(5, plan.getPlanID());
            stmt.executeUpdate();
        }
    }

    public void delete(int planID) throws SQLException {
        String sql = "DELETE FROM PremiumPlans WHERE PlanID=?";
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, planID);
            stmt.executeUpdate();
        }
    }
}
