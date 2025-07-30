package Dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

    // Lấy tất cả flashcard mà user có thể truy cập (của mình và của các khóa học đã join, chỉ public)
    public List<Flashcard> getAllAccessibleFlashcards(int userID) throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        
        // Lấy flashcard của chính user
        String userFlashcardsSql = "SELECT * FROM Flashcards WHERE UserID = ?";
        List<Flashcard> userFlashcards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(userFlashcardsSql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    flashcard.setCreatedAt(createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    flashcard.setUpdatedAt(updatedAt != null ? updatedAt : new Timestamp(System.currentTimeMillis()));
                    Object isPublicObj = rs.getObject("IsPublic");
                    flashcard.setPublicFlag(isPublicObj != null ? rs.getBoolean("IsPublic") : false);
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcard.setCourseID(rs.getObject("CourseID") != null ? rs.getInt("CourseID") : null);
                    userFlashcards.add(flashcard);
                }
            }
        }
        
        // Lấy flashcard public từ các khóa học user đã join
        String courseFlashcardsSql = "SELECT * FROM Flashcards WHERE IsPublic = 1 AND CourseID IN (SELECT CourseID FROM Enrollment WHERE UserID = ?) AND UserID <> ?";
        List<Flashcard> courseFlashcards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(courseFlashcardsSql)) {
            ps.setInt(1, userID);
            ps.setInt(2, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    flashcard.setCreatedAt(createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    flashcard.setUpdatedAt(updatedAt != null ? updatedAt : new Timestamp(System.currentTimeMillis()));
                    Object isPublicObj = rs.getObject("IsPublic");
                    flashcard.setPublicFlag(isPublicObj != null ? rs.getBoolean("IsPublic") : false);
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcard.setCourseID(rs.getObject("CourseID") != null ? rs.getInt("CourseID") : null);
                    courseFlashcards.add(flashcard);
                }
            }
        }
        
        // Gộp danh sách và sắp xếp
        flashcards.addAll(userFlashcards);
        flashcards.addAll(courseFlashcards);
        flashcards.sort((f1, f2) -> f2.getCreatedAt().compareTo(f1.getCreatedAt()));
        
        // Log chi tiết
        System.out.println("=== [FlashcardDAO] LOG CHI TIẾT CHO USER " + userID + " ===");
        System.out.println("[FlashcardDAO] Số flashcard của user: " + userFlashcards.size());
        for (Flashcard f : userFlashcards) {
            System.out.println("  - Flashcard ID: " + f.getFlashcardID() + ", Title: " + f.getTitle() + 
                             ", IsPublic: " + f.isPublicFlag() + ", CourseID: " + f.getCourseID());
        }
        
        System.out.println("[FlashcardDAO] Số flashcard từ khóa học đã join: " + courseFlashcards.size());
        for (Flashcard f : courseFlashcards) {
            System.out.println("  - Flashcard ID: " + f.getFlashcardID() + ", Title: " + f.getTitle() + 
                             ", Owner UserID: " + f.getUserID() + ", CourseID: " + f.getCourseID());
        }
        
        // Kiểm tra enrollment
        String enrollmentSql = "SELECT CourseID FROM Enrollment WHERE UserID = ?";
        List<Integer> enrolledCourses = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(enrollmentSql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    enrolledCourses.add(rs.getInt("CourseID"));
                }
            }
        }
        System.out.println("[FlashcardDAO] User đã join các khóa học: " + enrolledCourses);
        
        // Kiểm tra flashcard public của các khóa học đã join
        if (!enrolledCourses.isEmpty()) {
            String publicFlashcardsSql = "SELECT FlashcardID, Title, CourseID, UserID, IsPublic FROM Flashcards WHERE CourseID IN (" + 
                                        enrolledCourses.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")";
            System.out.println("[FlashcardDAO] Tất cả flashcard của các khóa học đã join:");
            try (PreparedStatement ps = connection.prepareStatement(publicFlashcardsSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("  - Flashcard ID: " + rs.getInt("FlashcardID") + 
                                         ", Title: " + rs.getString("Title") + 
                                         ", CourseID: " + rs.getInt("CourseID") + 
                                         ", Owner UserID: " + rs.getInt("UserID") + 
                                         ", IsPublic: " + rs.getBoolean("IsPublic"));
                    }
                }
            }
        }
        
        System.out.println("[FlashcardDAO] Tổng số flashcard có thể truy cập: " + flashcards.size());
        System.out.println("=== [FlashcardDAO] KẾT THÚC LOG ===");
        
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