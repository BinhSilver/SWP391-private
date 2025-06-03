package dao;

import model.Role;
import DB.JDBCConnection;
import java.sql.*;
import java.util.*;

public class RoleDAO {
    public List<Role> getAllRoles() {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles";
        try (Connection conn = JDBCConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Role(rs.getInt("RoleID"), rs.getString("RoleName")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void insert(Role r) {
        String sql = "INSERT INTO Roles (RoleID, RoleName) VALUES (?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getRoleID());
            ps.setString(2, r.getRoleName());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Role r) {
        String sql = "UPDATE Roles SET RoleName=? WHERE RoleID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.setInt(2, r.getRoleID());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String sql = "DELETE FROM Roles WHERE RoleID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Role getById(int id) {
        String sql = "SELECT * FROM Roles WHERE RoleID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
