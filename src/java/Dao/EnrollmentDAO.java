package dao;

import java.sql.*;
import model.Enrollment;
import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class EnrollmentDAO {

    public void add(Enrollment e) throws SQLException {
        String sql = "INSERT INTO Enrollment (UserID, CourseID) VALUES (?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, e.getUserID());
            stmt.setInt(2, e.getCourseID());
            stmt.executeUpdate();
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
}
