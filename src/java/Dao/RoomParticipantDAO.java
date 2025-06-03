package dao;

import DB.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.RoomParticipant;

public class RoomParticipantDAO {

 public void addParticipant(RoomParticipant participant) {
        String sql = "INSERT INTO RoomParticipants (RoomID, UserID, JoinedAt) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, participant.getRoomID());
            stmt.setInt(2, participant.getUserID());
            stmt.setTimestamp(3, new java.sql.Timestamp(participant.getJoinedAt().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding participant to room");
        }
    }

    public void removeParticipant(int roomId, int userId) {
        String sql = "DELETE FROM RoomParticipants WHERE RoomID = ? AND UserID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countParticipantsInRoom(int roomId) {
        String sql = "SELECT COUNT(*) FROM RoomParticipants WHERE RoomID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}
