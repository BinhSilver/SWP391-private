package Dao;

import DB.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.PremiumPlan;

public class PremiumPlanDAO {
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    public List<PremiumPlan> getAllPremiumPlans() {
        List<PremiumPlan> list = new ArrayList<>();
        String query = "SELECT * FROM PremiumPlans";
        try {
            conn = new JDBCConnection().getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new PremiumPlan(
                    rs.getInt("PlanID"),
                    rs.getString("PlanName"),
                    rs.getDouble("Price"),
                    rs.getInt("DurationInMonths"),
                    rs.getString("Description")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public boolean addPremiumPlan(String planName, double price, int durationInMonths, String description) {
        String query = "INSERT INTO PremiumPlans (PlanName, Price, DurationInMonths, Description) VALUES (?, ?, ?, ?)";
        try {
            conn = new JDBCConnection().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, planName);
            ps.setDouble(2, price);
            ps.setInt(3, durationInMonths);
            ps.setString(4, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean updatePremiumPlan(int planID, String planName, double price, int durationInMonths, String description) {
        String query = "UPDATE PremiumPlans SET PlanName=?, Price=?, DurationInMonths=?, Description=? WHERE PlanID=?";
        try {
            conn = new JDBCConnection().getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, planName);
            ps.setDouble(2, price);
            ps.setInt(3, durationInMonths);
            ps.setString(4, description);
            ps.setInt(5, planID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean deletePremiumPlan(int planID) {
        String query = "DELETE FROM PremiumPlans WHERE PlanID=?";
        try {
            conn = new JDBCConnection().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, planID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public PremiumPlan getPremiumPlanByID(int planID) {
        String query = "SELECT * FROM PremiumPlans WHERE PlanID=?";
        try {
            conn = new JDBCConnection().getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, planID);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new PremiumPlan(
                    rs.getInt("PlanID"),
                    rs.getString("PlanName"),
                    rs.getDouble("Price"),
                    rs.getInt("DurationInMonths"),
                    rs.getString("Description")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
} 