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
    private static final String REDIRECT_URI = "http://localhost:8080/test/oauth2callback";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code == null) {
            response.getWriter().println("Không nhận được code từ Google");
            return;
        }

        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    REDIRECT_URI
            ).execute();

            GoogleCredential credential = new GoogleCredential.Builder()
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JacksonFactory.getDefaultInstance())
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build()
                    .setAccessToken(tokenResponse.getAccessToken());

            Oauth2 oauth2 = new Oauth2.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("SWP_HUY")
                    .build();

            Userinfo userInfo = oauth2.userinfo().get().execute();

            String email = userInfo.getEmail();
            String name = userInfo.getName();

            // Kiểm tra user đã có trong DB chưa, nếu chưa thì thêm mới
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setFullName(name);
                user.setRoleID(1);
                // Bạn có thể đặt mật khẩu mặc định hoặc để trống vì dùng Google login
                userDAO.insertUser(user);
            }

            // Lưu thông tin user vào session
            HttpSession session = request.getSession();
            session.setAttribute("userLogin", user);

            // Redirect về trang home hoặc dashboard
            response.sendRedirect("index.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi đăng nhập Google: " + e.getMessage());
        }
    }
}
