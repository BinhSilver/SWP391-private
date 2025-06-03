package dao;

import java.sql.*;
import model.Tag;
import DB.JDBCConnection;

public class TagsDAO {

    public void add(Tag t) throws SQLException {
        String sql = "INSERT INTO Tags (TagName) VALUES (?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getTagName());
            stmt.executeUpdate();
        }
    }

    public void update(Tag t) throws SQLException {
        String sql = "UPDATE Tags SET TagName=? WHERE TagID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, t.getTagName());
            stmt.setInt(2, t.getTagID());
            stmt.executeUpdate();
        }
    }

    public void delete(int tagID) throws SQLException {
        String sql = "DELETE FROM Tags WHERE TagID=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tagID);
            stmt.executeUpdate();
        }
    }
}
