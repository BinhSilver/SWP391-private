package dao;

import java.sql.*;
import model.Vocabulary;
import DB.JDBCConnection;
import java.util.ArrayList;
public class VocabularyDAO {

    public void add(Vocabulary v) throws SQLException {
        String sql = "INSERT INTO Vocabulary (Word, Meaning, Reading, Example) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, v.getWord());
            stmt.setString(2, v.getMeaning());
            stmt.setString(3, v.getReading());
            stmt.setString(4, v.getExample());
            stmt.executeUpdate();
        }
    }

    public void update(Vocabulary v) throws SQLException {
        String sql = "UPDATE Vocabulary SET Word=?, Meaning=?, Reading=?, Example=? WHERE VocabID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, v.getWord());
            stmt.setString(2, v.getMeaning());
            stmt.setString(3, v.getReading());
            stmt.setString(4, v.getExample());
            stmt.setInt(5, v.getVocabID());
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
     public static ArrayList<Vocabulary> searchVocabulary(String keyword) {
        ArrayList<Vocabulary> list = new ArrayList<>();
        String sql = "SELECT * FROM Vocabulary WHERE word LIKE ? OR meaning LIKE ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Vocabulary v = new Vocabulary();
                v.setVocabID(rs.getInt("vocabID"));
                v.setWord(rs.getString("word"));
                v.setMeaning(rs.getString("meaning"));
                v.setReading(rs.getString("reading"));
                v.setExample(rs.getString("example"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
