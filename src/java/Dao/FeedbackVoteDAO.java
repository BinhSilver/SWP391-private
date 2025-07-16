package Dao;

import model.FeedbackVote;
import java.sql.*;

public class FeedbackVoteDAO {
    private Connection conn;
    public FeedbackVoteDAO(Connection conn) { this.conn = conn; }

    public FeedbackVote getVote(int feedbackId, int userId) throws SQLException {
        String sql = "SELECT * FROM FeedbackVotes WHERE FeedbackID = ? AND UserID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, feedbackId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new FeedbackVote(
                rs.getInt("VoteID"),
                rs.getInt("FeedbackID"),
                rs.getInt("UserID"),
                rs.getInt("VoteType")
            );
        }
        return null;
    }

    public void addVote(int feedbackId, int userId, int voteType) throws SQLException {
        String sql = "INSERT INTO FeedbackVotes (FeedbackID, UserID, VoteType) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, feedbackId);
        ps.setInt(2, userId);
        ps.setInt(3, voteType);
        ps.executeUpdate();
    }

    public void updateVote(int feedbackId, int userId, int voteType) throws SQLException {
        String sql = "UPDATE FeedbackVotes SET VoteType = ? WHERE FeedbackID = ? AND UserID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, voteType);
        ps.setInt(2, feedbackId);
        ps.setInt(3, userId);
        ps.executeUpdate();
    }

    public void deleteVote(int feedbackId, int userId) throws SQLException {
        String sql = "DELETE FROM FeedbackVotes WHERE FeedbackID = ? AND UserID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, feedbackId);
        ps.setInt(2, userId);
        ps.executeUpdate();
    }

    public int countVotes(int feedbackId, int voteType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM FeedbackVotes WHERE FeedbackID = ? AND VoteType = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, feedbackId);
        ps.setInt(2, voteType);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
} 