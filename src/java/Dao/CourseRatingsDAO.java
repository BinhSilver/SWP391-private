package Dao;

import java.sql.*;
import model.CourseRating;
import DB.JDBCConnection;

public class CourseRatingsDAO {

    public void add(CourseRating cr) throws SQLException {
        String sql = "INSERT INTO CourseRatings (UserID, CourseID, Rating, Comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cr.getUserID());
            stmt.setInt(2, cr.getCourseID());
            stmt.setInt(3, cr.getRating());
            stmt.setString(4, cr.getComment());
            stmt.executeUpdate();
        }
    }

    public void update(CourseRating cr) throws SQLException {
        String sql = "UPDATE CourseRatings SET UserID=?, CourseID=?, Rating=?, Comment=? WHERE RatingID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cr.getUserID());
            stmt.setInt(2, cr.getCourseID());
            stmt.setInt(3, cr.getRating());
            stmt.setString(4, cr.getComment());
            stmt.setInt(5, cr.getRatingID());
            stmt.executeUpdate();
        }
    }

    public void delete(int ratingID) throws SQLException {
        String sql = "DELETE FROM CourseRatings WHERE RatingID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ratingID);
            stmt.executeUpdate();
        }
    }
}
