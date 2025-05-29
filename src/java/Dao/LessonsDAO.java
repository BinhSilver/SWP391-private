package dao;

import java.sql.*;
import model.Lesson;
import DB.JDBCConnection;

public class LessonsDAO {

    public void add(Lesson l) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, IsHidden) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setBoolean(3, l.isIsHidden());
            stmt.executeUpdate();
        }
    }

    public void update(Lesson l) throws SQLException {
        String sql = "UPDATE Lessons SET CourseID=?, Title=?, IsHidden=? WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setBoolean(3, l.isIsHidden());
            stmt.setInt(4, l.getLessonID());
            stmt.executeUpdate();
        }
    }

    public void delete(int lessonID) throws SQLException {
        String sql = "DELETE FROM Lessons WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonID);
            stmt.executeUpdate();
        }
    }
}
