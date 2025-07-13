package Dao;

import java.sql.*;
import model.Lesson;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;

public class LessonsDAO {

    // Thêm bài học
    public void add(Lesson l) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, Description, IsHidden, OrderIndex) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setString(3, l.getDescription());
            stmt.setBoolean(4, l.isIsHidden());
            stmt.setInt(5, l.getOrderIndex());
            stmt.executeUpdate();
        }
    }

    // Cập nhật bài học
    public void update(Lesson l) throws SQLException {
        String sql = "UPDATE Lessons SET CourseID=?, Title=?, Description=?, IsHidden=?, OrderIndex=? WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setString(3, l.getDescription());
            stmt.setBoolean(4, l.isIsHidden());
            stmt.setInt(5, l.getOrderIndex());
            stmt.setInt(6, l.getLessonID());
            stmt.executeUpdate();
        }
    }

    // Xóa bài học
    public void delete(int lessonID) throws SQLException {
        String sql = "DELETE FROM Lessons WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonID);
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách bài học theo CourseID
    public List<Lesson> getLessonsByCourseID(int courseID) {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM Lessons WHERE CourseID = ? ORDER BY OrderIndex ASC";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Lesson(
                        rs.getInt("LessonID"),
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getBoolean("IsHidden"),
                        rs.getString("Description"),
                        rs.getInt("OrderIndex")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 bài học theo ID
    public static Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM Lessons WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Lesson(
                        rs.getInt("LessonID"),
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getBoolean("IsHidden"),
                        rs.getString("Description"),
                        rs.getInt("OrderIndex")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm bài học và trả về ID vừa tạo
    public int addAndReturnID(Lesson lesson) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, Description, IsHidden, OrderIndex) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, lesson.getCourseID());
            stmt.setString(2, lesson.getTitle());
            stmt.setString(3, lesson.getDescription());
            stmt.setBoolean(4, lesson.isIsHidden());
            stmt.setInt(5, lesson.getOrderIndex());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Insert Lesson failed, no ID obtained.");
    }
}
