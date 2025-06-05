package Dao;

import DB.JDBCConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.User;

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
                user.setBirthDate(rs.getDate("BirthDate"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setJapaneseLevel(rs.getString("JapaneseLevel"));
                user.setAddress(rs.getString("Address"));
                user.setCountry(rs.getString("Country"));
                user.setAvatar(rs.getString("Avatar"));
                return user;
            }
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
                users.add(user);
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE [dbo].[Users] SET RoleID = ?, Email = ?, PasswordHash = ?, GoogleID = ?, FullName = ?, "
                + "IsActive = ?, IsLocked = ?, BirthDate = ?, PhoneNumber = ?, JapaneseLevel = ?, Address = ?, "
                + "Country = ?, Avatar = ? WHERE UserID = ?";
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

    public User getUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setRoleID(rs.getInt("roleID"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("PasswordHash"));
                user.setGoogleID(rs.getString("googleID"));
                user.setFullName(rs.getString("fullName"));
                user.setCreatedAt(rs.getDate("createdAt"));
                user.setActive(rs.getBoolean("isActive"));
                user.setLocked(rs.getBoolean("isLocked"));
                user.setBirthDate(rs.getDate("BirthDate"));
                user.setPhoneNumber(rs.getString("PhoneNumber"));
                user.setJapaneseLevel(rs.getString("JapaneseLevel"));
                user.setAddress(rs.getString("Address"));
                user.setCountry(rs.getString("Country"));
                user.setAvatar(rs.getString("Avatar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
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

    public static void main(String[] args) throws SQLException {
        UserDAO dao = new UserDAO();

        // In danh sách user hiện tại
        List<User> userstrc = dao.getAllUsers();
        for (User u : userstrc) {
            System.out.println(u);
        }

        // Thử cập nhật profile người dùng có ID = 1
        try {
            User user = new User();
            user.setUserID(1); // ⚠️ Đảm bảo ID này tồn tại trong DB

            user.setEmail("jdk22_test@example.com");
            user.setFullName("Test User JDK21");
            user.setPhoneNumber("0909123456");

            String birthDateStr = "2000-01-01";
            Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDateStr);
            user.setBirthDate(birthDate);

            user.setJapaneseLevel("N3");
            user.setAddress("1-2-3 Tokyo");
            user.setCountry("Japan");
            user.setAvatar("https://example.com/avatar.png");

            boolean updated = dao.updateProfile(user);
            System.out.println(updated ? "✅ Update thành công" : "❌ Update thất bại");

        } catch (Exception e) {
            e.printStackTrace();
        }
        // In danh sách user hiện tại
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            System.out.println(u);
        }
    }
}
