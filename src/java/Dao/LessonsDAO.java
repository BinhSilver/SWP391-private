package Dao;

import java.sql.*;
import model.Lesson;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;

public class LessonsDAO {

    public void add(Lesson l) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, IsHidden) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setBoolean(3, l.isIsHidden());
            stmt.executeUpdate();
        }
    }

    public void update(Lesson l) throws SQLException {
        String sql = "UPDATE Lessons SET CourseID=?, Title=?, IsHidden=? WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setBoolean(3, l.isIsHidden());
            stmt.setInt(4, l.getLessonID());
            stmt.executeUpdate();
        }
    }

    public void delete(int lessonID) throws SQLException {
        String sql = "DELETE FROM Lessons WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonID);
            stmt.executeUpdate();
        }
    }

    public List<Lesson> getLessonsByCourseID(int courseID) {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM Lessons WHERE CourseID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Lesson(
                        rs.getInt("LessonID"),
                        courseID,
                        rs.getString("Title"),
                        rs.getBoolean("IsHidden")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Bổ sung để sử dụng trong StudyLessonServlet
    public static Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM Lessons WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Lesson(
                        rs.getInt("LessonID"),
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getBoolean("IsHidden")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int addAndReturnID(Lesson lesson) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, IsHidden) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lesson.getCourseID());
            stmt.setString(2, lesson.getTitle());
            stmt.setBoolean(3, lesson.isIsHidden());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về LessonID vừa tạo
            }
        }
        throw new SQLException("Insert Lesson failed, no ID obtained.");
    }
}
