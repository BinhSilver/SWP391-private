package Dao;

import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.User;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserDAO {

    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO [dbo].[Users] (RoleID, Email, PasswordHash, GoogleID, FullName, IsActive, IsLocked) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getRoleID());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getGoogleID());
            stmt.setString(5, user.getFullName());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isLocked());
            stmt.executeUpdate();
        }
    }

    public User getUserById(int userID) throws SQLException {
        String sql = "SELECT * FROM [dbo].[Users] WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Users]";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE [dbo].[Users] SET RoleID = ?, Email = ?, PasswordHash = ?, GoogleID = ?, FullName = ?, " +
                "IsActive = ?, IsLocked = ?, BirthDate = ?, PhoneNumber = ?, JapaneseLevel = ?, Address = ?, " +
                "Country = ?, Avatar = ? WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getRoleID());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getGoogleID());
            stmt.setString(5, user.getFullName());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isLocked());
            stmt.setDate(8, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);
            stmt.setString(9, user.getPhoneNumber());
            stmt.setString(10, user.getJapaneseLevel());
            stmt.setString(11, user.getAddress());
            stmt.setString(12, user.getCountry());
            stmt.setString(13, user.getAvatar());
            stmt.setInt(14, user.getUserID());
            stmt.executeUpdate();
        }
    }

    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE Users SET Email = ?, FullName = ?, PhoneNumber = ?, BirthDate = ?, " +
                "JapaneseLevel = ?, Address = ?, Country = ?, Avatar = ? WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setDate(4, user.getBirthDate() != null ? new java.sql.Date(user.getBirthDate().getTime()) : null);
            stmt.setString(5, user.getJapaneseLevel());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getCountry());
            stmt.setString(8, user.getAvatar());
            stmt.setInt(9, user.getUserID());
            return stmt.executeUpdate() > 0;
        }
    }

    public void deleteUser(int userID) throws SQLException {
        String sql = "DELETE FROM [dbo].[Users] WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        }
    }

    public boolean createNewUser(String email, String rawPassword) {
        String sql = "INSERT INTO Users (RoleID, Email, PasswordHash) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1); // default role
            pstmt.setString(2, email);
            pstmt.setString(3, rawPassword);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) AS Total FROM [dbo].[Users]";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    public int getUsersByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Users] WHERE MONTH(CreatedAt) = ? AND YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public List<JsonObject> getUserCountByMonth(int year) throws SQLException {
        List<JsonObject> list = new ArrayList<>();
        String sql = "SELECT MONTH(CreatedAt) AS Period, COUNT(*) AS Count FROM Users " +
                "WHERE YEAR(CreatedAt) = ? GROUP BY MONTH(CreatedAt) ORDER BY MONTH(CreatedAt)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", "Tháng " + rs.getInt("Period"));
                obj.addProperty("count", rs.getInt("Count"));
                list.add(obj);
            }
        }
        return list;
    }

    public List<JsonObject> getUserCountByYear() throws SQLException {
        List<JsonObject> list = new ArrayList<>();
        String sql = "SELECT YEAR(CreatedAt) AS Period, COUNT(*) AS Count FROM Users " +
                "GROUP BY YEAR(CreatedAt) ORDER BY YEAR(CreatedAt)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", String.valueOf(rs.getInt("Period")));
                obj.addProperty("count", rs.getInt("Count"));
                list.add(obj);
            }
        }
        return list;
    }

    public JsonArray getRegistrationsByPeriod(String periodType) {
        JsonArray jsonArray = new JsonArray();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        String sql;
        if (periodType.equalsIgnoreCase("month")) {
            sql = "SELECT DATENAME(MONTH, CreatedAt) AS Period, COUNT(*) AS RegistrationCount " +
                    "FROM [dbo].[Users] WHERE YEAR(CreatedAt) = ? " +
                    "GROUP BY DATENAME(MONTH, CreatedAt), MONTH(CreatedAt) " +
                    "ORDER BY MONTH(CreatedAt)";
        } else {
            sql = "SELECT YEAR(CreatedAt) AS Period, COUNT(*) AS RegistrationCount " +
                    "FROM [dbo].[Users] GROUP BY YEAR(CreatedAt) ORDER BY YEAR(CreatedAt)";
        }

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (periodType.equalsIgnoreCase("month")) {
                stmt.setInt(1, currentYear);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("period", rs.getString("Period"));
                    obj.addProperty("count", rs.getInt("RegistrationCount"));
                    jsonArray.add(obj);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (periodType.equalsIgnoreCase("month")) {
            JsonArray fullYearArray = new JsonArray();
            String[] months = {"January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"};
            for (String month : months) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", month);
                obj.addProperty("count", 0);
                for (int j = 0; j < jsonArray.size(); j++) {
                    JsonObject existing = jsonArray.get(j).getAsJsonObject();
                    if (existing.get("period").getAsString().equalsIgnoreCase(month)) {
                        obj.addProperty("count", existing.get("count").getAsInt());
                        break;
                    }
                }
                fullYearArray.add(obj);
            }
            return fullYearArray;
        }

        return jsonArray;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setRoleID(rs.getInt("RoleID"));
        user.setEmail(rs.getString("Email"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setGoogleID(rs.getString("GoogleID"));
        user.setFullName(rs.getString("FullName"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));
        user.setActive(rs.getBoolean("IsActive"));
        user.setLocked(rs.getBoolean("IsLocked"));
        try {
            user.setBirthDate(rs.getDate("BirthDate"));
            user.setPhoneNumber(rs.getString("PhoneNumber"));
            user.setJapaneseLevel(rs.getString("JapaneseLevel"));
            user.setAddress(rs.getString("Address"));
            user.setCountry(rs.getString("Country"));
            user.setAvatar(rs.getString("Avatar"));
        } catch (SQLException | NullPointerException ignored) {}
        return user;
    }

    public static void main(String[] args) throws SQLException {
        UserDAO dao = new UserDAO();
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            System.out.println(u);
        }
    }
}
