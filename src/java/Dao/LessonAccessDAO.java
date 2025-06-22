package Dao;

import DB.JDBCConnection;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class LessonAccessDAO extends JDBCConnection {

    // Ghi nhận lần truy cập nếu chưa có
    public void recordAccess(int userId, int lessonId) {
        String sql = """
            MERGE LessonAccess AS target
            USING (SELECT ? AS UserID, ? AS LessonID) AS source
            ON (target.UserID = source.UserID AND target.LessonID = source.LessonID)
            WHEN NOT MATCHED THEN
                INSERT (UserID, LessonID) VALUES (source.UserID, source.LessonID);
        """;
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, lessonId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách các bài học đã truy cập của người dùng
    public Set<Integer> getAccessedLessons(int userId) {
        Set<Integer> result = new HashSet<>();
        String sql = "SELECT LessonID FROM LessonAccess WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getInt("LessonID"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
