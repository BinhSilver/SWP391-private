package dao;

import java.sql.*;
import model.Enrollment;
import DB.JDBCConnection;

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
}
