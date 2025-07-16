package Dao;

import DB.JDBCConnection;
import model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public int saveMessage(int conversationId, int senderId, String content, String type) throws SQLException {
        String sql = "INSERT INTO [dbo].[Messages] (ConversationID, SenderID, Content, Type) VALUES (?, ?, ?, ?); SELECT SCOPE_IDENTITY() AS MessageID";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conversationId);
            stmt.setInt(2, senderId);
            stmt.setString(3, content);
            stmt.setString(4, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MessageID");
            }
        }
        return -1;
    }

    public List<Message> getMessagesByConversationId(int conversationId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Messages] WHERE ConversationID = ? ORDER BY SentAt";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, conversationId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message();
                msg.setMessageId(rs.getInt("MessageID"));
                msg.setConversationId(rs.getInt("ConversationID"));
                msg.setSenderId(rs.getInt("SenderID"));
                msg.setContent(rs.getString("Content"));
                msg.setType(rs.getString("Type"));
                msg.setRead(rs.getBoolean("IsRead"));
                msg.setRecall(rs.getBoolean("IsRecall"));
                msg.setSentAt(rs.getTimestamp("SentAt"));
                messages.add(msg);
            }
        }
        return messages;
    }

    public void markMessagesAsRead(int senderId, int receiverId) throws SQLException {
        String sql = "UPDATE [dbo].[Messages] SET IsRead = 1 WHERE SenderID = ? AND ConversationID IN " +
                    "(SELECT ConversationID FROM Conversations WHERE (User1ID = ? AND User2ID = ?) OR (User1ID = ? AND User2ID = ?))";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, Math.min(senderId, receiverId));
            stmt.setInt(3, Math.max(senderId, receiverId));
            stmt.setInt(4, Math.max(senderId, receiverId));
            stmt.setInt(5, Math.min(senderId, receiverId));
            stmt.executeUpdate();
        }
    }

    public void recallMessage(int messageId, int userId) throws SQLException {
        String sql = "UPDATE [dbo].[Messages] SET IsRecall = 1 WHERE MessageID = ? AND SenderID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getUsersWithUnreadMessages(int userId) throws SQLException {
        List<Integer> senderIds = new ArrayList<>();
        String sql = "SELECT DISTINCT SenderID FROM Messages m JOIN Conversations c ON m.ConversationID = c.ConversationID " +
                    "WHERE (c.User1ID = ? OR c.User2ID = ?) AND m.IsRead = 0 AND m.SenderID != ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                senderIds.add(rs.getInt("SenderID"));
            }
        }
        return senderIds;
    }
}