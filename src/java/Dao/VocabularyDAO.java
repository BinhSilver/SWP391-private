package Dao;

// ===== IMPORT STATEMENTS =====
import java.sql.*;                          // SQL database operations
import java.util.ArrayList;                 // ArrayList collection
import model.Vocabulary;                    // Vocabulary model
import DB.JDBCConnection;                   // Database connection utility
import org.checkerframework.checker.units.qual.A;  // Checker framework annotation

// ===== VOCABULARY DATA ACCESS OBJECT =====
/**
 * VocabularyDAO - Data Access Object cho Vocabulary
 * Quản lý tất cả các thao tác CRUD với bảng Vocabulary trong database
 * Bao gồm: thêm, sửa, xóa, tìm kiếm từ vựng theo lesson
 */
public class VocabularyDAO {
    
    // ===== GET VOCABULARY BY LESSON ID =====
    /**
     * Lấy tất cả từ vựng của một lesson cụ thể
     * @param lessonId ID của lesson cần lấy từ vựng
     * @return ArrayList các từ vựng của lesson
     * @throws SQLException nếu có lỗi database
     */
    public static ArrayList<Vocabulary> getVocabularyByLessonId(int lessonId) throws SQLException {
        ArrayList<Vocabulary> list = new ArrayList<>();
        
        // SQL query để lấy từ vựng theo LessonID
        String sql = "SELECT * FROM Vocabulary WHERE LessonID = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, lessonId);  // Set LessonID parameter
            
            // Thực thi query và xử lý kết quả
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Tạo Vocabulary object từ ResultSet
                Vocabulary v = new Vocabulary();
                v.setVocabID(rs.getInt("VocabID"));           // ID từ vựng
                v.setLessonID(rs.getInt("LessonID"));         // ID lesson
                v.setWord(rs.getString("Word"));              // Từ tiếng Nhật
                v.setMeaning(rs.getString("Meaning"));        // Nghĩa tiếng Việt
                v.setReading(rs.getString("Reading"));        // Cách đọc (hiragana/katakana)
                v.setExample(rs.getString("Example"));        // Ví dụ sử dụng
                v.setImagePath(rs.getString("imagePath"));    // Đường dẫn ảnh
                list.add(v);
            }
        }
        return list;
    }

    // ===== SEARCH VOCABULARY =====
    /**
     * Tìm kiếm từ vựng theo từ khóa
     * Tìm kiếm trong: Word (từ tiếng Nhật), Meaning (nghĩa), Reading (cách đọc)
     * @param keyword Từ khóa tìm kiếm
     * @return ArrayList các từ vựng phù hợp
     * @throws SQLException nếu có lỗi database
     */
    public static ArrayList<Vocabulary> searchVocabulary(String keyword) throws SQLException {
        ArrayList<Vocabulary> list = new ArrayList<>();
        
        // SQL query để tìm kiếm từ vựng theo nhiều trường
        String sql = "SELECT * FROM Vocabulary WHERE Word LIKE ? OR Meaning LIKE ? OR Reading LIKE ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Tạo pattern tìm kiếm với wildcard
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);  // Tìm trong Word
            stmt.setString(2, searchPattern);  // Tìm trong Meaning
            stmt.setString(3, searchPattern);  // Tìm trong Reading
            
            // Thực thi query và xử lý kết quả
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Tạo Vocabulary object từ ResultSet
                Vocabulary v = new Vocabulary();
                v.setVocabID(rs.getInt("VocabID"));           // ID từ vựng
                v.setLessonID(rs.getInt("LessonID"));         // ID lesson
                v.setWord(rs.getString("Word"));              // Từ tiếng Nhật
                v.setMeaning(rs.getString("Meaning"));        // Nghĩa tiếng Việt
                v.setReading(rs.getString("Reading"));        // Cách đọc
                v.setExample(rs.getString("Example"));        // Ví dụ sử dụng
                v.setImagePath(rs.getString("imagePath"));    // Đường dẫn ảnh
                list.add(v);
            }
        }
        return list;
    }

    // ===== ADD VOCABULARY =====
    /**
     * Thêm từ vựng mới vào database
     * @param v Vocabulary object cần thêm
     * @throws SQLException nếu có lỗi database
     */
    public void add(Vocabulary v) throws SQLException {
        // SQL query để insert từ vựng mới
        String sql = "INSERT INTO Vocabulary (Word, Meaning, Reading, Example, LessonID, imagePath) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setString(1, v.getWord());        // Từ tiếng Nhật
            stmt.setString(2, v.getMeaning());     // Nghĩa tiếng Việt
            stmt.setString(3, v.getReading());     // Cách đọc
            stmt.setString(4, v.getExample());     // Ví dụ sử dụng
            stmt.setInt(5, v.getLessonID());       // ID lesson
            stmt.setString(6, v.getImagePath());   // Đường dẫn ảnh
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== UPDATE VOCABULARY =====
    /**
     * Cập nhật thông tin từ vựng
     * @param v Vocabulary object cần cập nhật
     * @throws SQLException nếu có lỗi database
     */
    public void update(Vocabulary v) throws SQLException {
        // SQL query để update từ vựng
        String sql = "UPDATE Vocabulary SET Word=?, Meaning=?, Reading=?, Example=?, LessonID=?, imagePath=? WHERE VocabID=?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setString(1, v.getWord());        // Từ tiếng Nhật
            stmt.setString(2, v.getMeaning());     // Nghĩa tiếng Việt
            stmt.setString(3, v.getReading());     // Cách đọc
            stmt.setString(4, v.getExample());     // Ví dụ sử dụng
            stmt.setInt(5, v.getLessonID());       // ID lesson
            stmt.setString(6, v.getImagePath());   // Đường dẫn ảnh
            stmt.setInt(7, v.getVocabID());        // ID từ vựng
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== DELETE VOCABULARY BY ID =====
    /**
     * Xóa từ vựng theo ID
     * @param vocabID ID của từ vựng cần xóa
     * @throws SQLException nếu có lỗi database
     */
    public void delete(int vocabID) throws SQLException {
        // SQL query để xóa từ vựng theo ID
        String sql = "DELETE FROM Vocabulary WHERE VocabID=?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vocabID);  // Set VocabID parameter
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== DELETE VOCABULARY BY LESSON ID =====
    /**
     * Xóa tất cả từ vựng của một lesson
     * @param lessonId ID của lesson cần xóa từ vựng
     * @throws SQLException nếu có lỗi database
     */
    public void deleteByLessonId(int lessonId) throws SQLException {
        // SQL query để xóa từ vựng theo LessonID
        String sql = "DELETE FROM Vocabulary WHERE LessonID = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, lessonId);  // Set LessonID parameter
            
            // Thực thi query
            ps.executeUpdate();
        }
    }
    
    // ===== MAIN METHOD FOR TESTING =====
    /**
     * Method chính để test chức năng tìm kiếm
     * @param args Command line arguments
     * @throws SQLException nếu có lỗi database
     */
    public static void main(String[] args) throws SQLException {
        VocabularyDAO a = new VocabularyDAO();
        test.Testcase.printlist(a.searchVocabulary("c"));
    }
}