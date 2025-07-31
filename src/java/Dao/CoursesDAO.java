package Dao;

// ===== IMPORT STATEMENTS =====
import java.sql.*;                          // SQL database operations
import DB.JDBCConnection;                   // Database connection utility
import java.util.ArrayList;                 // ArrayList collection
import java.util.List;                      // List collection
import model.Course;                        // Course model

// ===== COURSES DATA ACCESS OBJECT =====
/**
 * CoursesDAO - Data Access Object cho Courses
 * Quản lý tất cả các thao tác CRUD với bảng Courses trong database
 * Bao gồm: thêm, sửa, xóa, tìm kiếm, lấy danh sách khóa học
 */
public class CoursesDAO {

    // ===== SEARCH COURSES =====
    /**
     * Tìm kiếm khóa học theo từ khóa
     * @param keyword Từ khóa tìm kiếm
     * @return ArrayList các khóa học phù hợp
     */
    public static ArrayList<Course> searchCourse(String keyword) {
        ArrayList<Course> list = new ArrayList<>();
        
        // SQL query để tìm kiếm khóa học theo title
        String sql = "SELECT CourseID, Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy "
                + "FROM [dbo].[Courses] WHERE Title LIKE ?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set tham số tìm kiếm với wildcard
            stmt.setString(1, "%" + keyword + "%");
            
            // Thực thi query và xử lý kết quả
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Tạo Course object từ ResultSet
                    Course c = new Course(
                            rs.getInt("CourseID"),           // ID khóa học
                            rs.getString("Title"),           // Tiêu đề
                            rs.getString("Description"),     // Mô tả
                            rs.getBoolean("IsHidden"),       // Có ẩn không
                            rs.getBoolean("IsSuggested"),    // Có đề xuất không
                            rs.getInt("CreatedBy")           // ID người tạo
                    );
                    c.setImageUrl(rs.getString("imageUrl")); // URL ảnh
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== ADD COURSE =====
    /**
     * Thêm khóa học mới vào database
     * @param c Course object cần thêm
     * @throws SQLException nếu có lỗi database
     */
    public void add(Course c) throws SQLException {
        // SQL query để insert khóa học mới
        String sql = "INSERT INTO [dbo].[Courses] "
                + "(Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setString(1, c.getTitle());        // Tiêu đề
            stmt.setString(2, c.getDescription());  // Mô tả
            stmt.setBoolean(3, c.getHidden());      // Có ẩn không
            stmt.setBoolean(4, c.isSuggested());    // Có đề xuất không
            stmt.setString(5, c.getImageUrl());     // URL ảnh
            stmt.setInt(6, c.getCreatedBy());       // ID người tạo
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== ADD COURSE AND RETURN ID =====
    /**
     * Thêm khóa học mới và trả về ID được generate
     * @param c Course object cần thêm
     * @return ID của khóa học vừa tạo
     * @throws SQLException nếu có lỗi database
     */
    public int addAndReturnID(Course c) throws SQLException {
        // SQL query để insert khóa học mới với RETURN_GENERATED_KEYS
        String sql = "INSERT INTO [dbo].[Courses] "
                + "(Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set các tham số cho prepared statement
            stmt.setString(1, c.getTitle());        // Tiêu đề
            stmt.setString(2, c.getDescription());  // Mô tả
            stmt.setBoolean(3, c.getHidden());      // Có ẩn không
            stmt.setBoolean(4, c.isSuggested());    // Có đề xuất không
            stmt.setString(5, c.getImageUrl());     // URL ảnh
            stmt.setInt(6, c.getCreatedBy());       // ID người tạo
            
            // Thực thi query và lấy số rows bị ảnh hưởng
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            
            // ===== GET GENERATED ID =====
            // Lấy ID được generate tự động
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (id > 0) {
                        return id;
                    }
                }
            }
            
            // ===== FALLBACK METHOD =====
            // Nếu không lấy được ID tự sinh, truy vấn lại DB lấy MAX(CourseID)
            System.err.println("[WARN] addAndReturnID: Không lấy được ID tự sinh, sẽ lấy MAX(CourseID) từ DB!");
            try (PreparedStatement maxStmt = conn.prepareStatement("SELECT MAX(CourseID) FROM Courses"); 
                 ResultSet maxRs = maxStmt.executeQuery()) {
                if (maxRs.next()) {
                    int maxId = maxRs.getInt(1);
                    if (maxId > 0) {
                        return maxId;
                    }
                }
            }
            throw new SQLException("Creating course failed, no ID obtained (even after fallback MAX).");
        }
    }

    // ===== UPDATE COURSE =====
    /**
     * Cập nhật thông tin khóa học
     * @param c Course object cần cập nhật
     * @throws SQLException nếu có lỗi database
     */
    public void update(Course c) throws SQLException {
        // SQL query để update khóa học
        String sql = "UPDATE [dbo].[Courses] "
                + "SET Title=?, Description=?, IsHidden=?, IsSuggested=?, imageUrl=? "
                + "WHERE CourseID=?";
        
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setString(1, c.getTitle());        // Tiêu đề
            stmt.setString(2, c.getDescription());  // Mô tả
            stmt.setBoolean(3, c.getHidden());      // Có ẩn không
            stmt.setBoolean(4, c.isSuggested());    // Có đề xuất không
            stmt.setString(5, c.getImageUrl());     // URL ảnh
            stmt.setInt(6, c.getCourseID());        // ID khóa học
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

     public void delete(int courseID) throws SQLException {
            try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Xóa FlashcardItems và Flashcards trước (vì có foreign key constraints)
                new Dao.FlashcardDAO().deleteFlashcardsByCourseId(courseID);
                System.out.println("[LOG] Đã xóa Flashcards và FlashcardItems cho course " + courseID);
            List<Integer> lessonIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT LessonID FROM Lessons WHERE CourseID = ?")) {
                ps.setInt(1, courseID);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lessonIds.add(rs.getInt("LessonID"));
                    }
                }
            }

            for (int lessonId : lessonIds) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Vocabulary WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LessonAccess WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LessonMaterials WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                // Bảng GrammarPoints không tồn tại trong database hiện tại
                // try (PreparedStatement ps = conn.prepareStatement("DELETE FROM GrammarPoints WHERE LessonID = ?")) {
                //     ps.setInt(1, lessonId);
                //     ps.executeUpdate();
                // }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Kanji WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LessonVocabulary WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Progress WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }

                List<Integer> quizIds = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT QuizID FROM Quizzes WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            quizIds.add(rs.getInt("QuizID"));
                        }
                    }
                }
                System.out.println("[LOG] Tìm thấy " + quizIds.size() + " bài học");

                // 2. FlashcardItems và Flashcards đã được xóa ở bước 1, không cần xóa lại
                // System.out.println("[LOG] FlashcardItems và Flashcards đã được xóa ở bước 1");

                // 4. Xóa dữ liệu liên quan đến từng lesson
                for (int innerLessonId : lessonIds) {
                    System.out.println("[LOG] Đang xóa lesson " + innerLessonId);
                    
                    // Xóa quiz và câu hỏi liên quan trước
                    quizIds.clear();
                    try (PreparedStatement psQuiz = conn.prepareStatement("SELECT QuizID FROM Quizzes WHERE LessonID = ?")) {
                        psQuiz.setInt(1, innerLessonId);
                        try (ResultSet rs = psQuiz.executeQuery()) {
                            while (rs.next()) {
                                quizIds.add(rs.getInt("QuizID"));
                            }
                        }
                    }
                    
                    for (int quizId : quizIds) {
                        try {
                            deleteByQuery(conn, "DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE QuizID = ?)", quizId);
                            deleteByQuery(conn, "DELETE FROM Questions WHERE QuizID = ?", quizId);
                            deleteByQuery(conn, "DELETE FROM QuizResults WHERE QuizID = ?", quizId);
                        } catch (Exception e) {
                            System.out.println("[WARNING] Lỗi khi xóa quiz " + quizId + ": " + e.getMessage());
                        }
                    }
                    
                    // Xóa các bảng liên quan đến lesson
                    try {
                        deleteByQuery(conn, "DELETE FROM Quizzes WHERE LessonID = ?", innerLessonId);
                        deleteByQuery(conn, "DELETE FROM LessonAccess WHERE LessonID = ?", innerLessonId);
                        deleteByQuery(conn, "DELETE FROM LessonMaterials WHERE LessonID = ?", innerLessonId);
                        // Bảng GrammarPoints không tồn tại trong database hiện tại
                        // deleteByQuery(conn, "DELETE FROM GrammarPoints WHERE LessonID = ?", innerLessonId);
                        deleteByQuery(conn, "DELETE FROM Kanji WHERE LessonID = ?", innerLessonId);
                        deleteByQuery(conn, "DELETE FROM LessonVocabulary WHERE LessonID = ?", innerLessonId);
                        deleteByQuery(conn, "DELETE FROM Vocabulary WHERE LessonID = ?", innerLessonId);
                        deleteByQuery(conn, "DELETE FROM Progress WHERE LessonID = ?", innerLessonId);
                    } catch (Exception e) {
                        System.out.println("[WARNING] Lỗi khi xóa dữ liệu lesson " + innerLessonId + ": " + e.getMessage());
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Quizzes WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                // KHÔNG xóa Feedbacks bằng LessonID
            }

                // 5. Xóa test và câu hỏi liên quan (bảng Tests không tồn tại trong database hiện tại)
                // List<Integer> testIds = new ArrayList<>();
                // try (PreparedStatement psTest = conn.prepareStatement("SELECT TestID FROM Tests WHERE CourseID = ?")) {
                //     psTest.setInt(1, courseID);
                //     try (ResultSet rs = psTest.executeQuery()) {
                //         while (rs.next()) {
                //             testIds.add(rs.getInt("TestID"));
                //         }
                //     }
                // }
                // 
                // for (int testId : testIds) {
                //     try {
                //         deleteByQuery(conn, "DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE TestID = ?)", testId);
                //         deleteByQuery(conn, "DELETE FROM Questions WHERE TestID = ?", testId);
                //         deleteByQuery(conn, "DELETE FROM TestResults WHERE TestID = ?", testId);
                //     } catch (Exception e) {
                //         System.out.println("[WARNING] Lỗi khi xóa test " + testId + ": " + e.getMessage());
                //     }
                // }
                // 
                // try {
                //     deleteByQuery(conn, "DELETE FROM Tests WHERE CourseID = ?", courseID);
                // } catch (Exception e) {
                //     System.out.println("[WARNING] Lỗi khi xóa Tests: " + e.getMessage());
                // }

                // 6. Xóa dữ liệu liên quan đến course (theo thứ tự để tránh foreign key constraint)
                try {
                    // Xóa FeedbackVotes trước (vì có foreign key đến Feedbacks)
                    deleteByQuery(conn, "DELETE FROM FeedbackVotes WHERE FeedbackID IN (SELECT FeedbackID FROM Feedbacks WHERE CourseID = ?)", courseID);
                    System.out.println("[LOG] Đã xóa FeedbackVotes");
                    
                    // Xóa Feedbacks
                    deleteByQuery(conn, "DELETE FROM Feedbacks WHERE CourseID = ?", courseID);
                    System.out.println("[LOG] Đã xóa Feedbacks");
                    
                    // Xóa CourseRatings
                    deleteByQuery(conn, "DELETE FROM CourseRatings WHERE CourseID = ?", courseID);
                    System.out.println("[LOG] Đã xóa CourseRatings");
                    
                    // Xóa Enrollment
                    deleteByQuery(conn, "DELETE FROM Enrollment WHERE CourseID = ?", courseID);
                    System.out.println("[LOG] Đã xóa Enrollment");
                    
                    // Xóa Progress
                    deleteByQuery(conn, "DELETE FROM Progress WHERE CourseID = ?", courseID);
                    System.out.println("[LOG] Đã xóa Progress");
                    
                    // Cuối cùng xóa Lessons (sau khi đã xóa tất cả dữ liệu liên quan)
                    deleteByQuery(conn, "DELETE FROM Lessons WHERE CourseID = ?", courseID);
                    System.out.println("[LOG] Đã xóa Lessons");
                    
                } catch (Exception e) {
                    System.out.println("[WARNING] Lỗi khi xóa dữ liệu course: " + e.getMessage());
                }
                
                // 7. Cuối cùng xóa course
                try {
                    System.out.println("[LOG] Bắt đầu xóa course " + courseID);
                    deleteByQuery(conn, "DELETE FROM Courses WHERE CourseID = ?", courseID);
                    System.out.println("[LOG] Đã xóa course " + courseID);
                    
                    // Commit transaction nếu tất cả thành công
                    conn.commit();
                    System.out.println("[LOG] Transaction committed successfully");
                    System.out.println("[LOG] Xóa course " + courseID + " thành công!");
                } catch (Exception e) {
                    // Rollback nếu có lỗi
                    conn.rollback();
                    System.out.println("[ERROR] Lỗi khi xóa course: " + e.getMessage());
                    System.out.println("[ERROR] Transaction rolled back");
                    throw e;
                }

            // Phần này đã được xử lý ở trên, không cần lặp lại
            // try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Feedbacks WHERE CourseID = ?")) {
            //     ps.setInt(1, courseID);
            //     ps.executeUpdate();
            // }
            // try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Enrollment WHERE CourseID = ?")) {
            //     ps.setInt(1, courseID);
            //     ps.executeUpdate();
            // }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CourseRatings WHERE CourseID = ?")) {
                ps.setInt(1, courseID);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Progress WHERE CourseID = ?")) {
                ps.setInt(1, courseID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Courses WHERE CourseID = ?")) {
                ps.setInt(1, courseID);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            ex.printStackTrace();
            throw new SQLException("Xóa khóa học thất bại: " + ex.getMessage(), ex);
        }
    }
}

/**
 * Helper method để thực hiện delete query với parameter
 */
private void deleteByQuery(Connection conn, String query, int parameter) throws SQLException {
    try (PreparedStatement ps = conn.prepareStatement(query)) {
        if (parameter > 0) {
            ps.setInt(1, parameter);
        }
        int deletedRows = ps.executeUpdate();
        System.out.println("[DEBUG] Executed: " + query + " with parameter: " + parameter + " - Deleted rows: " + deletedRows);
    }
}

    private void deleteByQuery(Connection conn, String query) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            int deletedRows = ps.executeUpdate();
            System.out.println("[DEBUG] Executed: " + query + " - Deleted rows: " + deletedRows);
        }
    }

    public int getTotalCourses() throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    public int getCoursesByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] "
                + "WHERE MONTH(CreatedAt) = ? AND YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count");
                }
            }
        }
        return 0;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT CourseID, Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course c = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getBoolean("IsHidden"),
                        rs.getBoolean("IsSuggested"),
                        rs.getInt("CreatedBy")
                );
                c.setImageUrl(rs.getString("imageUrl"));
                courses.add(c);
            }
        }
        return courses;
    }

    public List<Course> getSuggestedCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT TOP 4 CourseID, Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy "
                + "FROM Courses WHERE IsHidden = 0 AND IsSuggested = 1 ORDER BY NEWID()";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getBoolean("IsHidden"),
                        rs.getBoolean("IsSuggested"),
                        rs.getInt("CreatedBy")
                );
                c.setImageUrl(rs.getString("imageUrl"));
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Course> getCoursesByTeacher(int teacherID) throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT CourseID, Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy "
                + "FROM Courses WHERE CreatedBy = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course();
                    c.setCourseID(rs.getInt("CourseID"));
                    c.setTitle(rs.getString("Title"));
                    c.setDescription(rs.getString("Description"));
                    c.setHidden(rs.getBoolean("IsHidden"));
                    c.setSuggested(rs.getBoolean("IsSuggested"));
                    c.setImageUrl(rs.getString("imageUrl"));
                    c.setCreatedBy(rs.getInt("CreatedBy"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    public Course getCourseByID(int courseID) {
        String sql = "SELECT CourseID, Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy "
                + "FROM Courses WHERE CourseID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course(
                            rs.getInt("CourseID"),
                            rs.getString("Title"),
                            rs.getString("Description"),
                            rs.getBoolean("IsHidden"),
                            rs.getBoolean("IsSuggested"),
                            rs.getInt("CreatedBy")
                    );
                    c.setImageUrl(rs.getString("imageUrl"));
                    return c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalEnrollments() throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Enrollment]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    public int getEnrollmentsByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Enrollment] "
                + "WHERE MONTH(EnrolledAt) = ? AND YEAR(EnrolledAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count");
                }
            }
        }
        return 0;
    }

    public int getHiddenCoursesCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] WHERE IsHidden = 1";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Count");
            }
        }
        return 0;
    }

    public int getCoursesByYear(int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] WHERE YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count");
                }
            }
        }
        return 0;
    }
}
