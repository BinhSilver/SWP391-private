package controller.Authentication;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import Dao.UserDAO;
import model.User;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/oauth2callback")
public class OAuth2CallbackServlet extends HttpServlet {
    private static final String CLIENT_ID = "1025289027596-qkbrdlnf5s31pjg2s7mkmdg0tj8s5c65.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8080/Wasabii/oauth2callback";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        String error = request.getParameter("error");
        
        // Kiểm tra lỗi từ Google
        if (error != null) {
            System.err.println("Google OAuth error: " + error);
            response.sendRedirect(request.getContextPath() + "/login?error=google_oauth_error");
            return;
        }
        
        if (code == null) {
            System.err.println("Không nhận được code từ Google");
            response.sendRedirect(request.getContextPath() + "/login?error=no_code");
            return;
        }

        try {
            System.out.println("Bắt đầu xử lý Google OAuth callback với code: " + code.substring(0, Math.min(code.length(), 10)) + "...");
            
            // Bước 1: Đổi code lấy access token
            String tokenResponse = exchangeCodeForToken(code);
            String accessToken = extractAccessToken(tokenResponse);
            
            if (accessToken == null) {
                System.err.println("Không thể lấy access token");
                response.sendRedirect(request.getContextPath() + "/login?error=token_exchange_failed");
                return;
            }
            
            // Bước 2: Lấy thông tin user từ Google
            String userInfo = getUserInfoFromGoogle(accessToken);
            String email = extractEmail(userInfo);
            String name = extractName(userInfo);
            String googleId = extractGoogleId(userInfo);
            
            System.out.println("Thông tin user từ Google - Email: " + email + ", Name: " + name + ", GoogleID: " + googleId);

            // Kiểm tra user đã có trong DB chưa, nếu chưa thì thêm mới
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);
            
            if (user == null) {
                System.out.println("Tạo user mới từ Google login");
                // Tạo user mới từ Google
                user = new User();
                user.setEmail(email);
                user.setFullName(name);
                user.setGoogleID(googleId);
                user.setRoleID(1); // Role mặc định là student
                user.setActive(true);
                user.setLocked(false);
                user.setPasswordHash("GOOGLE_LOGIN_" + System.currentTimeMillis()); // Password mặc định cho Google login
                
                try {
                    userDAO.insertUser(user);
                    // Lấy lại user đã insert để có UserID
                    user = userDAO.getUserByEmail(email);
                    System.out.println("Tạo user thành công với UserID: " + user.getUserID());
                    
                    // Lưu thông tin user vào session
                    HttpSession session = request.getSession();
                    session.setAttribute("authUser", user);
                    session.setAttribute("userID", user.getUserID());
                    session.setMaxInactiveInterval(60 * 60 * 24); // 24 giờ
                    
                    // Redirect đến trang chọn vai trò cho user mới
                    response.sendRedirect(request.getContextPath() + "/choose-role");
                    return;
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Lỗi khi tạo tài khoản mới: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/login?error=create_user_failed");
                    return;
                }
            } else {
                System.out.println("User đã tồn tại, UserID: " + user.getUserID());
                // User đã tồn tại, cập nhật GoogleID nếu chưa có
                if (user.getGoogleID() == null || user.getGoogleID().isEmpty()) {
                    user.setGoogleID(googleId);
                    try {
                        userDAO.updateUser(user);
                        System.out.println("Cập nhật GoogleID thành công");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Không dừng quá trình login nếu update thất bại
                    }
                }

                // Lưu thông tin user vào session
                HttpSession session = request.getSession();
                session.setAttribute("authUser", user);
                session.setAttribute("userID", user.getUserID());
                session.setMaxInactiveInterval(60 * 60 * 24); // 24 giờ

                System.out.println("Đăng nhập Google thành công, chuyển hướng về trang chủ");
                // Redirect về trang chủ cho user đã tồn tại
                response.sendRedirect(request.getContextPath() + "/HomeServlet");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi đăng nhập Google: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login?error=google_login_failed");
        }
    }
    
    private String exchangeCodeForToken(String code) throws IOException, InterruptedException {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        String postData = String.format(
            "client_id=%s&client_secret=%s&code=%s&grant_type=authorization_code&redirect_uri=%s",
            CLIENT_ID, CLIENT_SECRET, code, URLEncoder.encode(REDIRECT_URI, "UTF-8")
        );
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tokenUrl))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(postData))
            .build();
            
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    private String getUserInfoFromGoogle(String accessToken) throws IOException, InterruptedException {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(userInfoUrl))
            .header("Authorization", "Bearer " + accessToken)
            .GET()
            .build();
            
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    private String extractAccessToken(String tokenResponse) {
        // Parse JSON response để lấy access_token
        Pattern pattern = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(tokenResponse);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private String extractEmail(String userInfo) {
        Pattern pattern = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(userInfo);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private String extractName(String userInfo) {
        Pattern pattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(userInfo);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private String extractGoogleId(String userInfo) {
        Pattern pattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(userInfo);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}