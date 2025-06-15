package Dao;

import java.sql.*;
import model.LessonMaterial;
import DB.JDBCConnection;

public class LessonMaterialsDAO {

    public void add(LessonMaterial m) throws SQLException {
        String sql = "INSERT INTO LessonMaterials (LessonID, MaterialType, Title, FilePath, IsHidden) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getLessonID());
            stmt.setString(2, m.getMaterialType());
            stmt.setString(3, m.getTitle());
            stmt.setString(4, m.getFilePath());
            stmt.setBoolean(5, m.isIsHidden());
            stmt.executeUpdate();
        }
    }

    public void update(LessonMaterial m) throws SQLException {
        String sql = "UPDATE LessonMaterials SET LessonID=?, MaterialType=?, Title=?, FilePath=?, IsHidden=? WHERE MaterialID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getLessonID());
            stmt.setString(2, m.getMaterialType());
            stmt.setString(3, m.getTitle());
            stmt.setString(4, m.getFilePath());
            stmt.setBoolean(5, m.isIsHidden());
            stmt.setInt(6, m.getMaterialID());
            stmt.executeUpdate();
        }
    }

    public void delete(int materialID) throws SQLException {
        String sql = "DELETE FROM LessonMaterials WHERE MaterialID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialID);
            stmt.executeUpdate();
        }
    }
}
