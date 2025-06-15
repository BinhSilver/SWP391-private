package Dao;

import java.sql.*;
import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import model.Course;

public class CoursesDAO {

    public static ArrayList<Course> searchCourse(String keyword) {
        ArrayList<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Courses] WHERE title LIKE ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseID(rs.getInt("courseID"));
                c.setTitle(rs.getString("title"));
                c.setDescription(rs.getString("description"));
                c.setIsHidden(rs.getBoolean("isHidden"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void add(Course c) throws SQLException {
        String sql = "INSERT INTO [dbo].[Courses] (Title, Description, IsHidden) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.isIsHidden());
            stmt.executeUpdate();
        }
    }

    public void update(Course c) throws SQLException {
        String sql = "UPDATE [dbo].[Courses] SET Title=?, Description=?, IsHidden=? WHERE CourseID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.isIsHidden());
            stmt.setInt(4, c.getCourseID());
            stmt.executeUpdate();
        }
    }

    public void delete(int courseID) throws SQLException {
        String sql = "DELETE FROM [dbo].[Courses] WHERE CourseID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseID);
            stmt.executeUpdate();
        }
    }

    // New method to get total courses
    public int getTotalCourses() throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    // New method to get course count for a specific month and year
    public int getCoursesByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] WHERE MONTH(CreatedAt) = ? AND YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
            }
        }
        return 0;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseID(rs.getInt("CourseID"));
                course.setTitle(rs.getString("Title")); // Hoặc rs.getNString()
                course.setDescription(rs.getString("Description"));
                course.setIsHidden(rs.getBoolean("IsHidden"));
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Course> getSuggestedCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT TOP 4 * FROM Courses WHERE isHidden = 0 ORDER BY NEWID()"; // random gợi ý
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseID(rs.getInt("courseID"));
                c.setTitle(rs.getString("title"));
                c.setDescription(rs.getString("description"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
