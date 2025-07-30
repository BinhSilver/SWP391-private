package Dao;

import java.sql.*;
import model.Enrollment;
import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EnrollmentDAO {

    public void add(Enrollment e) throws SQLException {
        String sql = "INSERT INTO Enrollment (UserID, CourseID) VALUES (?, ?)";
        System.out.println("=== [EnrollmentDAO] USER " + e.getUserID() + " JOIN KHÓA HỌC " + e.getCourseID() + " ===");
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, e.getUserID());
            stmt.setInt(2, e.getCourseID());
            int result = stmt.executeUpdate();
            System.out.println("[EnrollmentDAO] Kết quả thêm enrollment: " + result + " bản ghi");
            System.out.println("[EnrollmentDAO] User " + e.getUserID() + " đã join thành công khóa học " + e.getCourseID());
        } catch (SQLException ex) {
            System.out.println("[EnrollmentDAO] Lỗi khi thêm enrollment: " + ex.getMessage());
            throw ex;
        }
    }

    public void update(Enrollment e) throws SQLException {
        String sql = "UPDATE Enrollment SET UserID=?, CourseID=? WHERE EnrollmentID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, e.getUserID());
            stmt.setInt(2, e.getCourseID());
            stmt.setInt(3, e.getEnrollmentID());
            stmt.executeUpdate();
        }
    }

    public void delete(int enrollmentID) throws SQLException {
        String sql = "DELETE FROM Enrollment WHERE EnrollmentID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentID);
            stmt.executeUpdate();
        }
    }
    public JsonArray getEnrollmentsByPeriod(String periodType) {
        JsonArray jsonArray = new JsonArray();
        String sql = periodType.equals("month") ?
                "SELECT FORMAT(EnrolledAt, 'yyyy-MM') AS Period, COUNT(*) AS EnrollmentCount " +
                "FROM [dbo].[Enrollment] GROUP BY FORMAT(EnrolledAt, 'yyyy-MM') ORDER BY Period" :
                "SELECT YEAR(EnrolledAt) AS Period, COUNT(*) AS EnrollmentCount " +
                "FROM [dbo].[Enrollment] GROUP BY YEAR(EnrolledAt) ORDER BY Period";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", rs.getString("Period"));
                obj.addProperty("count", rs.getInt("EnrollmentCount"));
                jsonArray.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    // Kiểm tra user đã join vào khóa học chưa
    public boolean isUserEnrolled(int userID, int courseID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Enrollment WHERE UserID = ? AND CourseID = ?";
        System.out.println("=== [EnrollmentDAO] KIỂM TRA USER " + userID + " ĐÃ JOIN KHÓA HỌC " + courseID + " ===");
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.setInt(2, courseID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean isEnrolled = rs.getInt(1) > 0;
                    System.out.println("[EnrollmentDAO] User " + userID + " đã " + (isEnrolled ? "đã" : "chưa") + " join khóa học " + courseID);
                    return isEnrolled;
                }
            }
        } catch (SQLException ex) {
            System.out.println("[EnrollmentDAO] Lỗi khi kiểm tra enrollment: " + ex.getMessage());
            throw ex;
        }
        System.out.println("[EnrollmentDAO] User " + userID + " chưa join khóa học " + courseID);
        return false;
    }
}
