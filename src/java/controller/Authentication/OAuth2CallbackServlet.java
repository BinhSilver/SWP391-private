package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import java.io.IOException;                 // IO Exception
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.*;              // HTTP Servlet classes

// ===== GOOGLE API CLIENT IMPORTS =====
import com.google.api.client.googleapis.auth.oauth2.*;  // Google OAuth classes
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;  // HTTP Transport
import com.google.api.client.json.jackson2.JacksonFactory;  // JSON Factory

// ===== GOOGLE OAUTH2 SERVICE IMPORTS =====
import com.google.api.services.oauth2.Oauth2;  // OAuth2 service
import com.google.api.services.oauth2.model.Userinfo;  // User info model

// ===== APPLICATION IMPORTS =====
import Dao.UserDAO;                         // Data Access Object cho Users
import model.User;                          // User model

// ===== SERVLET CONFIGURATION =====
/**
 * OAuth2CallbackServlet - Servlet xử lý callback từ Google OAuth
 * 
 * Chức năng chính:
 * - Nhận authorization code từ Google
 * - Exchange code lấy access token
 * - Lấy thông tin user từ Google
 * - Tạo hoặc cập nhật user trong database
 * - Set session và redirect
 * 
 * URL mapping: /oauth2callback
 */
@WebServlet("/oauth2callback")
public class OAuth2CallbackServlet extends HttpServlet {
    
    // ===== GOOGLE OAUTH CONSTANTS =====
    // Client ID từ Google Cloud Console
    private static final String CLIENT_ID = "1025289027596-qkbrdlnf5s31pjg2s7mkmdg0tj8s5c65.apps.googleusercontent.com";
    
    // Client Secret từ Google Cloud Console
    private static final String CLIENT_SECRET = "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";
    
    // URL callback sau khi Google xác thực xong
    private static final String REDIRECT_URI = "https://wasabii.id.vn/oauth2callback";

    // ===== GET METHOD - HANDLE OAUTH CALLBACK =====
    /**
     * Xử lý GET request từ Google OAuth callback
     * Quy trình:
     * 1. Nhận authorization code từ Google
     * 2. Exchange code lấy access token
     * 3. Lấy thông tin user từ Google
     * 4. Tạo hoặc cập nhật user trong database
     * 5. Set session và redirect
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // ===== GET OAUTH PARAMETERS =====
        // Lấy authorization code và error từ Google
        String code = request.getParameter("code");
        String error = request.getParameter("error");
        
        // ===== ERROR HANDLING =====
        // Kiểm tra lỗi từ Google
        if (error != null) {
            System.err.println("❌ [OAuth2Callback] Google OAuth error: " + error);
            response.sendRedirect(request.getContextPath() + "/login?error=google_oauth_error");
            return;
        }
        
        // ===== CODE VALIDATION =====
        // Kiểm tra có nhận được code không
        if (code == null) {
            System.err.println("❌ [OAuth2Callback] Không nhận được code từ Google");
            response.sendRedirect(request.getContextPath() + "/login?error=no_code");
            return;
        }

        try {
            // ===== OAUTH PROCESSING =====
            // Bắt đầu xử lý Google OAuth callback
            System.out.println("🔄 [OAuth2Callback] Bắt đầu xử lý Google OAuth callback với code: " + code.substring(0, Math.min(code.length(), 10)) + "...");
            
            // ===== STEP 1: EXCHANGE CODE FOR ACCESS TOKEN =====
            // Sử dụng Google API Client để exchange code lấy access token
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    REDIRECT_URI
            ).execute();

            // ===== STEP 2: CREATE CREDENTIAL =====
            // Tạo credential với access token
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build()
                    .setAccessToken(tokenResponse.getAccessToken());

            // ===== STEP 3: GET USER INFO FROM GOOGLE =====
            // Lấy thông tin user từ Google OAuth2 API
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

            System.out.println("👤 [OAuth2Callback] Thông tin user từ Google - Email: " + email + ", Name: " + name + ", GoogleID: " + googleId);

            // ===== DATABASE OPERATIONS =====
            // Kiểm tra user đã có trong DB chưa, nếu chưa thì thêm mới
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);
            
            if (user == null) {
                // ===== CREATE NEW USER =====
                // Tạo user mới từ Google login
                System.out.println("🆕 [OAuth2Callback] Tạo user mới từ Google login");
                user = new User();
                user.setEmail(email);
                user.setFullName(name);
                user.setGoogleID(googleId);
                user.setRoleID(1); // Role mặc định là student
                user.setActive(true);
                user.setLocked(false);
                user.setPasswordHash("GOOGLE_LOGIN_" + System.currentTimeMillis()); // Password mặc định cho Google login
                
                try {
                    // ===== INSERT USER TO DATABASE =====
                    // Thêm user mới vào database
                    userDAO.insertUser(user);
                    
                    // ===== GET INSERTED USER =====
                    // Lấy lại user đã insert để có UserID
                    user = userDAO.getUserByEmail(email);
                    System.out.println("✅ [OAuth2Callback] Tạo user thành công với UserID: " + user.getUserID());
                    
                    // ===== SESSION MANAGEMENT =====
                    // Lưu thông tin user vào session
                    HttpSession session = request.getSession();
                    session.setAttribute("authUser", user);
                    session.setAttribute("userID", user.getUserID());
                    session.setMaxInactiveInterval(60 * 60 * 24); // 24 giờ
                    
                    // ===== REDIRECT FOR NEW USER =====
                    // Redirect đến trang chọn vai trò cho user mới
                    response.sendRedirect(request.getContextPath() + "/choose-role");
                    return;
                    
                } catch (Exception e) {
                    // ===== ERROR HANDLING =====
                    // Xử lý lỗi khi tạo user mới
                    e.printStackTrace();
                    System.err.println("❌ [OAuth2Callback] Lỗi khi tạo tài khoản mới: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/login?error=create_user_failed");
                    return;
                }
            } else {
                // ===== EXISTING USER HANDLING =====
                // User đã tồn tại, cập nhật GoogleID nếu chưa có
                System.out.println("👤 [OAuth2Callback] User đã tồn tại, UserID: " + user.getUserID());
                
                if (user.getGoogleID() == null || user.getGoogleID().isEmpty()) {
                    // ===== UPDATE GOOGLE ID =====
                    // Cập nhật GoogleID cho user hiện tại
                    user.setGoogleID(googleId);
                    try {
                        userDAO.updateUser(user);
                        System.out.println("✅ [OAuth2Callback] Cập nhật GoogleID thành công");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Không dừng quá trình login nếu update thất bại
                        System.err.println("⚠️ [OAuth2Callback] Lỗi cập nhật GoogleID: " + e.getMessage());
                    }
                }

                // ===== LOAD PREMIUM INFO =====
                // Load premium info for all users (Free, Premium, Teacher, Admin)
                try {
                    Dao.UserPremiumDAO userPremiumDAO = new Dao.UserPremiumDAO();
                    model.UserPremium premium = userPremiumDAO.getCurrentUserPremium(user.getUserID());
                    if (premium != null) {
                        System.out.println("💎 [OAuth2Callback] Premium info loaded for Google user: " + user.getUserID() + " (role: " + user.getRoleID() + ")");
                    }
                } catch (Exception e) {
                    System.out.println("⚠️ [OAuth2Callback] Error loading premium info for Google user: " + e.getMessage());
                    e.printStackTrace();
                }

                // ===== SESSION MANAGEMENT =====
                // Lưu thông tin user vào session
                HttpSession session = request.getSession();
                session.setAttribute("authUser", user);
                session.setAttribute("userID", user.getUserID());
                session.setMaxInactiveInterval(60 * 60 * 24); // 24 giờ

                // ===== REDIRECT FOR EXISTING USER =====
                // Redirect về trang chủ cho user đã tồn tại
                System.out.println("✅ [OAuth2Callback] Đăng nhập Google thành công, chuyển hướng về trang chủ");
                response.sendRedirect(request.getContextPath() + "/HomeServlet");
            }

        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi đăng nhập Google
            e.printStackTrace();
            System.err.println("❌ [OAuth2Callback] Lỗi đăng nhập Google: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login?error=google_login_failed");
        }
    }
}