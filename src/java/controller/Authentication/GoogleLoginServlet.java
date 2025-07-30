/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.Authentication;

/**
 *
 * @author Admin
 */
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

@WebServlet("/login-google")
public class GoogleLoginServlet extends HttpServlet {
    private static final String CLIENT_ID = "1025289027596-qkbrdlnf5s31pjg2s7mkmdg0tj8s5c65.apps.googleusercontent.com";
    private static final String REDIRECT_URI = "http://localhost:8080/Wasabii/oauth2callback";
    private static final String SCOPE = "openid email profile";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            System.out.println("Bắt đầu Google OAuth flow");
            
            // Sử dụng Google API Client library để tạo URL OAuth
            String url = new GoogleAuthorizationCodeRequestUrl(
                    CLIENT_ID,
                    REDIRECT_URI,
                    java.util.Arrays.asList(SCOPE.split(" "))
            ).build();

            System.out.println("Redirecting to Google OAuth URL: " + url);
            response.sendRedirect(url);
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo Google OAuth URL: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login?error=google_oauth_error");
        }
    }
}