package Dao;

import DB.JDBCConnection;
import model.Vocabulary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LessonVocabularyDAO {

    public List<Vocabulary> getVocabularyByLessonID(int lessonID) {
        List<Vocabulary> list = new ArrayList<>();
        String sql = """
            SELECT v.* FROM LessonVocabulary lv
            JOIN Vocabulary v ON lv.VocabID = v.VocabID
            WHERE lv.LessonID = ?
        """;
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vocabulary v = new Vocabulary();
                v.setVocabID(rs.getInt("VocabID"));
                v.setWord(rs.getString("Word"));
                v.setMeaning(rs.getString("Meaning"));
                v.setReading(rs.getString("Reading"));
                v.setExample(rs.getString("Example"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addVocabularyToLesson(int lessonID, int vocabID) throws SQLException {
        String sql = "INSERT INTO LessonVocabulary (LessonID, VocabID) VALUES (?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonID);
            ps.setInt(2, vocabID);
            ps.executeUpdate();
        }
    }

    public void deleteVocabularyFromLesson(int lessonID, int vocabID) throws SQLException {
        String sql = "DELETE FROM LessonVocabulary WHERE LessonID = ? AND VocabID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonID);
            ps.setInt(2, vocabID);
            ps.executeUpdate();
        }
    }

    public void deleteAllVocabForLesson(int lessonID) throws SQLException {
        String sql = "DELETE FROM LessonVocabulary WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonID);
            ps.executeUpdate();
        }
    }
}
