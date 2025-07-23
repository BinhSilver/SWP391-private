package Dao;

import model.Feedback;
import java.sql.*;
import java.util.*;

public class FeedbackDAO {
    private Connection conn;
    public FeedbackDAO(Connection conn) { this.conn = conn; }

    public List<Feedback> getFeedbacksByCourseId(int courseId) throws SQLException {
        String sql = "SELECT f.*, u.fullName, u.avatar, " +
                "SUM(CASE WHEN v.VoteType = 1 THEN 1 ELSE 0 END) AS totalLikes, " +
                "SUM(CASE WHEN v.VoteType = -1 THEN 1 ELSE 0 END) AS totalDislikes " +
                "FROM Feedbacks f " +
                "JOIN Users u ON f.UserID = u.UserID " +
                "LEFT JOIN FeedbackVotes v ON f.FeedbackID = v.FeedbackID " +
                "WHERE f.CourseID = ? " +
                "GROUP BY f.FeedbackID, f.UserID, f.CourseID, f.Content, f.Rating, f.CreatedAt, u.fullName, u.avatar " +
                "ORDER BY f.CreatedAt DESC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        List<Feedback> list = new ArrayList<>();
        while (rs.next()) {
            Feedback f = new Feedback();
            f.setFeedbackID(rs.getInt("FeedbackID"));
            f.setUserID(rs.getInt("UserID"));
            f.setCourseID(rs.getInt("CourseID"));
            f.setContent(rs.getString("Content"));
            f.setRating(rs.getInt("Rating"));
            f.setCreatedAt(rs.getTimestamp("CreatedAt"));
            f.setUserName(rs.getString("fullName"));
            f.setUserAvatar(rs.getString("avatar"));
            f.setTotalLikes(rs.getInt("totalLikes"));
            f.setTotalDislikes(rs.getInt("totalDislikes"));
            list.add(f);
        }
        return list;
    }

    public Feedback getFeedbackByUserAndCourse(int userId, int courseId) throws SQLException {
        String sql = "SELECT * FROM Feedbacks WHERE UserID = ? AND CourseID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setInt(2, courseId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Feedback f = new Feedback();
            f.setFeedbackID(rs.getInt("FeedbackID"));
            f.setUserID(rs.getInt("UserID"));
            f.setCourseID(rs.getInt("CourseID"));
            f.setContent(rs.getString("Content"));
            f.setRating(rs.getInt("Rating"));
            f.setCreatedAt(rs.getTimestamp("CreatedAt"));
            return f;
        }
        return null;
    }

    public void addFeedback(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO Feedbacks (UserID, CourseID, Content, Rating, CreatedAt) VALUES (?, ?, ?, ?, GETDATE())";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, feedback.getUserID());
        ps.setInt(2, feedback.getCourseID());
        ps.setString(3, feedback.getContent());
        ps.setInt(4, feedback.getRating());
        ps.executeUpdate();
    }

    public void updateFeedback(Feedback feedback) throws SQLException {
        // Chỉ update nội dung và rating, KHÔNG động chạm gì đến bảng vote
        String sql = "UPDATE Feedbacks SET Content = ?, Rating = ? WHERE FeedbackID = ? AND UserID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, feedback.getContent());
        ps.setInt(2, feedback.getRating());
        ps.setInt(3, feedback.getFeedbackID());
        ps.setInt(4, feedback.getUserID());
        ps.executeUpdate();
    }

    public void deleteFeedback(int feedbackId, int userId) throws SQLException {
        // Xóa vote trước
        String sqlVote = "DELETE FROM FeedbackVotes WHERE FeedbackID = ?";
        PreparedStatement psVote = conn.prepareStatement(sqlVote);
        psVote.setInt(1, feedbackId);
        psVote.executeUpdate();
        // Xóa feedback
        String sql = "DELETE FROM Feedbacks WHERE FeedbackID = ? AND UserID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, feedbackId);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }

    public double getAverageRatingByCourseId(int courseId) throws SQLException {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) AS avgRating FROM Feedbacks WHERE CourseID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getDouble("avgRating");
        }
        return 0;
    }
} 