package Dao;

import java.sql.*;
import java.util.ArrayList;
import model.Vocabulary;
import DB.JDBCConnection;
import org.checkerframework.checker.units.qual.A;

public class VocabularyDAO {
    public static ArrayList<Vocabulary> getVocabularyByLessonId(int lessonId) throws SQLException {
        ArrayList<Vocabulary> list = new ArrayList<>();
        String sql = "SELECT * FROM Vocabulary WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lessonId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vocabulary v = new Vocabulary();
                v.setVocabID(rs.getInt("VocabID"));
                v.setLessonID(rs.getInt("LessonID"));
                v.setWord(rs.getString("Word"));
                v.setMeaning(rs.getString("Meaning"));
                v.setReading(rs.getString("Reading"));
                v.setExample(rs.getString("Example"));
                v.setImagePath(rs.getString("imagePath"));
                list.add(v);
            }
        }
        return list;
    }

public static ArrayList<Vocabulary> searchVocabulary(String keyword) throws SQLException {
    ArrayList<Vocabulary> list = new ArrayList<>();
    String sql = "SELECT * FROM Vocabulary WHERE Word LIKE ? OR Meaning LIKE ? OR Reading LIKE ?";
    try (Connection conn = JDBCConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        String searchPattern = "%" + keyword + "%";
        stmt.setString(1, searchPattern);
        stmt.setString(2, searchPattern);
        stmt.setString(3, searchPattern); // Thêm điều kiện Reading
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Vocabulary v = new Vocabulary();
            v.setVocabID(rs.getInt("VocabID"));
            v.setLessonID(rs.getInt("LessonID"));
            v.setWord(rs.getString("Word"));
            v.setMeaning(rs.getString("Meaning"));
            v.setReading(rs.getString("Reading"));
            v.setExample(rs.getString("Example"));
            v.setImagePath(rs.getString("imagePath"));
            list.add(v);
        }
    }
    return list;
}


    public void add(Vocabulary v) throws SQLException {
        String sql = "INSERT INTO Vocabulary (Word, Meaning, Reading, Example, LessonID, imagePath) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, v.getWord());
            stmt.setString(2, v.getMeaning());
            stmt.setString(3, v.getReading());
            stmt.setString(4, v.getExample());
            stmt.setInt(5, v.getLessonID());
            stmt.setString(6, v.getImagePath());
            stmt.executeUpdate();
        }
    }

    public void update(Vocabulary v) throws SQLException {
        String sql = "UPDATE Vocabulary SET Word=?, Meaning=?, Reading=?, Example=?, LessonID=?, imagePath=? WHERE VocabID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, v.getWord());
            stmt.setString(2, v.getMeaning());
            stmt.setString(3, v.getReading());
            stmt.setString(4, v.getExample());
            stmt.setInt(5, v.getLessonID());
            stmt.setString(6, v.getImagePath());
            stmt.setInt(7, v.getVocabID());
            stmt.executeUpdate();
        }
    }

    public void delete(int vocabID) throws SQLException {
        String sql = "DELETE FROM Vocabulary WHERE VocabID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vocabID);
            stmt.executeUpdate();
        }
    }

    public void deleteByLessonId(int lessonId) throws SQLException {
        String sql = "DELETE FROM Vocabulary WHERE LessonID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ps.executeUpdate();
        }
    }
    public static void main(String[] args) throws SQLException {
        VocabularyDAO a = new VocabularyDAO();
        test.Testcase.printlist(a.searchVocabulary("c"));
    }
}