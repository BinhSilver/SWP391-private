package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Flashcard;
import model.FlashcardItem;

public class FlashcardDAO {
    private Connection connection;

    public FlashcardDAO() {
        try {
            connection = DB.JDBCConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tạo flashcard mới
    public int createFlashcard(Flashcard flashcard) throws SQLException {
        String sql = "INSERT INTO Flashcards (UserID, Title, CreatedAt, UpdatedAt, IsPublic, Description, CoverImage, CourseID) VALUES (?, ?, GETDATE(), GETDATE(), ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, flashcard.getUserID());
            ps.setString(2, flashcard.getTitle());
            ps.setBoolean(3, flashcard.isPublicFlag());
            ps.setString(4, flashcard.getDescription());
            ps.setString(5, flashcard.getCoverImage());
            if (flashcard.getCourseID() == 0) {
                ps.setNull(6, java.sql.Types.INTEGER);
            } else {
                ps.setInt(6, flashcard.getCourseID());
            }
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating flashcard failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating flashcard failed, no ID obtained.");
                }
            }
        }
    }

    // Lấy tất cả flashcard của user
    public List<Flashcard> getFlashcardsByUserID(int userID) throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT * FROM Flashcards WHERE UserID = ? ORDER BY CreatedAt DESC";
        System.out.println("[FlashcardDAO] SQL: " + sql + ", userID=" + userID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    
                    // Xử lý các cột có thể NULL
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    flashcard.setCreatedAt(createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
                    
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    flashcard.setUpdatedAt(updatedAt != null ? updatedAt : new Timestamp(System.currentTimeMillis()));
                    
                    // Xử lý IsPublic có thể NULL
                    Object isPublicObj = rs.getObject("IsPublic");
                    flashcard.setPublicFlag(isPublicObj != null ? rs.getBoolean("IsPublic") : false);
                    
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcards.add(flashcard);
                }
            }
        }
        System.out.println("[FlashcardDAO] Trả về " + flashcards.size() + " flashcard cho userID=" + userID);
        return flashcards;
    }

    // Lấy flashcard theo ID
    public Flashcard getFlashcardByID(int flashcardID) throws SQLException {
        String sql = "SELECT * FROM Flashcards WHERE FlashcardID = ?";
        System.out.println("[FlashcardDAO] SQL: " + sql + ", flashcardID=" + flashcardID);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    
                    // Xử lý các cột có thể NULL
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    flashcard.setCreatedAt(createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
                    
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    flashcard.setUpdatedAt(updatedAt != null ? updatedAt : new Timestamp(System.currentTimeMillis()));
                    
                    // Xử lý IsPublic có thể NULL
                    Object isPublicObj = rs.getObject("IsPublic");
                    flashcard.setPublicFlag(isPublicObj != null ? rs.getBoolean("IsPublic") : false);
                    
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    System.out.println("[FlashcardDAO] Tìm thấy flashcard: " + flashcard.getTitle());
                    return flashcard;
                }
            }
        }
        System.out.println("[FlashcardDAO] Không tìm thấy flashcard với ID=" + flashcardID);
        return null;
    }

    // Cập nhật flashcard
    public boolean updateFlashcard(Flashcard flashcard) throws SQLException {
        String sql = "UPDATE Flashcards SET Title = ?, UpdatedAt = GETDATE(), IsPublic = ?, Description = ?, CoverImage = ? WHERE FlashcardID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, flashcard.getTitle());
            ps.setBoolean(2, flashcard.isPublicFlag());
            ps.setString(3, flashcard.getDescription());
            ps.setString(4, flashcard.getCoverImage());
            ps.setInt(5, flashcard.getFlashcardID());
            
            return ps.executeUpdate() > 0;
        }
    }

    // Xóa flashcard
    public boolean deleteFlashcard(int flashcardID) throws SQLException {
        // Xóa các flashcard items trước
        String deleteItemsSql = "DELETE FROM FlashcardItems WHERE FlashcardID = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteItemsSql)) {
            ps.setInt(1, flashcardID);
            ps.executeUpdate();
        }

        // Xóa flashcard
        String deleteFlashcardSql = "DELETE FROM Flashcards WHERE FlashcardID = ?";
        try (PreparedStatement ps = connection.prepareStatement(deleteFlashcardSql)) {
            ps.setInt(1, flashcardID);
            return ps.executeUpdate() > 0;
        }
    }

    // Lấy flashcard từ khóa học mà user đã đăng ký
    public List<Flashcard> getFlashcardsFromEnrolledCourses(int userID) throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        String sql = "SELECT DISTINCT f.* FROM Flashcards f " +
                    "INNER JOIN FlashcardItems fi ON f.FlashcardID = fi.FlashcardID " +
                    "INNER JOIN Vocabulary v ON fi.VocabID = v.VocabID " +
                    "INNER JOIN Lessons l ON v.LessonID = l.LessonID " +
                    "INNER JOIN Courses c ON l.CourseID = c.CourseID " +
                    "INNER JOIN Enrollment e ON c.CourseID = e.CourseID " +
                    "WHERE e.UserID = ? AND (f.IsPublic = 1 OR f.IsPublic IS NULL) " +
                    "ORDER BY f.CreatedAt DESC";
        System.out.println("[FlashcardDAO] SQL: " + sql + ", userID=" + userID);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    
                    // Xử lý các cột có thể NULL
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    flashcard.setCreatedAt(createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
                    
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    flashcard.setUpdatedAt(updatedAt != null ? updatedAt : new Timestamp(System.currentTimeMillis()));
                    
                    // Xử lý IsPublic có thể NULL
                    Object isPublicObj = rs.getObject("IsPublic");
                    flashcard.setPublicFlag(isPublicObj != null ? rs.getBoolean("IsPublic") : false);
                    
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcards.add(flashcard);
                }
            }
        }
        System.out.println("[FlashcardDAO] Trả về " + flashcards.size() + " flashcard từ khóa học cho userID=" + userID);
        return flashcards;
    }

    // Xóa tất cả flashcard theo courseId
    public void deleteFlashcardsByCourseId(int courseId) throws SQLException {
        String sql = "SELECT FlashcardID FROM Flashcards WHERE CourseID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    deleteFlashcard(rs.getInt("FlashcardID"));
                }
            }
        }
    }
} 