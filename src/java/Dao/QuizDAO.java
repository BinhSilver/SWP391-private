package Dao;

import DB.JDBCConnection;
import model.Answer;
import model.QuizQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ===== QUIZ DATA ACCESS OBJECT =====
/**
 * QuizDAO - Data Access Object cho bảng Quizzes, Questions, Answers
 * Quản lý tất cả các thao tác CRUD với quiz, câu hỏi và đáp án
 * 
 * Chức năng chính:
 * - Tạo và quản lý quiz cho bài học
 * - Thêm, sửa, xóa câu hỏi và đáp án
 * - Lấy danh sách câu hỏi theo lesson
 * - Backup và restore quiz
 * - Thống kê kết quả quiz
 * 
 * Sử dụng JDBC để kết nối và thao tác với SQL Server
 * 
 * Các nhóm phương thức chính:
 * - saveQuestions: Lưu quiz + câu hỏi + đáp án
 * - getQuestionsByLessonId, getQuestionsWithAnswersByLessonId: Lấy câu hỏi
 * - updateQuestion, updateAnswer: Cập nhật câu hỏi/đáp án
 * - deleteQuestion, deleteQuestionsByLessonId: Xóa câu hỏi
 * - backupQuiz, restoreQuiz: Backup và restore
 * - getQuizStatsByLessonId: Thống kê quiz
 */
public class QuizDAO {

    // ✅ Lưu quiz + câu hỏi + đáp án mới (xóa trước nếu đã tồn tại)
    public static boolean saveQuestions(int lessonId, List<QuizQuestion> questions) {
        String insertQuizSql = "INSERT INTO Quizzes (LessonID, Title) OUTPUT INSERTED.QuizID VALUES (?, ?)";
        String insertQuestionSql = "INSERT INTO Questions (QuizID, QuestionText, TimeLimit) OUTPUT INSERTED.QuestionID VALUES (?, ?, ?)";
        String insertAnswerSql = "INSERT INTO Answers (QuestionID, AnswerText, IsCorrect, AnswerNumber) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            int quizId = getOrCreateQuizId(conn, lessonId);
            System.out.println("[saveQuestions] quizId = " + quizId);

            deleteQuestionsAndAnswers(conn, quizId);

            // Nếu không có câu hỏi nào, xóa luôn quiz khỏi DB
            if (questions == null || questions.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Quizzes WHERE QuizID = ?")) {
                    ps.setInt(1, quizId);
                    ps.executeUpdate();
                }
                conn.commit();
                System.out.println("[saveQuestions] Xóa quiz vì không có câu hỏi!");
                return true;
            }

            for (QuizQuestion question : questions) {
                PreparedStatement qStmt = conn.prepareStatement(insertQuestionSql);
                qStmt.setInt(1, quizId);
                qStmt.setString(2, question.getQuestion());
                qStmt.setInt(3, question.getTimeLimit());
                ResultSet qRs = qStmt.executeQuery();
                if (!qRs.next()) {
                    System.err.println("[saveQuestions] Insert question failed for: " + question.getQuestion());
                    throw new SQLException("Insert question failed");
                }
                int questionId = qRs.getInt(1);
                System.out.println("[saveQuestions] Inserted questionId = " + questionId);

                for (Answer answer : question.getAnswers()) {
                    PreparedStatement aStmt = conn.prepareStatement(insertAnswerSql);
                    aStmt.setInt(1, questionId);
                    aStmt.setString(2, answer.getAnswerText());
                    aStmt.setInt(3, answer.getIsCorrect());
                    aStmt.setInt(4, answer.getAnswerNumber());
                    aStmt.executeUpdate();
                    System.out.println("[saveQuestions] Inserted answer: " + answer.getAnswerText());
                }
            }

            conn.commit();
            System.out.println("[saveQuestions] Commit OK!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Lấy danh sách câu hỏi đơn giản (không có đáp án)
    public static List<QuizQuestion> getQuestionsByLessonId(int lessonId) {
        List<QuizQuestion> list = new ArrayList<>();
        String questionSql = "SELECT q.QuestionID, q.QuestionText, q.CorrectAnswer, q.TimeLimit FROM Questions q JOIN Quizzes z ON q.QuizID = z.QuizID WHERE z.LessonID = ?";
        String answerSql = "SELECT AnswerText, AnswerNumber FROM Answers WHERE QuestionID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement qStmt = conn.prepareStatement(questionSql)) {
            qStmt.setInt(1, lessonId);
            ResultSet qRs = qStmt.executeQuery();

            while (qRs.next()) {
                int qId = qRs.getInt("QuestionID");
                String qText = qRs.getString("QuestionText");
                int correct = qRs.getInt("CorrectAnswer");
                int timeLimit = qRs.getInt("TimeLimit");

                List<Answer> answers = new ArrayList<>();
                PreparedStatement aStmt = conn.prepareStatement(answerSql);
                aStmt.setInt(1, qId);
                ResultSet aRs = aStmt.executeQuery();
                while (aRs.next()) {
                    Answer ans = new Answer();
                    ans.setAnswerText(aRs.getString("AnswerText"));
                    ans.setAnswerNumber(aRs.getInt("AnswerNumber"));
                    answers.add(ans);
                }

                QuizQuestion q = new QuizQuestion(qId, 0, qText, correct, timeLimit, answers);
                list.add(q);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Lấy danh sách câu hỏi + đáp án đầy đủ
    public static List<QuizQuestion> getQuestionsWithAnswersByLessonId(int lessonId) {
        List<QuizQuestion> questions = new ArrayList<>();
        String questionSql = "SELECT q.QuestionID, q.QuestionText, q.TimeLimit FROM Questions q JOIN Quizzes z ON q.QuizID = z.QuizID WHERE z.LessonID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement qStmt = conn.prepareStatement(questionSql)) {
            qStmt.setInt(1, lessonId);
            ResultSet qRs = qStmt.executeQuery();

            while (qRs.next()) {
                int questionId = qRs.getInt("QuestionID");
                String questionText = qRs.getString("QuestionText");
                int timeLimit = qRs.getInt("TimeLimit");

                List<Answer> answers = getAnswersByQuestionId(questionId);

                int correctAnswerNumber = -1;
                for (Answer a : answers) {
                    if (a.getIsCorrect() == 1) {
                        correctAnswerNumber = a.getAnswerNumber();
                        break;
                    }
                }

                QuizQuestion question = new QuizQuestion(
                        questionId,
                        0,
                        questionText,
                        correctAnswerNumber,
                        timeLimit,
                        answers
                );

                questions.add(question);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    // ✅ Lấy danh sách đáp án theo câu hỏi
    public static List<Answer> getAnswersByQuestionId(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT AnswerID, QuestionID, AnswerText, AnswerNumber, IsCorrect FROM Answers WHERE QuestionID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Answer answer = new Answer();
                answer.setId(rs.getInt("AnswerID"));
                answer.setQuestionId(rs.getInt("QuestionID"));
                answer.setAnswerText(rs.getString("AnswerText"));
                answer.setAnswerNumber(rs.getInt("AnswerNumber"));
                answer.setIsCorrect(rs.getInt("IsCorrect"));
                answers.add(answer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return answers;
    }

    // ✅ Xóa toàn bộ câu hỏi/đáp án và quiz theo lessonId
    public static boolean deleteQuestionsByLessonId(int lessonId) {
        String getQuizIdSql = "SELECT QuizID FROM Quizzes WHERE LessonID = ?";
        String deleteAnswersSql = "DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE QuizID = ?)";
        String deleteQuestionsSql = "DELETE FROM Questions WHERE QuizID = ?";
        String deleteQuizSql = "DELETE FROM Quizzes WHERE QuizID = ?";

        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            PreparedStatement getQuizStmt = conn.prepareStatement(getQuizIdSql);
            getQuizStmt.setInt(1, lessonId);
            ResultSet rs = getQuizStmt.executeQuery();
            if (!rs.next()) {
                return false;
            }
            int quizId = rs.getInt("QuizID");

            PreparedStatement delAnswers = conn.prepareStatement(deleteAnswersSql);
            delAnswers.setInt(1, quizId);
            delAnswers.executeUpdate();

            PreparedStatement delQuestions = conn.prepareStatement(deleteQuestionsSql);
            delQuestions.setInt(1, quizId);
            delQuestions.executeUpdate();

            PreparedStatement delQuiz = conn.prepareStatement(deleteQuizSql);
            delQuiz.setInt(1, quizId);
            delQuiz.executeUpdate();

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Tạo mới hoặc lấy QuizID hiện tại theo Lesson
    private static int getOrCreateQuizId(Connection conn, int lessonId) throws SQLException {
        String selectSql = "SELECT QuizID FROM Quizzes WHERE LessonID = ?";
        String insertSql = "INSERT INTO Quizzes (LessonID, Title) OUTPUT INSERTED.QuizID VALUES (?, ?)";

        PreparedStatement stmt = conn.prepareStatement(selectSql);
        stmt.setInt(1, lessonId);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("QuizID");
        }

        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
        insertStmt.setInt(1, lessonId);
        insertStmt.setString(2, "Quiz for lesson " + lessonId);
        ResultSet insertRs = insertStmt.executeQuery();
        if (!insertRs.next()) {
            throw new SQLException("Insert quiz failed");
        }
        return insertRs.getInt(1);
    }

    // ✅ Xóa toàn bộ câu hỏi và đáp án cho quiz (nội bộ)
    private static void deleteQuestionsAndAnswers(Connection conn, int quizId) throws SQLException {
        String deleteAnswers = "DELETE FROM Answers WHERE QuestionID IN (SELECT QuestionID FROM Questions WHERE QuizID = ?)";
        String deleteQuestions = "DELETE FROM Questions WHERE QuizID = ?";

        PreparedStatement delAnswers = conn.prepareStatement(deleteAnswers);
        delAnswers.setInt(1, quizId);
        delAnswers.executeUpdate();

        PreparedStatement delQuestions = conn.prepareStatement(deleteQuestions);
        delQuestions.setInt(1, quizId);
        delQuestions.executeUpdate();
    }

    // ✅ Lấy CourseID theo LessonID
    public static int getCourseIdByLessonId(int lessonId) {
        String sql = "SELECT CourseID FROM Lessons WHERE LessonID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("CourseID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if not found
    }

    // ✅ Cập nhật câu hỏi quiz
    public static boolean updateQuestion(int questionId, String questionText, int timeLimit) {
        String sql = "UPDATE Questions SET QuestionText = ?, TimeLimit = ? WHERE QuestionID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, questionText);
            stmt.setInt(2, timeLimit);
            stmt.setInt(3, questionId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Cập nhật đáp án
    public static boolean updateAnswer(int answerId, String answerText, int isCorrect) {
        String sql = "UPDATE Answers SET AnswerText = ?, IsCorrect = ? WHERE AnswerID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, answerText);
            stmt.setInt(2, isCorrect);
            stmt.setInt(3, answerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Xóa câu hỏi cụ thể
    public static boolean deleteQuestion(int questionId) {
        String deleteAnswersSql = "DELETE FROM Answers WHERE QuestionID = ?";
        String deleteQuestionSql = "DELETE FROM Questions WHERE QuestionID = ?";

        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Delete answers first
            PreparedStatement delAnswers = conn.prepareStatement(deleteAnswersSql);
            delAnswers.setInt(1, questionId);
            delAnswers.executeUpdate();

            // Delete question
            PreparedStatement delQuestion = conn.prepareStatement(deleteQuestionSql);
            delQuestion.setInt(1, questionId);
            int rowsAffected = delQuestion.executeUpdate();

            conn.commit();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Lấy thông tin quiz theo lesson ID (bao gồm thống kê)
    public static Map<String, Object> getQuizStatsByLessonId(int lessonId) {
        Map<String, Object> stats = new HashMap<>();
        String sql = "SELECT COUNT(q.QuestionID) as questionCount, "
                + "AVG(q.TimeLimit) as avgTimeLimit "
                + "FROM Questions q "
                + "JOIN Quizzes z ON q.QuizID = z.QuizID "
                + "WHERE z.LessonID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                stats.put("questionCount", rs.getInt("questionCount"));
                stats.put("avgTimeLimit", rs.getDouble("avgTimeLimit"));
            } else {
                stats.put("questionCount", 0);
                stats.put("avgTimeLimit", 0.0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }

    // ✅ Kiểm tra xem lesson có quiz hay không
    public static boolean hasQuiz(int lessonId) {
        String sql = "SELECT COUNT(*) as count FROM Quizzes WHERE LessonID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count") > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ✅ Lấy tất cả quiz theo course ID
    public static Map<Integer, List<QuizQuestion>> getAllQuizzesByCourseId(int courseId) {
        Map<Integer, List<QuizQuestion>> courseQuizzes = new HashMap<>();
        String sql = "SELECT l.LessonID FROM Lessons l WHERE l.CourseID = ?";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int lessonId = rs.getInt("LessonID");
                List<QuizQuestion> questions = getQuestionsWithAnswersByLessonId(lessonId);
                if (!questions.isEmpty()) {
                    courseQuizzes.put(lessonId, questions);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseQuizzes;
    }

    // ✅ Backup quiz data (tạo bản sao)
    public static boolean backupQuiz(int lessonId, String backupName) {
        String createBackupTableSql = "CREATE TABLE QuizBackup_" + backupName + " AS "
                + "SELECT * FROM Quizzes WHERE LessonID = ?";
        String createQuestionsBackupSql = "CREATE TABLE QuestionsBackup_" + backupName + " AS "
                + "SELECT q.* FROM Questions q "
                + "JOIN Quizzes z ON q.QuizID = z.QuizID "
                + "WHERE z.LessonID = ?";
        String createAnswersBackupSql = "CREATE TABLE AnswersBackup_" + backupName + " AS "
                + "SELECT a.* FROM Answers a "
                + "JOIN Questions q ON a.QuestionID = q.QuestionID "
                + "JOIN Quizzes z ON q.QuizID = z.QuizID "
                + "WHERE z.LessonID = ?";

        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Create backup tables
            PreparedStatement backupQuiz = conn.prepareStatement(createBackupTableSql);
            backupQuiz.setInt(1, lessonId);
            backupQuiz.executeUpdate();

            PreparedStatement backupQuestions = conn.prepareStatement(createQuestionsBackupSql);
            backupQuestions.setInt(1, lessonId);
            backupQuestions.executeUpdate();

            PreparedStatement backupAnswers = conn.prepareStatement(createAnswersBackupSql);
            backupAnswers.setInt(1, lessonId);
            backupAnswers.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Restore quiz data từ backup
    public static boolean restoreQuiz(int lessonId, String backupName) {
        // First delete existing quiz
        deleteQuestionsByLessonId(lessonId);

        String restoreQuizSql = "INSERT INTO Quizzes (LessonID, Title) "
                + "SELECT LessonID, Title FROM QuizBackup_" + backupName + " WHERE LessonID = ?";

        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Restore quiz
            PreparedStatement restoreQuiz = conn.prepareStatement(restoreQuizSql);
            restoreQuiz.setInt(1, lessonId);
            restoreQuiz.executeUpdate();

            // Note: Full restore would require more complex logic to handle question and answer IDs
            // This is a simplified version
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteByLessonId(int lessonId) throws SQLException {
        String sql = "DELETE FROM Quizzes WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ps.executeUpdate();
        }
    }
}
