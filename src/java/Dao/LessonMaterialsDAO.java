package Dao;

import java.sql.*;
import model.LessonMaterial;
import DB.JDBCConnection;
import java.util.*;

public class LessonMaterialsDAO {

    // ✅ Thêm mới tài liệu
    public void add(LessonMaterial m) throws SQLException {
        String sql = "INSERT INTO LessonMaterials (LessonID, MaterialType, FileType, Title, FilePath, IsHidden) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getLessonID());
            stmt.setString(2, m.getMaterialType());
            stmt.setString(3, m.getFileType());
            stmt.setString(4, m.getTitle());
            stmt.setString(5, m.getFilePath());
            stmt.setBoolean(6, m.isIsHidden());
            stmt.executeUpdate();
        }
    }

    // ✅ Cập nhật tài liệu
    public void update(LessonMaterial m) throws SQLException {
        String sql = "UPDATE LessonMaterials SET LessonID=?, MaterialType=?, FileType=?, Title=?, FilePath=?, IsHidden=? WHERE MaterialID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, m.getLessonID());
            stmt.setString(2, m.getMaterialType());
            stmt.setString(3, m.getFileType());
            stmt.setString(4, m.getTitle());
            stmt.setString(5, m.getFilePath());
            stmt.setBoolean(6, m.isIsHidden());
            stmt.setInt(7, m.getMaterialID());
            stmt.executeUpdate();
        }
    }

    // ✅ Xóa tài liệu
    public void delete(int materialID) throws SQLException {
        String sql = "DELETE FROM LessonMaterials WHERE MaterialID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, materialID);
            stmt.executeUpdate();
        }
    }

    // ✅ Lấy tài liệu theo bài học
    public List<LessonMaterial> getMaterialsByLessonID(int lessonID) {
        List<LessonMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM LessonMaterials WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String rawType = rs.getString("MaterialType");
                String fileType = rs.getString("FileType");
                String title = rs.getString("Title");

                // Phân loại thông minh
                String displayType = classifyMaterialType(rawType, fileType, title);

                LessonMaterial material = new LessonMaterial(
                        rs.getInt("MaterialID"),
                        rs.getInt("LessonID"),
                        displayType,
                        fileType,
                        title,
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

    // ✅ Lấy tất cả tài liệu trong một khóa học, nhóm theo lessonID
    public Map<Integer, List<LessonMaterial>> getAllMaterialsGroupedByLesson(int courseId) {
        Map<Integer, List<LessonMaterial>> map = new HashMap<>();
        String sql = "SELECT lm.* FROM LessonMaterials lm "
                + "JOIN Lessons l ON lm.LessonID = l.LessonID "
                + "WHERE l.CourseID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int lessonId = rs.getInt("LessonID");
                String rawType = rs.getString("MaterialType");
                String fileType = rs.getString("FileType");
                String title = rs.getString("Title");

                // Phân loại thông minh
                String displayType = classifyMaterialType(rawType, fileType, title);

                LessonMaterial material = new LessonMaterial(
                        rs.getInt("MaterialID"),
                        lessonId,
                        displayType,
                        fileType,
                        title,
                        rs.getString("FilePath"),
                        rs.getBoolean("IsHidden"),
                        rs.getTimestamp("CreatedAt")
                );
                map.computeIfAbsent(lessonId, k -> new ArrayList<>()).add(material);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // ✅ Phân loại thông minh (từ tên và định dạng file)
    private String classifyMaterialType(String rawType, String fileType, String title) {
        String titleLower = title.toLowerCase();

        if ("PDF".equalsIgnoreCase(fileType)) {
            if (titleLower.contains("từ vựng")) {
                return "Từ vựng";
            }
            if (titleLower.contains("ngữ pháp")) {
                return "Ngữ pháp";
            }
            if (titleLower.contains("kanji")) {
                return "Kanji";
            }
        }
        if ("Video".equalsIgnoreCase(fileType)) {
            if (titleLower.contains("ngữ pháp")) {
                return "Ngữ pháp";
            }
        }

        // Mặc định giữ nguyên nếu không phân loại được
        return rawType;
    }

    // ✅ Dùng tiện lợi trong servlet
    public static List<LessonMaterial> getByLessonId(int lessonId) {
        LessonMaterialsDAO dao = new LessonMaterialsDAO();
        return dao.getMaterialsByLessonID(lessonId);
    }
}
