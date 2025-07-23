package Dao;

import java.sql.*;
import DB.JDBCConnection;
import java.util.ArrayList;
import java.util.List;
import model.Course;

public class CoursesDAO {

    public static ArrayList<Course> searchCourse(String keyword) {
        ArrayList<Course> list = new ArrayList<>();
        String sql = "SELECT CourseID, Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy "
                + "FROM [dbo].[Courses] WHERE Title LIKE ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void add(Course c) throws SQLException {
        String sql = "INSERT INTO [dbo].[Courses] "
                + "(Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.getHidden());
            stmt.setBoolean(4, c.isSuggested());
            stmt.setString(5, c.getImageUrl());
            stmt.setInt(6, c.getCreatedBy());
            stmt.executeUpdate();
        }
    }

    public int addAndReturnID(Course c) throws SQLException {
        String sql = "INSERT INTO [dbo].[Courses] "
                + "(Title, Description, IsHidden, IsSuggested, imageUrl, CreatedBy, CreatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, c.getTitle());
            stmt.setString(2, c.getDescription());
            stmt.setBoolean(3, c.getHidden());
            stmt.setBoolean(4, c.isSuggested());
            stmt.setString(5, c.getImageUrl());
            stmt.setInt(6, c.getCreatedBy());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (id > 0) {
                        return id;
                    }
                }
            }
            // Nếu không lấy được ID tự sinh, truy vấn lại DB lấy MAX(CourseID)
            System.err.println("[WARN] addAndReturnID: Không lấy được ID tự sinh, sẽ lấy MAX(CourseID) từ DB!");
            try (PreparedStatement maxStmt = conn.prepareStatement("SELECT MAX(CourseID) FROM Courses"); ResultSet maxRs = maxStmt.executeQuery()) {
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

    public void update(Course c) throws SQLException {
        String sql = "UPDATE [dbo].[Courses] "
                + "SET Title=?, Description=?, IsHidden=?, IsSuggested=?, imageUrl=? "
                + "WHERE CourseID=?";
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
            // Xóa flashcard tự động tạo từ khóa học
            new Dao.FlashcardDAO().deleteFlashcardsByCourseId(courseID);
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
                // KHÔNG xóa Feedbacks bằng LessonID
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Lessons WHERE CourseID = ?")) {
                ps.setInt(1, courseID);
                ps.executeUpdate();
            }

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
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE TestID = ?)")) {
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

            // Xóa các bảng liên quan đến CourseID
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Feedbacks WHERE CourseID = ?")) {
                ps.setInt(1, courseID);
                ps.executeUpdate();
            }
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
        ps.setInt(1, parameter);
        int deletedRows = ps.executeUpdate();
        System.out.println("[DEBUG] Executed: " + query + " with parameter: " + parameter + " - Deleted rows: " + deletedRows);
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
