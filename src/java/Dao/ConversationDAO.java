package Dao;

import DB.JDBCConnection;
import model.Conversation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversationDAO {
    public int createConversation(int user1Id, int user2Id) throws SQLException {
        String sql = "INSERT INTO [dbo].[Conversations] (User1ID, User2ID) VALUES (?, ?); SELECT SCOPE_IDENTITY() AS ConversationID";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.min(user1Id, user2Id)); // Đảm bảo User1ID < User2ID để tránh trùng lặp
            stmt.setInt(2, Math.max(user1Id, user2Id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ConversationID");
            }
        }
        return -1;
    }

    public Conversation getConversation(int user1Id, int user2Id) throws SQLException {
        String sql = "SELECT * FROM [dbo].[Conversations] WHERE (User1ID = ? AND User2ID = ?) OR (User1ID = ? AND User2ID = ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.min(user1Id, user2Id));
            stmt.setInt(2, Math.max(user1Id, user2Id));
            stmt.setInt(3, Math.max(user1Id, user2Id));
            stmt.setInt(4, Math.min(user1Id, user2Id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Conversation conv = new Conversation();
                conv.setConversationId(rs.getInt("ConversationID"));
                conv.setUser1Id(rs.getInt("User1ID"));
                conv.setUser2Id(rs.getInt("User2ID"));
                conv.setCreatedAt(rs.getTimestamp("CreatedAt"));
                return conv;
            }
        }
        return null;
    }

    public List<Conversation> getConversationsByUserId(int userId) throws SQLException {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Conversations] WHERE User1ID = ? OR User2ID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Conversation conv = new Conversation();
                conv.setConversationId(rs.getInt("ConversationID"));
                conv.setUser1Id(rs.getInt("User1ID"));
                conv.setUser2Id(rs.getInt("User2ID"));
                conv.setCreatedAt(rs.getTimestamp("CreatedAt"));
                conversations.add(conv);
            }
        }
        return conversations;
    }
}