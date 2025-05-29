package dao;

import java.sql.*;
import model.GrammarPoint;
import DB.JDBCConnection;

public class GrammarPointsDAO {

    public void add(GrammarPoint g) throws SQLException {
        String sql = "INSERT INTO GrammarPoints (LessonID, Title, Explanation) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, g.getLessonID());
            stmt.setString(2, g.getTitle());
            stmt.setString(3, g.getExplanation());
            stmt.executeUpdate();
        }
    }

    public void update(GrammarPoint g) throws SQLException {
        String sql = "UPDATE GrammarPoints SET LessonID=?, Title=?, Explanation=? WHERE GrammarID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, g.getLessonID());
            stmt.setString(2, g.getTitle());
            stmt.setString(3, g.getExplanation());
            stmt.setInt(4, g.getGrammarID());
            stmt.executeUpdate();
        }
    }

    public void delete(int grammarID) throws SQLException {
        String sql = "DELETE FROM GrammarPoints WHERE GrammarID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grammarID);
            stmt.executeUpdate();
        }
    }
}
