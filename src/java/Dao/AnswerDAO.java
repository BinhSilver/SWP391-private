/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;


import DB.JDBCConnection;
import model.Answer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {

    // Thêm đáp án cho 1 câu hỏi
    public static boolean insertAnswer(Answer answer) {
        String sql = "INSERT INTO Answers (QuestionID, AnswerText, AnswerNumber) VALUES (?, ?, ?)";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, answer.getQuestionId());
            stmt.setString(2, answer.getAnswerText());
            stmt.setInt(3, answer.getAnswerNumber());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy danh sách đáp án theo ID câu hỏi
    public static List<Answer> getAnswersByQuestionId(int questionId) {
        List<Answer> list = new ArrayList<>();
        String sql = "SELECT * FROM Answers WHERE QuestionID = ? ORDER BY AnswerNumber";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Answer answer = new Answer();
                answer.setId(rs.getInt("AnswerID"));
                answer.setQuestionId(rs.getInt("QuestionID"));
                answer.setAnswerText(rs.getString("AnswerText"));
                answer.setAnswerNumber(rs.getInt("AnswerNumber"));
                list.add(answer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Xóa tất cả đáp án theo QuestionID
    public static boolean deleteAnswersByQuestionId(int questionId) {
        String sql = "DELETE FROM Answers WHERE QuestionID = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
