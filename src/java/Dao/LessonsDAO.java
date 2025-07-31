package Dao;

import java.sql.*;
import model.Lesson;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;

// ===== LESSONS DATA ACCESS OBJECT =====
/**
 * LessonsDAO - Data Access Object cho bảng Lessons
 * Quản lý tất cả các thao tác CRUD với bài học
 * 
 * Chức năng chính:
 * - Thêm, sửa, xóa bài học
 * - Lấy danh sách bài học theo khóa học
 * - Quản lý thứ tự bài học
 * - Kiểm soát trạng thái ẩn/hiện bài học
 * - Xóa bài học và các dependencies
 * 
 * Sử dụng JDBC để kết nối và thao tác với SQL Server
 * 
 * Các nhóm phương thức chính:
 * - add, addAndReturnID: Thêm bài học mới
 * - update: Cập nhật thông tin bài học
 * - delete, deleteLessonAndDependencies: Xóa bài học
 * - getLessonsByCourseID: Lấy danh sách bài học theo khóa học
 * - getLessonById: Lấy bài học theo ID
 */
public class LessonsDAO {

    // Thêm bài học
    public void add(Lesson l) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, Description, IsHidden, OrderIndex) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setString(3, l.getDescription());
            stmt.setBoolean(4, l.isIsHidden());
            stmt.setInt(5, l.getOrderIndex());
            stmt.executeUpdate();
        }
    }

    // Cập nhật bài học
    public void update(Lesson l) throws SQLException {
        String sql = "UPDATE Lessons SET CourseID=?, Title=?, Description=?, IsHidden=?, OrderIndex=? WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, l.getCourseID());
            stmt.setString(2, l.getTitle());
            stmt.setString(3, l.getDescription());
            stmt.setBoolean(4, l.isIsHidden());
            stmt.setInt(5, l.getOrderIndex());
            stmt.setInt(6, l.getLessonID());
            stmt.executeUpdate();
        }
    }

    // Xóa bài học
    public void delete(int lessonID) throws SQLException {
        String sql = "DELETE FROM Lessons WHERE LessonID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonID);
            stmt.executeUpdate();
        }
    }

    // Lấy danh sách bài học theo CourseID
    public List<Lesson> getLessonsByCourseID(int courseID) {
        List<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM Lessons WHERE CourseID = ? ORDER BY OrderIndex ASC";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Lesson(
                        rs.getInt("LessonID"),
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getBoolean("IsHidden"),
                        rs.getString("Description"),
                        rs.getInt("OrderIndex")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy 1 bài học theo ID
    public static Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM Lessons WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Lesson(
                        rs.getInt("LessonID"),
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getBoolean("IsHidden"),
                        rs.getString("Description"),
                        rs.getInt("OrderIndex")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm bài học và trả về ID vừa tạo
    public int addAndReturnID(Lesson lesson) throws SQLException {
        String sql = "INSERT INTO Lessons (CourseID, Title, Description, IsHidden, OrderIndex) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, lesson.getCourseID());
            stmt.setString(2, lesson.getTitle());
            stmt.setString(3, lesson.getDescription());
            stmt.setBoolean(4, lesson.isIsHidden());
            stmt.setInt(5, lesson.getOrderIndex());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Insert Lesson failed, no ID obtained.");
    }

    // Xóa lesson và toàn bộ dữ liệu liên quan
    public void deleteLessonAndDependencies(int lessonId) throws SQLException {
        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Xóa các bảng phụ liên quan đến lessonId
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
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM GrammarPoints WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
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
                // Xóa quiz và các phụ thuộc
                List<Integer> quizIds = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT QuizID FROM Quizzes WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            quizIds.add(rs.getInt("QuizID"));
                        }
                    }
                }
                for (int quizId : quizIds) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE QuizID = ?)")) {
                        ps.setInt(1, quizId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Questions WHERE QuizID = ?")) {
                        ps.setInt(1, quizId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM QuizResults WHERE QuizID = ?")) {
                        ps.setInt(1, quizId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Quizzes WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Feedbacks WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                // Xóa lesson cuối cùng
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Lessons WHERE LessonID = ?")) {
                    ps.setInt(1, lessonId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        }
    }
}
