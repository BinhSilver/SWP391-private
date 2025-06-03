package Dao;

import DB.JDBCConnection;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import model.Room;

public class RoomDAO {

    private final RoomParticipantDAO participantDAO = new RoomParticipantDAO();

    public void deleteRoomIfEmpty(int roomId) {
        int count = participantDAO.countParticipantsInRoom(roomId);
        if (count == 0) {
            String sql = "DELETE FROM Rooms WHERE RoomID = ?";
            try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
                System.out.println("Room " + roomId + " has been deleted because it's empty.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int createRoom(Room room) {
        String sql = "INSERT INTO Rooms (HostUserID, LanguageLevel, GenderPreference, MinAge, MaxAge, CreatedAt, IsActive) "
                + "OUTPUT INSERTED.RoomID "
                + "VALUES (?, ?, ?, ?, ?, GETDATE(), 1)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getHostUserId());
            stmt.setString(2, room.getLanguageLevel());
            stmt.setString(3, room.getGenderPreference());
            stmt.setInt(4, room.getMinAge());
            stmt.setInt(5, room.getMaxAge());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Room> findMatchingRooms(String gender, int age, String level) {
        List<Room> results = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE IsActive = 1 "
                + "AND (GenderPreference = N'Không yêu cầu' OR GenderPreference = ?) "
                + "AND MinAge <= ? AND MaxAge >= ? "
                + "AND LanguageLevel = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gender);
            stmt.setInt(2, age);
            stmt.setInt(3, age);
            stmt.setString(4, level);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("RoomID"));
                room.setHostUserId(rs.getInt("HostUserID"));
                room.setLanguageLevel(rs.getString("LanguageLevel"));
                room.setGenderPreference(rs.getString("GenderPreference"));
                room.setMinAge(rs.getInt("MinAge"));
                room.setMaxAge(rs.getInt("MaxAge"));
                room.setCreatedAt(rs.getTimestamp("CreatedAt"));
                room.setIsActive(rs.getBoolean("IsActive"));
                results.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

}
