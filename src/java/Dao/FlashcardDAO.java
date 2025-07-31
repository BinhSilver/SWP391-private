package Dao;

// ===== IMPORT STATEMENTS =====
import java.sql.*;                          // SQL database operations
import java.util.ArrayList;                 // ArrayList collection
import java.util.List;                      // List collection
import java.util.stream.Collectors;         // Stream operations
import model.Flashcard;                     // Flashcard model
import model.FlashcardItem;                 // FlashcardItem model

// ===== FLASHCARD DATA ACCESS OBJECT =====
/**
 * FlashcardDAO - Data Access Object cho Flashcards
 * Quản lý tất cả các thao tác CRUD với bảng Flashcards trong database
 */
public class FlashcardDAO {
    
    // ===== INSTANCE VARIABLES =====
    private Connection connection;           // Database connection

    // ===== CONSTRUCTOR =====
    /**
     * Khởi tạo FlashcardDAO và thiết lập kết nối database
     */
    public FlashcardDAO() {
        try {
            // Lấy connection từ JDBCConnection pool
            connection = DB.JDBCConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== CREATE FLASHCARD =====
    /**
     * Tạo flashcard mới trong database
     * @param flashcard Flashcard object cần tạo
     * @return ID của flashcard vừa tạo
     * @throws SQLException nếu có lỗi database
     */
    public int createFlashcard(Flashcard flashcard) throws SQLException {
        // SQL query để insert flashcard mới
        String sql = "INSERT INTO Flashcards (UserID, Title, CreatedAt, UpdatedAt, IsPublic, Description, CoverImage, CourseID) VALUES (?, ?, GETDATE(), GETDATE(), ?, ?, ?, ?)";
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set các tham số cho prepared statement
            ps.setInt(1, flashcard.getUserID());           // UserID
            ps.setString(2, flashcard.getTitle());         // Title
            ps.setBoolean(3, flashcard.isPublicFlag());    // IsPublic
            ps.setString(4, flashcard.getDescription());   // Description
            ps.setString(5, flashcard.getCoverImage());    // CoverImage
            
            // Xử lý CourseID có thể null
            if (flashcard.getCourseID() == 0) {
                ps.setNull(6, java.sql.Types.INTEGER);    // CourseID = null
            } else {
                ps.setInt(6, flashcard.getCourseID());     // CourseID
            }
            
            // Thực thi query và lấy số rows bị ảnh hưởng
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating flashcard failed, no rows affected.");
            }
            
            // Lấy ID được generate tự động
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);        // Trả về FlashcardID
                } else {
                    throw new SQLException("Creating flashcard failed, no ID obtained.");
                }
            }
        }
    }

    // ===== GET FLASHCARDS BY USER ID =====
    /**
     * Lấy tất cả flashcard của một user cụ thể
     * @param userID ID của user
     * @return List các flashcard của user
     * @throws SQLException nếu có lỗi database
     */
    public List<Flashcard> getFlashcardsByUserID(int userID) throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        
        // SQL query để lấy flashcards theo UserID
        String sql = "SELECT * FROM Flashcards WHERE UserID = ? ORDER BY CreatedAt DESC";
        System.out.println("[FlashcardDAO] SQL: " + sql + ", userID=" + userID);
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);  // Set UserID parameter
            
            try (ResultSet rs = ps.executeQuery()) {
                // Lặp qua kết quả và tạo Flashcard objects
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    
                    // ===== HANDLE NULLABLE TIMESTAMP FIELDS =====
                    // Xử lý các cột có thể NULL
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    flashcard.setCreatedAt(createdAt != null ? createdAt : new Timestamp(System.currentTimeMillis()));
                    
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
                    flashcard.setUpdatedAt(updatedAt != null ? updatedAt : new Timestamp(System.currentTimeMillis()));
                    
                    // ===== HANDLE NULLABLE BOOLEAN FIELD =====
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

    // ===== GET FLASHCARD BY ID =====
    /**
     * Lấy flashcard theo ID
     * @param flashcardID ID của flashcard cần lấy
     * @return Flashcard object hoặc null nếu không tìm thấy
     * @throws SQLException nếu có lỗi database
     */
    public Flashcard getFlashcardByID(int flashcardID) throws SQLException {
        // SQL query để lấy flashcard theo ID
        String sql = "SELECT * FROM Flashcards WHERE FlashcardID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardID);  // Set FlashcardID parameter
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Tạo Flashcard object từ ResultSet
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcard.setPublicFlag(rs.getBoolean("IsPublic"));
                    flashcard.setCourseID(rs.getInt("CourseID"));
                    flashcard.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    flashcard.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    return flashcard;
                }
            }
        }
        return null;  // Không tìm thấy flashcard
    }

    // ===== UPDATE FLASHCARD =====
    /**
     * Cập nhật thông tin flashcard
     * @param flashcard Flashcard object cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi database
     */
    public boolean updateFlashcard(Flashcard flashcard) throws SQLException {
        // SQL query để update flashcard
        String sql = "UPDATE Flashcards SET Title = ?, Description = ?, CoverImage = ?, IsPublic = ?, CourseID = ?, UpdatedAt = GETDATE() WHERE FlashcardID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set các tham số cho prepared statement
            ps.setString(1, flashcard.getTitle());         // Title
            ps.setString(2, flashcard.getDescription());   // Description
            ps.setString(3, flashcard.getCoverImage());    // CoverImage
            ps.setBoolean(4, flashcard.isPublicFlag());    // IsPublic
            ps.setInt(5, flashcard.getCourseID());         // CourseID
            ps.setInt(6, flashcard.getFlashcardID());      // FlashcardID
            
            // Thực thi query và trả về kết quả
            return ps.executeUpdate() > 0;
        }
    }

    // ===== DELETE FLASHCARD =====
    /**
     * Xóa flashcard khỏi database
     * @param flashcardID ID của flashcard cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     * @throws SQLException nếu có lỗi database
     */
    public boolean deleteFlashcard(int flashcardID) throws SQLException {
        // SQL query để xóa flashcard
        String sql = "DELETE FROM Flashcards WHERE FlashcardID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flashcardID);  // Set FlashcardID parameter
            
            // Thực thi query và trả về kết quả
            return ps.executeUpdate() > 0;
        }
    }

    // ===== GET FLASHCARDS FROM ENROLLED COURSES =====
    /**
     * Lấy flashcards từ các khóa học mà user đã đăng ký
     * @param userID ID của user
     * @return List các flashcard từ khóa học đã đăng ký
     * @throws SQLException nếu có lỗi database
     */
    public List<Flashcard> getFlashcardsFromEnrolledCourses(int userID) throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        
        // SQL query để lấy flashcards từ khóa học đã đăng ký
        String sql = "SELECT f.* FROM Flashcards f " +
                    "INNER JOIN Enrollment e ON f.CourseID = e.CourseID " +
                    "WHERE e.UserID = ? AND f.IsPublic = 1 " +
                    "ORDER BY f.CreatedAt DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);  // Set UserID parameter
            
            try (ResultSet rs = ps.executeQuery()) {
                // Lặp qua kết quả và tạo Flashcard objects
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcard.setPublicFlag(rs.getBoolean("IsPublic"));
                    flashcard.setCourseID(rs.getInt("CourseID"));
                    flashcard.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    flashcard.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    flashcards.add(flashcard);
                }
            }
        }
        return flashcards;
    }

    // ===== GET ALL ACCESSIBLE FLASHCARDS =====
    /**
     * Lấy tất cả flashcards mà user có thể truy cập
     * Bao gồm: flashcards của user + flashcards public từ khóa học đã đăng ký
     * @param userID ID của user
     * @return List tất cả flashcards có thể truy cập
     * @throws SQLException nếu có lỗi database
     */
    public List<Flashcard> getAllAccessibleFlashcards(int userID) throws SQLException {
        List<Flashcard> flashcards = new ArrayList<>();
        
        // SQL query để lấy flashcards có thể truy cập
        String sql = "SELECT * FROM Flashcards WHERE UserID = ? " +
                    "UNION " +
                    "SELECT * FROM Flashcards WHERE IsPublic = 1 AND CourseID IN (SELECT CourseID FROM Enrollment WHERE UserID = ?) AND UserID <> ? " +
                    "ORDER BY CreatedAt DESC";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);  // UserID cho flashcards của user
            ps.setInt(2, userID);  // UserID cho enrollment check
            ps.setInt(3, userID);  // UserID để loại trừ flashcards của chính user
            
            try (ResultSet rs = ps.executeQuery()) {
                // Lặp qua kết quả và tạo Flashcard objects
                while (rs.next()) {
                    Flashcard flashcard = new Flashcard();
                    flashcard.setFlashcardID(rs.getInt("FlashcardID"));
                    flashcard.setUserID(rs.getInt("UserID"));
                    flashcard.setTitle(rs.getString("Title"));
                    flashcard.setDescription(rs.getString("Description"));
                    flashcard.setCoverImage(rs.getString("CoverImage"));
                    flashcard.setPublicFlag(rs.getBoolean("IsPublic"));
                    flashcard.setCourseID(rs.getInt("CourseID"));
                    flashcard.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    flashcard.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    flashcards.add(flashcard);
                }
            }
        }
        return flashcards;
    }

    // ===== DELETE FLASHCARDS BY COURSE ID =====
    /**
     * Xóa tất cả flashcards của một khóa học
     * @param courseId ID của khóa học
     * @throws SQLException nếu có lỗi database
     */
    public void deleteFlashcardsByCourseId(int courseId) throws SQLException {
        // SQL query để xóa flashcards theo CourseID
        String sql = "DELETE FROM Flashcards WHERE CourseID = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, courseId);  // Set CourseID parameter
            ps.executeUpdate();
        }
    }
} 
