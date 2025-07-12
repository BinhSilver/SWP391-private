package Dao;

import java.sql.*;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;
import model.Course;

public class CoursesDAO {

    public static ArrayList<Course> searchCourse(String keyword) {
        ArrayList<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Courses] WHERE title LIKE ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Course c = new Course(
                        rs.getInt("courseID"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBoolean("isHidden"),
                        rs.getBoolean("isSuggested"),
                        rs.getString("imageUrl")
                );
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void add(Course c) throws SQLException {
        String sql = "INSERT INTO [dbo].[Courses] (Title, Description, IsHidden, IsSuggested, imageUrl) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.getHidden());
            stmt.setBoolean(4, c.isSuggested());
            stmt.setString(5, c.getImageUrl());
            stmt.executeUpdate();
        }
    }

    public void update(Course c) throws SQLException {
        String sql = "UPDATE [dbo].[Courses] SET Title=?, Description=?, IsHidden=?, IsSuggested=?, imageUrl=? WHERE CourseID=?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.getHidden());
            stmt.setBoolean(4, c.isSuggested());
            stmt.setString(5, c.getImageUrl());
            stmt.setInt(6, c.getCourseID());
            stmt.executeUpdate();
        }
    }

    public void delete(int courseID) throws SQLException {
        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // Lấy toàn bộ LessonID của khóa học này
                List<Integer> lessonIds = new ArrayList<>();
                String lessonSql = "SELECT LessonID FROM Lessons WHERE CourseID = ?";
                try (PreparedStatement ps = conn.prepareStatement(lessonSql)) {
                    ps.setInt(1, courseID);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            lessonIds.add(rs.getInt("LessonID"));
                        }
                    }
                }

                // Xóa dữ liệu phụ thuộc LessonID
                for (int lessonId : lessonIds) {
                    // Xóa LessonAccess
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LessonAccess WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa LessonMaterials
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LessonMaterials WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa GrammarPoints
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM GrammarPoints WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa Kanji
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Kanji WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa LessonVocabulary
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM LessonVocabulary WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa Feedbacks
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Feedbacks WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa Progress liên quan LessonID
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Progress WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                    // Xóa Quiz, Question, Answer, QuizResults
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
                        // Xóa Answers
                        try (PreparedStatement ps = conn.prepareStatement(
                                "DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE QuizID = ?)")) {
                            ps.setInt(1, quizId);
                            ps.executeUpdate();
                        }
                        // Xóa Questions
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Questions WHERE QuizID = ?")) {
                            ps.setInt(1, quizId);
                            ps.executeUpdate();
                        }
                        // Xóa QuizResults
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM QuizResults WHERE QuizID = ?")) {
                            ps.setInt(1, quizId);
                            ps.executeUpdate();
                        }
                    }
                    // Xóa Quizzes
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Quizzes WHERE LessonID = ?")) {
                        ps.setInt(1, lessonId);
                        ps.executeUpdate();
                    }
                }

                // Xóa Lessons
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Lessons WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    ps.executeUpdate();
                }

                // Xóa các bảng liên kết CourseID
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Enrollment WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM CourseRatings WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Progress WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    ps.executeUpdate();
                }

                // Xóa Tests liên quan CourseID
                List<Integer> testIds = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT TestID FROM Tests WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            testIds.add(rs.getInt("TestID"));
                        }
                    }
                }
                for (int testId : testIds) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE TestID = ?)")) {
                        ps.setInt(1, testId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Questions WHERE TestID = ?")) {
                        ps.setInt(1, testId);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TestResults WHERE TestID = ?")) {
                        ps.setInt(1, testId);
                        ps.executeUpdate();
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Tests WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    ps.executeUpdate();
                }

                // Cuối cùng xóa khóa học
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Courses WHERE CourseID = ?")) {
                    ps.setInt(1, courseID);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
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
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Courses] WHERE MONTH(CreatedAt) = ? AND YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
            }
        }
        return 0;
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Courses]";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("CourseID"),
                        rs.getString("Title"),
                        rs.getString("Description"),
                        rs.getBoolean("IsHidden"),
                        rs.getBoolean("IsSuggested"),
                        rs.getString("imageUrl")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    public List<Course> getSuggestedCourses() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT TOP 4 * FROM Courses WHERE isHidden = 0 AND IsSuggested = 1 ORDER BY NEWID()";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course(
                        rs.getInt("courseID"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getBoolean("isHidden"),
                        rs.getBoolean("isSuggested"),
                        rs.getString("imageUrl")
                );
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Course getCourseByID(int courseID) {
        String sql = "SELECT * FROM Courses WHERE courseID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("courseID"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getBoolean("isHidden"),
                            rs.getBoolean("isSuggested"),
                            rs.getString("imageUrl")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addAndReturnID(Course course) throws SQLException {
        String sql = "INSERT INTO Courses (Title, Description, IsHidden, IsSuggested, imageUrl, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, GETDATE())";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setBoolean(3, course.getHidden());
            ps.setBoolean(4, course.isSuggested());
            ps.setString(5, course.getImageUrl());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
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
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Enrollment] WHERE MONTH(EnrolledAt) = ? AND YEAR(EnrolledAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, month);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
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
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Count");
            }
        }
        return 0;
    }

}
