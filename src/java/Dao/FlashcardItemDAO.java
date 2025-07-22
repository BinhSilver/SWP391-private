package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.FlashcardItem;

public class FlashcardItemDAO {
    private Connection connection;

    public FlashcardItemDAO() {
        try {
            connection = DB.JDBCConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tạo flashcard item mới
    public int createFlashcardItem(FlashcardItem item) throws SQLException {
        String sql = "INSERT INTO FlashcardItems (FlashcardID, VocabID, UserVocabID, Note, FrontContent, BackContent, FrontImage, BackImage, OrderIndex) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Debug: In ra thông tin trước khi lưu vào database
        System.out.println("[FlashcardItemDAO] Lưu item vào DB: " +
                        "FlashcardID=" + item.getFlashcardID() +
                        ", FrontContent='" + item.getFrontContent() + "'" +
                        ", BackContent='" + item.getBackContent() + "'" +
                        ", Note='" + item.getNote() + "'" +
                        ", FrontImage='" + item.getFrontImage() + "'" +
                        ", BackImage='" + item.getBackImage() + "'" +
                        ", OrderIndex=" + item.getOrderIndex());
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getFlashcardID());
            ps.setObject(2, item.getVocabID());
            ps.setObject(3, item.getUserVocabID());
            ps.setString(4, item.getNote());
            ps.setString(5, item.getFrontContent());
            ps.setString(6, item.getBackContent());
            ps.setString(7, item.getFrontImage());
            ps.setString(8, item.getBackImage());
            ps.setInt(9, item.getOrderIndex());
            
            int affectedRows = ps.executeUpdate();
            System.out.println("[FlashcardItemDAO] Số dòng bị ảnh hưởng: " + affectedRows);
            
            if (affectedRows == 0) {
                throw new SQLException("Creating flashcard item failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    System.out.println("[FlashcardItemDAO] Tạo thành công item với ID: " + newId);
                    return newId;
                } else {
                    throw new SQLException("Creating flashcard item failed, no ID obtained.");
                }
            }
        }
    }

    // Lấy tất cả items của một flashcard
    public List<FlashcardItem> getFlashcardItemsByFlashcardID(int flashcardID) throws SQLException {
        List<FlashcardItem> items = new ArrayList<>();
        String sql = "SELECT * FROM FlashcardItems WHERE FlashcardID = ? ORDER BY OrderIndex ASC";
        
        System.out.println("[FlashcardItemDAO] SQL: " + sql + ", flashcardID=" + flashcardID);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FlashcardItem item = new FlashcardItem();
                    item.setFlashcardItemID(rs.getInt("FlashcardItemID"));
                    item.setFlashcardID(rs.getInt("FlashcardID"));
                    item.setVocabID(rs.getObject("VocabID") != null ? rs.getInt("VocabID") : null);
                    item.setUserVocabID(rs.getObject("UserVocabID") != null ? rs.getInt("UserVocabID") : null);
                    item.setNote(rs.getString("Note"));
                    item.setFrontContent(rs.getString("FrontContent"));
                    item.setBackContent(rs.getString("BackContent"));
                    item.setFrontImage(rs.getString("FrontImage"));
                    item.setBackImage(rs.getString("BackImage"));
                    item.setOrderIndex(rs.getInt("OrderIndex"));
                    
                    // Debug: In ra thông tin chi tiết về item được đọc từ database
                    System.out.println("[FlashcardItemDAO] Đọc item từ DB: " +
                                    "ID=" + item.getFlashcardItemID() +
                                    ", FrontContent='" + item.getFrontContent() + "'" +
                                    ", BackContent='" + item.getBackContent() + "'" +
                                    ", Note='" + item.getNote() + "'" +
                                    ", FrontImage='" + item.getFrontImage() + "'" +
                                    ", BackImage='" + item.getBackImage() + "'" +
                                    ", OrderIndex=" + item.getOrderIndex());
                    
                    items.add(item);
                }
            }
        }
        
        System.out.println("[FlashcardItemDAO] Tổng số items đọc được: " + items.size());
        return items;
    }

    // Lấy flashcard item theo ID
    public FlashcardItem getFlashcardItemByID(int flashcardItemID) throws SQLException {
        String sql = "SELECT * FROM FlashcardItems WHERE FlashcardItemID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardItemID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FlashcardItem item = new FlashcardItem();
                    item.setFlashcardItemID(rs.getInt("FlashcardItemID"));
                    item.setFlashcardID(rs.getInt("FlashcardID"));
                    item.setVocabID(rs.getObject("VocabID") != null ? rs.getInt("VocabID") : null);
                    item.setUserVocabID(rs.getObject("UserVocabID") != null ? rs.getInt("UserVocabID") : null);
                    item.setNote(rs.getString("Note"));
                    item.setFrontContent(rs.getString("FrontContent"));
                    item.setBackContent(rs.getString("BackContent"));
                    item.setFrontImage(rs.getString("FrontImage"));
                    item.setBackImage(rs.getString("BackImage"));
                    item.setOrderIndex(rs.getInt("OrderIndex"));
                    return item;
                }
            }
        }
        return null;
    }

    // Cập nhật flashcard item
    public boolean updateFlashcardItem(FlashcardItem item) throws SQLException {
        String sql = "UPDATE FlashcardItems SET VocabID = ?, UserVocabID = ?, Note = ?, FrontContent = ?, BackContent = ?, FrontImage = ?, BackImage = ?, OrderIndex = ? WHERE FlashcardItemID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, item.getVocabID());
            ps.setObject(2, item.getUserVocabID());
            ps.setString(3, item.getNote());
            ps.setString(4, item.getFrontContent());
            ps.setString(5, item.getBackContent());
            ps.setString(6, item.getFrontImage());
            ps.setString(7, item.getBackImage());
            ps.setInt(8, item.getOrderIndex());
            ps.setInt(9, item.getFlashcardItemID());
            
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa flashcard item
    public boolean deleteFlashcardItem(int flashcardItemID) throws SQLException {
        String sql = "DELETE FROM FlashcardItems WHERE FlashcardItemID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardItemID);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy số lượng items trong một flashcard
    public int getItemCountByFlashcardID(int flashcardID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM FlashcardItems WHERE FlashcardID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Cập nhật thứ tự của các items
    public boolean updateItemOrder(int flashcardItemID, int newOrderIndex) throws SQLException {
        String sql = "UPDATE FlashcardItems SET OrderIndex = ? WHERE FlashcardItemID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newOrderIndex);
            ps.setInt(2, flashcardItemID);
            return ps.executeUpdate() > 0;
        }
    }
} 