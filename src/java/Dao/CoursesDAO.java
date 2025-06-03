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

        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.isIsHidden());
            stmt.executeUpdate();
        }
    }

    public void update(Course c) throws SQLException {
        String sql = "UPDATE [dbo].[Courses] SET Title=?, Description=?, IsHidden=? WHERE CourseID=?";
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.isIsHidden());
            stmt.setInt(4, c.getCourseID());
            stmt.executeUpdate();
        }
    }

    public void delete(int courseID) throws SQLException {
        String sql = "DELETE FROM [dbo].[Courses] WHERE CourseID=?";
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseID);
            stmt.executeUpdate();
        }
    }

    public List<Course> getAllCoursesforchatbox() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course();
                course.setCourseID(rs.getInt("CourseID"));
                course.setTitle(rs.getNString("Title"));
                course.setDescription(rs.getNString("Description"));
                courses.add(course);
            }
        }
        return courses;
    }

    public JsonArray getAllCoursesforchatboxt() {
        JsonArray jsonArray = new JsonArray();
        String sql = "SELECT * FROM Courses";

        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("CourseID", rs.getInt("CourseID"));
                obj.addProperty("Title", rs.getNString("Title"));
                obj.addProperty("Description", rs.getNString("Description"));
                jsonArray.add(obj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static void main(String[] args) throws SQLException {
        CoursesDAO testcourse = new CoursesDAO();
        test.Testcase.printlist(testcourse.searchCourse("Tiếng Nhật Sơ Cấp N5: Khởi đầu hoàn hảo"));
    }
}
