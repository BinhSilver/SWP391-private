package Dao;

import DB.JDBCConnection;
import java.sql.*;

public class ProgressDAO {

    // Lấy % hoàn thành của 1 bài học cho user (nếu chưa có => trả 0)
    public int getLessonCompletionPercent(int userId, int lessonId) {
        String sql = "SELECT CompletionPercent FROM Progress WHERE UserID=? AND LessonID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, lessonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("CompletionPercent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Insert/Update tiến trình học
    public void upsertLessonProgress(int userId, int courseId, int lessonId, int percent) {
        String sqlCheck = "SELECT COUNT(*) FROM Progress WHERE UserID=? AND LessonID=?";
        String sqlUpdate = "UPDATE Progress SET CompletionPercent=?, LastAccessed=GETDATE() WHERE UserID=? AND LessonID=?";
        String sqlInsert = "INSERT INTO Progress (UserID, CourseID, LessonID, CompletionPercent) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection()) {
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setInt(1, userId);
            psCheck.setInt(2, lessonId);
            ResultSet rs = psCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                psUpdate.setInt(1, percent);
                psUpdate.setInt(2, userId);
                psUpdate.setInt(3, lessonId);
                psUpdate.executeUpdate();
            } else {
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setInt(1, userId);
                psInsert.setInt(2, courseId);
                psInsert.setInt(3, lessonId);
                psInsert.setInt(4, percent);
                psInsert.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
