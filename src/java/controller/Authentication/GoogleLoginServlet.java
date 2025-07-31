/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import java.io.IOException;                 // IO Exception
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.*;              // HTTP Servlet classes
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;  // Google OAuth URL builder

// ===== SERVLET CONFIGURATION =====
/**
 * GoogleLoginServlet - Servlet khởi tạo quá trình đăng nhập Google OAuth
 * 
 * Chức năng chính:
 * - Tạo URL OAuth cho Google
 * - Redirect user đến Google để xác thực
 * - Xử lý lỗi OAuth
 * 
 * URL mapping: /login-google
 * 
 * @author Admin
 */
@WebServlet("/login-google")
public class GoogleLoginServlet extends HttpServlet {
    
    // ===== GOOGLE OAUTH CONSTANTS =====
    // Client ID từ Google Cloud Console
    private static final String CLIENT_ID = "1025289027596-qkbrdlnf5s31pjg2s7mkmdg0tj8s5c65.apps.googleusercontent.com";
    
    // URL callback sau khi Google xác thực xong
    private static final String REDIRECT_URI = "https://wasabii.id.vn/oauth2callback";
    
    // Quyền truy cập cần thiết từ Google
    private static final String SCOPE = "openid email profile";

    // ===== GET METHOD - INITIATE GOOGLE OAUTH =====
    /**
     * Xử lý GET request để bắt đầu quá trình đăng nhập Google
     * Quy trình:
     * 1. Tạo URL OAuth với Google API Client library
     * 2. Redirect user đến Google để xác thực
     * 3. Xử lý lỗi nếu có
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ===== OAUTH FLOW INITIATION =====
            // Bắt đầu quá trình OAuth với Google
            System.out.println("🚀 [GoogleLogin] Bắt đầu Google OAuth flow");
            
            // ===== CREATE OAUTH URL =====
            // Sử dụng Google API Client library để tạo URL OAuth
            String url = new GoogleAuthorizationCodeRequestUrl(
                    CLIENT_ID,
                    REDIRECT_URI,
                    java.util.Arrays.asList(SCOPE.split(" "))
            ).build();

            // ===== REDIRECT TO GOOGLE =====
            // Chuyển hướng user đến Google để xác thực
            System.out.println("🔗 [GoogleLogin] Redirecting to Google OAuth URL: " + url);
            response.sendRedirect(url);
            
        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi khi tạo OAuth URL
            System.err.println("❌ [GoogleLogin] Lỗi khi tạo Google OAuth URL: " + e.getMessage());
            e.printStackTrace();
            
            // ===== ERROR REDIRECT =====
            // Chuyển hướng về trang login với thông báo lỗi
            response.sendRedirect(request.getContextPath() + "/login?error=google_oauth_error");
        }
    }
}