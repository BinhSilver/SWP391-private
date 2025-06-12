package Dao;

import DB.JDBCConnection;
import model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public int createRoom(Room room) {
        String sql = "INSERT INTO Rooms(HostUserID, LanguageLevel, GenderPreference, MinAge, MaxAge, AllowApproval, IsActive) "
                + "OUTPUT INSERTED.RoomID VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = JDBCConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, room.getHostUserID());
            ps.setString(2, room.getLanguageLevel());
            ps.setString(3, room.getGenderPreference());
            ps.setInt(4, room.getMinAge());
            ps.setInt(5, room.getMaxAge());
            ps.setBoolean(6, room.isAllowApproval());
            ps.setBoolean(7, room.isActive());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoomID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE RoomID = ? AND IsActive = 1";
        try (Connection con = JDBCConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setRoomID(rs.getInt("RoomID"));
                    room.setHostUserID(rs.getInt("HostUserID"));
                    room.setLanguageLevel(rs.getString("LanguageLevel"));
                    room.setGenderPreference(rs.getString("GenderPreference"));
                    room.setMinAge(rs.getInt("MinAge"));
                    room.setMaxAge(rs.getInt("MaxAge"));
                    room.setAllowApproval(rs.getBoolean("AllowApproval"));
                    room.setIsActive(rs.getBoolean("IsActive"));
                    return room;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

public boolean deleteRoom(int roomId, int hostUserId) {
    String checkRoom = "SELECT HostUserID FROM Rooms WHERE RoomID = ?";
    String deleteJoinRequests = "DELETE FROM JoinRequests WHERE RoomID = ?";
    String deleteRoom = "DELETE FROM Rooms WHERE RoomID = ? AND HostUserID = ?";
    
    try (Connection con = JDBCConnection.getConnection()) {
        con.setAutoCommit(false);
        
        // Kiểm tra phòng tồn tại và host đúng
        try (PreparedStatement psCheck = con.prepareStatement(checkRoom)) {
            psCheck.setInt(1, roomId);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next() || rs.getInt("HostUserID") != hostUserId) {
                con.rollback();
                System.err.println("Room " + roomId + " not found or user " + hostUserId + " is not host");
                return false;
            }
        }

        // Xóa JoinRequests
        try (PreparedStatement ps1 = con.prepareStatement(deleteJoinRequests)) {
            ps1.setInt(1, roomId);
            ps1.executeUpdate();
        }

        // Xóa Room
        try (PreparedStatement ps2 = con.prepareStatement(deleteRoom)) {
            ps2.setInt(1, roomId);
            ps2.setInt(2, hostUserId);
            int affectedRows = ps2.executeUpdate();
            if (affectedRows > 0) {
                con.commit();
                return true;
            } else {
                con.rollback();
                System.err.println("No room deleted for RoomID " + roomId);
                return false;
            }
        } catch (SQLException e) {
            con.rollback();
            System.err.println("Error deleting room " + roomId + ": " + e.getMessage());
            throw e;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public List<Room> getAvailableRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Rooms] WHERE IsActive = 1";
        try (Connection con = JDBCConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomID(rs.getInt("RoomID"));
                room.setHostUserID(rs.getInt("HostUserID"));
                room.setLanguageLevel(rs.getString("LanguageLevel"));
                room.setGenderPreference(rs.getString("GenderPreference"));
                room.setMinAge(rs.getInt("MinAge"));
                room.setMaxAge(rs.getInt("MaxAge"));
                room.setAllowApproval(rs.getBoolean("AllowApproval"));
                room.setIsActive(rs.getBoolean("IsActive"));
                list.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Room> findMatchingRooms(String gender, int age, String level) {
        List<Room> matches = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE IsActive = 1 AND (GenderPreference = ? OR GenderPreference = 'Không xác định') "
                + "AND ? BETWEEN MinAge AND MaxAge AND LanguageLevel = ?";
        try (Connection con = JDBCConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, gender != null ? gender : "Không xác định");
            ps.setInt(2, age >= 0 ? age : 0);
            ps.setString(3, level != null ? level : "");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room();
                    room.setRoomID(rs.getInt("RoomID"));
                    room.setHostUserID(rs.getInt("HostUserID"));
                    room.setLanguageLevel(rs.getString("LanguageLevel"));
                    room.setGenderPreference(rs.getString("GenderPreference"));
                    room.setMinAge(rs.getInt("MinAge"));
                    room.setMaxAge(rs.getInt("MaxAge"));
                    room.setAllowApproval(rs.getBoolean("AllowApproval"));
                    room.setIsActive(rs.getBoolean("IsActive"));
                    matches.add(room);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    public static void main(String[] args) {
        RoomDAO a = new RoomDAO();
        test.Testcase.printlist(a.getAvailableRooms());
    }
}
