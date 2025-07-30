package Dao;

import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.User;

import java.sql.*;
import java.util.*;
import java.io.InputStream;
import controller.Email.EmailUtil;

public class UserDAO {

    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO [dbo].[Users] (RoleID, Email, PasswordHash, GoogleID, FullName, IsActive, IsLocked, CreatedAt, Gender) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, user.getRoleID());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getGoogleID());
            stmt.setString(5, user.getFullName());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isLocked());
            stmt.setString(8, user.getGender() != null ? user.getGender() : "Khác");
            stmt.executeUpdate();
        }
    }

    public User getUserById(int userID) throws SQLException {
        String sql = "SELECT * FROM [dbo].[Users] WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE [dbo].[Users] SET RoleID = ?, Email = ?, PasswordHash = ?, GoogleID = ?, FullName = ?, "
                + "IsActive = ?, IsLocked = ?, BirthDate = ?, PhoneNumber = ?, JapaneseLevel = ?, Address = ?, "
                + "Country = ?, Avatar = ?, IsTeacherPending = ?, CertificatePath = ? WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            stmt.setBoolean(14, user.isTeacherPending());
            stmt.setString(15, user.getCertificatePath());
            stmt.setInt(16, user.getUserID());
            stmt.executeUpdate();
        }
    }

    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE Users SET Email = ?, FullName = ?, PhoneNumber = ?, BirthDate = ?, "
                + "JapaneseLevel = ?, Address = ?, Country = ?, Avatar = ? WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userID);
            stmt.executeUpdate();
        }
    }

    public boolean createNewUser(String email, String password, String gender) {
        String sql = "INSERT INTO Users (RoleID, Email, PasswordHash, Gender) VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1); // default role
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, gender);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createNewUser(String email, String password, String gender, String role, boolean isTeacherPending, String certificatePath) {
        String sql = "INSERT INTO Users (RoleID, Email, PasswordHash, Gender, IsTeacherPending, CertificatePath) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int roleId = 1; // default user
            if ("teacher".equals(role)) roleId = 1; // vẫn là user thường, chờ xác nhận
            if ("student".equals(role)) roleId = 1;
            pstmt.setInt(1, roleId);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, gender);
            pstmt.setBoolean(5, isTeacherPending);
            pstmt.setString(6, certificatePath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean createNewUser(String email, String fullName, String password, String gender, String role, boolean isTeacherPending, String certificatePath) {
        String sql = "INSERT INTO Users (RoleID, Email, FullName, PasswordHash, Gender, IsTeacherPending, CertificatePath) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int roleId = 1; // default user
            if ("teacher".equals(role)) roleId = 1; // vẫn là user thường, chờ xác nhận
            if ("student".equals(role)) roleId = 1;
            pstmt.setInt(1, roleId);
            pstmt.setString(2, email);
            pstmt.setString(3, fullName);
            pstmt.setString(4, password);
            pstmt.setString(5, gender);
            pstmt.setBoolean(6, isTeacherPending);
            pstmt.setString(7, certificatePath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE Users SET PasswordHash = ? WHERE Email = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    public int getUsersByMonthAndYear(int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) AS Count FROM [dbo].[Users] WHERE MONTH(CreatedAt) = ? AND YEAR(CreatedAt) = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT MONTH(CreatedAt) AS Period, COUNT(*) AS Count FROM Users "
                + "WHERE YEAR(CreatedAt) = ? GROUP BY MONTH(CreatedAt) ORDER BY MONTH(CreatedAt)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        String sql = "SELECT YEAR(CreatedAt) AS Period, COUNT(*) AS Count FROM Users "
                + "GROUP BY YEAR(CreatedAt) ORDER BY YEAR(CreatedAt)";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
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
            sql = "SELECT DATENAME(MONTH, CreatedAt) AS Period, COUNT(*) AS RegistrationCount "
                    + "FROM [dbo].[Users] WHERE YEAR(CreatedAt) = ? "
                    + "GROUP BY DATENAME(MONTH, CreatedAt), MONTH(CreatedAt) "
                    + "ORDER BY MONTH(CreatedAt)";
        } else {
            sql = "SELECT YEAR(CreatedAt) AS Period, COUNT(*) AS RegistrationCount "
                    + "FROM [dbo].[Users] GROUP BY YEAR(CreatedAt) ORDER BY YEAR(CreatedAt)";
        }

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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

    public void updateAvatar(int userId, String avatarUrl) throws SQLException {
        String sql = "UPDATE Users SET Avatar = ? WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, avatarUrl);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
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
            user.setAvatar(rs.getString("Avatar")); // Sử dụng setAvatar thay vì setAvatarUrl
            user.setGender(rs.getString("Gender"));
            user.setCertificatePath(rs.getString("CertificatePath"));
            user.setTeacherPending(rs.getBoolean("IsTeacherPending"));
        } catch (SQLException | NullPointerException ignored) {
        }
        return user;
    }

    public JsonArray getUserCountByRole() throws SQLException {
        JsonArray jsonArray = new JsonArray();
        // Lấy tổng số người dùng trước
        String totalSql = "SELECT COUNT(*) AS Total FROM [dbo].[Users]";
        int totalUsers = 0;
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(totalSql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalUsers = rs.getInt("Total");
            }
        }

        // Lấy số lượng người dùng theo vai trò
        String sql = "SELECT r.RoleName, COUNT(u.UserID) AS UserCount "
                + "FROM [dbo].[Roles] r LEFT JOIN [dbo].[Users] u ON r.RoleID = u.RoleID "
                + "GROUP BY r.RoleName";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                String roleName = rs.getString("RoleName");
                int count = rs.getInt("UserCount");
                double percent = totalUsers > 0 ? ((double) count / totalUsers * 100) : 0;
                obj.addProperty("role", roleName);
                obj.addProperty("count", count);
                obj.addProperty("percent", String.format("%.1f", percent)); // Làm tròn đến 1 chữ số thập phân
                jsonArray.add(obj);
                System.out.println("Role: " + roleName + ", Count: " + count + ", Percent: " + percent);
            }
        }
        System.out.println("JSON trả về: " + jsonArray.toString());
        return jsonArray;
    }

    public List<User> getUsersByRoles(List<String> roleNames) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM [dbo].[Users] u JOIN [dbo].[Roles] r ON u.RoleID = r.RoleID WHERE r.RoleName IN (";
        // Build placeholders for role names
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < roleNames.size(); i++) {
            placeholders.append("?");
            if (i < roleNames.size() - 1) {
                placeholders.append(",");
            }
        }
        sql += placeholders.toString() + ")";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set role names as parameters
            for (int i = 0; i < roleNames.size(); i++) {
                stmt.setString(i + 1, roleNames.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        }
        return users;
    }

    public List<User> searchUsersByFullName(String keyword) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM [dbo].[Users] WHERE dbo.RemoveDiacritics(FullName) LIKE '%' + dbo.RemoveDiacritics(?) + '%' AND IsActive = 1";

        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    public List<User> getPendingTeachers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE IsTeacherPending = 1 AND RoleID = 1";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void approveTeacher(int userId) {
        String sql = "UPDATE Users SET RoleID = 3, IsTeacherPending = 0 WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Gửi email xác nhận giáo viên
        User user = getUserByIdSafe(userId);
        if (user != null) {
            try {
                EmailUtil.sendTeacherApprovedMail(user);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void rejectTeacher(int userId) {
        String sql = "UPDATE Users SET IsTeacherPending = 0 WHERE UserID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Gửi email từ chối giáo viên
        User user = getUserByIdSafe(userId);
        if (user != null) {
            try {
                EmailUtil.sendTeacherRejectedMail(user);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Helper để lấy user không throw exception
    private User getUserByIdSafe(int userId) {
        try {
            return getUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isGoogleUser(String email) {
        String sql = "SELECT PasswordHash, GoogleID FROM Users WHERE email = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String passwordHash = rs.getString("PasswordHash");
                String googleID = rs.getString("GoogleID");
                
                // Kiểm tra cả PasswordHash và GoogleID
                boolean isGoogleByPassword = passwordHash != null && passwordHash.startsWith("GOOGLE_LOGIN_");
                boolean isGoogleByID = googleID != null && !googleID.trim().isEmpty();
                
                return isGoogleByPassword || isGoogleByID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUserByGoogleId(String googleId) {
        String sql = "SELECT * FROM Users WHERE GoogleID = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, googleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        UserDAO dao = new UserDAO();
        User users = dao.getUserById(1);
     
            System.out.println(users);
        
    }
}
