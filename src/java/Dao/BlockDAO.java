package Dao;

import DB.JDBCConnection;
import java.sql.*;

public class BlockDAO {
    public boolean blockUser(int blockerId, int blockedId) throws SQLException {
        String sql = "INSERT INTO Blocks (BlockerID, BlockedID) VALUES (?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean unblockUser(int blockerId, int blockedId) throws SQLException {
        String sql = "DELETE FROM Blocks WHERE BlockerID = ? AND BlockedID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, blockerId);
            stmt.setInt(2, blockedId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isBlocked(int user1Id, int user2Id) throws SQLException {
        String sql = "SELECT 1 FROM Blocks WHERE (BlockerID = ? AND BlockedID = ?) OR (BlockerID = ? AND BlockedID = ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.setInt(3, user2Id);
            stmt.setInt(4, user1Id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}