package controller.RealTime;

import DB.JDBCConnection;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Logger;

@WebServlet("/getDailyToken")
public class DailyTokenServlet extends HttpServlet {
    private static final String API_KEY = "6f4b7e9e1f388b12028895794090635c5f373d9fa7dd31110cf0c0601628f0e5"; // Thay bằng API key từ Daily.co
    private static final Logger LOGGER = Logger.getLogger(DailyTokenServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password"); // Nhận password thô

        if (username == null || password == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username and password are required");
            return;
        }

        LOGGER.info("Request for token with username: " + username);

        try (Connection conn = JDBCConnection.getConnection()) {
            if (conn == null) {
                LOGGER.severe("Database connection failed");
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection failed");
                return;
            }
            // Sửa SQL để khớp với bảng Users
            String sql = "SELECT UserID, PasswordHash FROM [dbo].[Users] WHERE Email = ?"; // Sử dụng Email thay vì username
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("PasswordHash"); // Lấy PasswordHash
                        if (password.equals(storedPassword)) { // So sánh trực tiếp vì password thô
                            int userId = rs.getInt("UserID");
                            
                            // TODO: Thêm thư viện JWT vào lib folder để sử dụng token thật
                            // String token = Jwts.builder()
                            //     .setSubject(String.valueOf(userId))
                            //     .claim("name", username)
                            //     .setIssuedAt(new Date())
                            //     .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 giờ
                            //     .signWith(SignatureAlgorithm.HS256, API_KEY.getBytes())
                            //     .compact();
                            
                            // Tạo token giả lập cho đến khi có thư viện JWT
                            String token = "dummy_token_user_" + userId + "_" + System.currentTimeMillis();
                            
                            response.setContentType("text/plain");
                            response.getWriter().write(token);
                        } else {
                            LOGGER.warning("Invalid credentials for username: " + username);
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
                        }
                    } else {
                        LOGGER.warning("User not found: " + username);
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error in DailyTokenServlet: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }
}