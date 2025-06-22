package Dao;

import DB.JDBCConnection;
import model.Answer;
import model.QuizQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    // ✅ Lưu quiz + câu hỏi + đáp án mới (xóa trước nếu đã tồn tại)
    public static boolean saveQuestions(int lessonId, List<QuizQuestion> questions) {
        String insertQuizSql = "INSERT INTO Quizzes (LessonID, Title) OUTPUT INSERTED.QuizID VALUES (?, ?)";
        String insertQuestionSql = "INSERT INTO Questions (QuizID, QuestionText, TimeLimit) OUTPUT INSERTED.QuestionID VALUES (?, ?, ?)";
        String insertAnswerSql = "INSERT INTO Answers (QuestionID, AnswerText, IsCorrect, AnswerNumber) VALUES (?, ?, ?, ?)";

        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);

            int quizId = getOrCreateQuizId(conn, lessonId);
            deleteQuestionsAndAnswers(conn, quizId);

            for (QuizQuestion question : questions) {
                PreparedStatement qStmt = conn.prepareStatement(insertQuestionSql);
                qStmt.setInt(1, quizId);
                qStmt.setString(2, question.getQuestion());
                qStmt.setInt(3, question.getTimeLimit());
                ResultSet qRs = qStmt.executeQuery();
                if (!qRs.next()) {
                    throw new SQLException("Insert question failed");
                }
                int questionId = qRs.getInt(1);

                for (Answer answer : question.getAnswers()) {
                    PreparedStatement aStmt = conn.prepareStatement(insertAnswerSql);
                    aStmt.setInt(1, questionId);
                    aStmt.setString(2, answer.getAnswerText());
                    aStmt.setInt(3, answer.getIsCorrect());
                    aStmt.setInt(4, answer.getAnswerNumber());
                    aStmt.executeUpdate();
                }
            }

            conn.commit();
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
}
