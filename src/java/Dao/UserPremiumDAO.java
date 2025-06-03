package dao;

import java.sql.*;

import DB.JDBCConnection;
import model.UserPremium;

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
}
