package controller.Authentication;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import Dao.UserDAO;
import model.User;

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
            
            // Bước 1: Exchange code for access token sử dụng Google API Client
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    REDIRECT_URI
            ).execute();

            // Bước 2: Tạo credential
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build()
                    .setAccessToken(tokenResponse.getAccessToken());

            // Bước 3: Lấy thông tin user từ Google
            Oauth2 oauth2 = new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Wasabii")
                    .build();

            Userinfo userInfo = oauth2.userinfo().get().execute();
            String email = userInfo.getEmail();
            String name = userInfo.getName();
            String googleId = userInfo.getId();

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

                // Load premium info for all users (Free, Premium, Teacher, Admin)
                try {
                    Dao.UserPremiumDAO userPremiumDAO = new Dao.UserPremiumDAO();
                    model.UserPremium premium = userPremiumDAO.getCurrentUserPremium(user.getUserID());
                    if (premium != null) {
                        System.out.println("Premium info loaded for Google user: " + user.getUserID() + " (role: " + user.getRoleID() + ")");
                    }
                } catch (Exception e) {
                    System.out.println("Error loading premium info for Google user: " + e.getMessage());
                    e.printStackTrace();
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
}