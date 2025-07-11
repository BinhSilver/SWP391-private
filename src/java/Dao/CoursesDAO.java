package Dao;

import java.sql.*;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;
import model.Course;

public class CoursesDAO {

 public static ArrayList<Course> searchCourse(String keyword) {
        ArrayList<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Courses] WHERE dbo.RemoveDiacritics(title) LIKE '%' + dbo.RemoveDiacritics(?) + '%'";

        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course c = new Course();
                c.setCourseID(rs.getInt("courseID"));
                c.setTitle(rs.getString("title"));
                c.setDescription(rs.getString("description"));
                c.setHidden(rs.getBoolean("isHidden"));
                c.setSuggested(rs.getBoolean("isSuggested"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void add(Course c) throws SQLException {
        String sql = "INSERT INTO [dbo].[Courses] (Title, Description, IsHidden, IsSuggested) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.getHidden());
            stmt.setBoolean(4, c.isSuggested());
            stmt.executeUpdate();
            System.out.println("abc");
        }
    }

    public void update(Course c) throws SQLException {
        String sql = "UPDATE [dbo].[Courses] SET Title=?, Description=?, IsHidden=?, IsSuggested=? WHERE CourseID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.getHidden());
            stmt.setBoolean(4, c.isSuggested()); // ✅
            stmt.setInt(5, c.getCourseID());
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

    public int getTotalCourses() throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

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
                course.setTitle(rs.getString("Title"));
                course.setDescription(rs.getString("Description"));
                course.setHidden(rs.getBoolean("IsHidden"));
                course.setSuggested(rs.getBoolean("IsSuggested")); // ✅
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Course> getSuggestedCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT TOP 4 * FROM Courses WHERE isHidden = 0 AND IsSuggested = 1 ORDER BY NEWID()"; // ✅ thêm điều kiện gợi ý
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setCourseID(rs.getInt("courseID"));
                c.setTitle(rs.getString("title"));
                c.setDescription(rs.getString("description"));
                c.setHidden(rs.getBoolean("isHidden"));
                c.setSuggested(rs.getBoolean("isSuggested")); // ✅
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Course getCourseByID(int courseID) {
        String sql = "SELECT * FROM Courses WHERE courseID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("courseID"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getBoolean("isHidden"),
                            rs.getBoolean("isSuggested")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addAndReturnID(Course course) throws SQLException {
        String sql = "INSERT INTO Courses (Title, Description, IsHidden, IsSuggested, CreatedAt) "
                + "VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setBoolean(3, course.getHidden());
            ps.setBoolean(4, course.isSuggested());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về CourseID vừa tạo
            }
        }
        return -1;
    }

    // Thêm các phương thức mới
    public int getTotalEnrollments() throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Enrollment]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    public int getEnrollmentsByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Enrollment] WHERE MONTH(EnrolledAt) = ? AND YEAR(EnrolledAt) = ?";
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

    public int getHiddenCoursesCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] WHERE IsHidden = 1";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Count");
            }
        }
        return 0;
    }

    public int getCoursesByYear(int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] WHERE YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
            }
        }
        return 0;
    }


}
