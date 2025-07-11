package Dao;

import java.sql.*;
import model.LessonMaterial;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;

public class LessonMaterialsDAO {

    public void add(LessonMaterial m) throws SQLException {
        String sql = "INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath, IsHidden) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getLessonID());
            stmt.setString(2, m.getMaterialType());
            stmt.setString(3, m.getFileType()); // ✅ Thêm FileType
            stmt.setString(4, m.getTitle());
            stmt.setString(5, m.getFilePath());
            stmt.setBoolean(6, m.isIsHidden());
            stmt.executeUpdate();
        }
    }

    public void update(LessonMaterial m) throws SQLException {
        String sql = "UPDATE LessonMaterials SET LessonID=?, MaterialType=?, FileType=?, Title=?, FilePath=?, IsHidden=? WHERE MaterialID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getLessonID());
            stmt.setString(2, m.getMaterialType());
            stmt.setString(3, m.getFileType()); // ✅ Thêm FileType
            stmt.setString(4, m.getTitle());
            stmt.setString(5, m.getFilePath());
            stmt.setBoolean(6, m.isIsHidden());
            stmt.setInt(7, m.getMaterialID());
            stmt.executeUpdate();
        }
    }

    public void delete(int materialID) throws SQLException {
        String sql = "DELETE FROM LessonMaterials WHERE MaterialID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialID);
            stmt.executeUpdate();
        }
    }

    public List<LessonMaterial> getMaterialsByLessonID(int lessonID) {
        List<LessonMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM LessonMaterials WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LessonMaterial material = new LessonMaterial(
                        rs.getInt("MaterialID"),
                        rs.getInt("LessonID"),
                        rs.getString("MaterialType"),
                        rs.getString("FileType"),
                        rs.getString("Title"),
                        rs.getString("FilePath"),
                        rs.getBoolean("IsHidden"),
                        rs.getTimestamp("CreatedAt")
                );
                list.add(material);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
