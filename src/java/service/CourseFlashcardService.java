package service;

import Dao.CoursesDAO;
import Dao.FlashcardDAO;
import Dao.FlashcardItemDAO;
import Dao.LessonsDAO;
import Dao.VocabularyDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Course;
import model.Flashcard;
import model.FlashcardItem;
import model.Lesson;
import model.Vocabulary;

/**
 * Service để tự động tạo flashcard từ từ vựng của khóa học
 */
public class CourseFlashcardService {
    
    private final FlashcardDAO flashcardDAO;
    private final FlashcardItemDAO flashcardItemDAO;
    private final VocabularyDAO vocabularyDAO;
    private final LessonsDAO lessonsDAO;
    private final CoursesDAO coursesDAO;
    
    public CourseFlashcardService() {
        this.flashcardDAO = new FlashcardDAO();
        this.flashcardItemDAO = new FlashcardItemDAO();
        this.vocabularyDAO = new VocabularyDAO();
        this.lessonsDAO = new LessonsDAO();
        this.coursesDAO = new CoursesDAO();
    }
    
    /**
     * Tạo flashcard từ từ vựng của một khóa học
     * @param courseID ID của khóa học
     * @param creatorID ID của người tạo flashcard (giáo viên)
     * @return ID của flashcard đã tạo, hoặc -1 nếu có lỗi
     */
    public int createFlashcardFromCourse(int courseID, int creatorID) throws SQLException {
        // Lấy thông tin khóa học
        Course course = coursesDAO.getCourseByID(courseID);
        if (course == null) {
            return -1;
        }
        
        // Tạo flashcard mới
        Flashcard flashcard = new Flashcard();
        flashcard.setUserID(creatorID);
        flashcard.setTitle("Từ vựng khóa học: " + course.getTitle());
        flashcard.setDescription("Flashcard tự động tạo từ từ vựng của khóa học " + course.getTitle());
        flashcard.setPublicFlag(true); // Công khai để học viên có thể truy cập
        flashcard.setCoverImage(course.getImageUrl()); // Sử dụng ảnh bìa của khóa học
        
        // Lưu flashcard và lấy ID
        int flashcardID = flashcardDAO.createFlashcard(flashcard);
        if (flashcardID <= 0) {
            return -1;
        }
        
        // Lấy danh sách bài học trong khóa học
        List<Lesson> lessons = lessonsDAO.getLessonsByCourseID(courseID);
        int orderIndex = 0;
        
        // Duyệt qua từng bài học để lấy từ vựng
        for (Lesson lesson : lessons) {
            try {
                // Lấy từ vựng của bài học
                ArrayList<Vocabulary> vocabularies = vocabularyDAO.getVocabularyByLessonId(lesson.getLessonID());
                
                // Tạo flashcard item cho mỗi từ vựng
                for (Vocabulary vocab : vocabularies) {
                    FlashcardItem item = new FlashcardItem();
                    item.setFlashcardID(flashcardID);
                    item.setVocabID(vocab.getVocabID());
                    item.setUserVocabID(null);
                    item.setNote("Từ bài học: " + lesson.getTitle());
                    
                    // Mặt trước: Từ tiếng Nhật - đảm bảo không có ký tự đặc biệt gây lỗi
                    String frontContent = vocab.getWord();
                    if (frontContent == null) frontContent = "";
                    // Loại bỏ các ký tự có thể gây lỗi JavaScript
                    frontContent = frontContent.replace("\\", "\\\\")
                                             .replace("\"", "\\\"")
                                             .replace("'", "\\'")
                                             .replace("\n", " ")
                                             .replace("\r", "");
                    item.setFrontContent(frontContent);
                    
                    // Mặt sau: Chỉ hiển thị nghĩa - định dạng an toàn
                    String meaning = vocab.getMeaning();
                    if (meaning == null) meaning = "";
                    meaning = meaning.replace("\\", "\\\\")
                                   .replace("\"", "\\\"")
                                   .replace("'", "\\'");
                    
                    // Đặt nghĩa vào mặt sau
                    item.setBackContent(meaning);
                    
                    // Đưa cách đọc và ví dụ vào phần ghi chú
                    StringBuilder noteContent = new StringBuilder();
                    noteContent.append("Từ bài học: ").append(lesson.getTitle()).append("\n\n");
                    
                    String reading = vocab.getReading();
                    if (reading != null && !reading.trim().isEmpty()) {
                        reading = reading.replace("\\", "\\\\")
                                       .replace("\"", "\\\"")
                                       .replace("'", "\\'");
                        noteContent.append("Cách đọc: ").append(reading).append("\n\n");
                    }
                    
                    String example = vocab.getExample();
                    if (example != null && !example.trim().isEmpty()) {
                        example = example.replace("\\", "\\\\")
                                       .replace("\"", "\\\"")
                                       .replace("'", "\\'");
                        noteContent.append("Ví dụ: ").append(example);
                    }
                    
                    item.setNote(noteContent.toString());
                    
                    // Nếu từ vựng có ảnh, đưa vào mặt sau của flashcard
                    item.setFrontImage(null);
                    // Kiểm tra đường dẫn ảnh hợp lệ
                    String imagePath = vocab.getImagePath();
                    if (imagePath != null && !imagePath.trim().isEmpty() && 
                        (imagePath.startsWith("http") || imagePath.startsWith("/"))) {
                        item.setBackImage(imagePath);
                    } else {
                        item.setBackImage(null);
                    }
                    
                    item.setOrderIndex(orderIndex++);
                    
                    // Lưu flashcard item
                    flashcardItemDAO.createFlashcardItem(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Tiếp tục với bài học tiếp theo nếu có lỗi
            }
        }
        
        return flashcardID;
    }
    
    /**
     * Kiểm tra xem khóa học đã có flashcard chưa
     * @param courseID ID của khóa học
     * @return true nếu đã có flashcard, false nếu chưa có
     */
    public boolean courseHasFlashcard(int courseID) {
        try {
            // Truy vấn để kiểm tra xem khóa học đã có flashcard chưa
            String sql = "SELECT COUNT(*) FROM Flashcards f " +
                         "INNER JOIN FlashcardItems fi ON f.FlashcardID = fi.FlashcardID " +
                         "INNER JOIN Vocabulary v ON fi.VocabID = v.VocabID " +
                         "INNER JOIN Lessons l ON v.LessonID = l.LessonID " +
                         "WHERE l.CourseID = ?";
            
            try (java.sql.Connection conn = DB.JDBCConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, courseID);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy ID của flashcard của khóa học
     * @param courseID ID của khóa học
     * @return ID của flashcard, hoặc -1 nếu không tìm thấy
     */
    public int getCourseFlashcardID(int courseID) {
        try {
            String sql = "SELECT DISTINCT f.FlashcardID FROM Flashcards f " +
                         "INNER JOIN FlashcardItems fi ON f.FlashcardID = fi.FlashcardID " +
                         "INNER JOIN Vocabulary v ON fi.VocabID = v.VocabID " +
                         "INNER JOIN Lessons l ON v.LessonID = l.LessonID " +
                         "WHERE l.CourseID = ?";
            
            try (java.sql.Connection conn = DB.JDBCConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, courseID);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("FlashcardID");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
} 