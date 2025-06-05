package controller.Authentication;

import controller.Email.EmailUtil;
import dao.UserDAO;
import jakarta.mail.MessagingException;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import service.PasswordService;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("signin".equals(action)) {
            handleSignIn(request, response);
            return;
        }

        if ("signup".equals(action)) {
            handleSignUp(request, response);
            return;
        }
        
        if ("forgot_pass".equals(action)) {
            try {
                handleForgotPassword(request, response);
            } catch (MessagingException ex) {
                Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        return;}
        
        if ("reset_pass".equals(action)) {
                handleResetPassword(request, response);
        return;
        }
        if ("change_pass".equals(action)) {
            doPut(request, response);
        }
        
    }
    
//Dang nhap
    private void handleSignIn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        User user = new UserDAO().getUserByEmail(email);

        if (user != null && checkPassword(password, user.getPasswordHash())) {
            // Đăng nhập thành công
            HttpSession session = request.getSession();
            session.setAttribute("authUser", user);
            session.setMaxInactiveInterval(60 * 60 * 24); // 1 ngày

            if ("on".equals(rememberMe)) {
                setRememberMeCookies(response, email);
            } else {
                clearRememberMeCookies(response);
            }

            // Chuyển hướng về trang chủ
            response.sendRedirect("index.jsp");
            
        } else {
            // Đăng nhập thất bại
            request.setAttribute("message", "Invalid email or password!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

// Đăng ký
    private void handleSignUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String repass = request.getParameter("repass");
    String otpInput = request.getParameter("otp");

    // Đặt trạng thái form để hiển thị form đăng ký khi có lỗi
    request.setAttribute("showRegisterForm", true);
    request.setAttribute("registerActive", "active");

    // Kiểm tra mật khẩu có khớp không
    if (!password.equals(repass)) {
        request.setAttribute("message_signup", "Passwords do not match!");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        return;
    }

    // Kiểm tra mã OTP
    HttpSession session = request.getSession();
    String otpSession = (String) session.getAttribute("otp_" + email);
    Long otpTime = (Long) session.getAttribute("otp_time_" + email);

    if (otpSession == null || otpInput == null || !otpSession.equals(otpInput)) {
        request.setAttribute("message_signup", "Invalid OTP code!");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        return;
    }

    // Kiểm tra thời gian hiệu lực OTP (5 phút)
    if (otpTime == null || (System.currentTimeMillis() - otpTime > 5 * 60 * 1000)) {
        request.setAttribute("message_signup", "OTP code has expired! Please request a new one.");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        return;
    }

    // Kiểm tra email đã tồn tại chưa
    User existingUser = new UserDAO().getUserByEmail(email);
    if (existingUser != null) {
        request.setAttribute("message_signup", "Email already exists!");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        return;
    }

    // Nếu không có lỗi, tạo tài khoản mới
    new UserDAO().createNewUser(email, password);

    // Xóa mã OTP đã dùng
    session.removeAttribute("otp_" + email);
    session.removeAttribute("otp_time_" + email);

    request.setAttribute("message_signup", "Registration successful!");
    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
}
 
//Quen mat khau
    protected void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, MessagingException {
    String email = request.getParameter("email");
    UserDAO userDao = new UserDAO();
    User user = userDao.getUserByEmail(email);

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    if (user == null) {
        response.getWriter().write("{\"success\":false, \"message\":\"Email không tồn tại!\"}");
        return;
    }

    String otp = String.format("%06d", new Random().nextInt(999999));

    HttpSession session = request.getSession();
    session.setAttribute("otp_" + email, otp);
    session.setAttribute("otp_time_" + email, System.currentTimeMillis());

    try {
        EmailUtil.sendOtpEmailForResetPassword(email, otp);
        response.getWriter().write("{\"success\":true}");
    } catch (MessagingException e) {
        response.getWriter().write("{\"success\":false, \"message\":\"Gửi mã OTP thất bại, vui lòng thử lại.\"}");
    }
}

//Cap nhat mat khau moi  
private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    try {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);

        if (user == null) {
            response.getWriter().write("{\"success\":false, \"message\":\"Email không tồn tại.\"}");
            return;
        }

        boolean updated = dao.updatePassword(email, newPassword);
        if (updated) {
            response.getWriter().write("{\"success\":true}");
        } else {
            response.getWriter().write("{\"success\":false, \"message\":\"Cập nhật mật khẩu thất bại.\"}");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        response.getWriter().write("{\"success\":false, \"message\":\"Lỗi server: " + ex.getMessage() + "\"}");
    } finally {
        response.getWriter().flush();
    }
}



    private boolean checkPassword(String rawPassword, String hashedPassword) {
        // TODO: Thay bằng hàm kiểm tra mật khẩu băm thực tế, ví dụ dùng BCrypt
        // Ví dụ đơn giản giả định mật khẩu lưu thô (KHÔNG NÊN làm vậy ở thực tế)
        return rawPassword.equals(hashedPassword);
    }

    private void setRememberMeCookies(HttpServletResponse response, String email) {
        Cookie emailCookie = new Cookie("email", email);
        emailCookie.setHttpOnly(true);
        emailCookie.setMaxAge(60 * 60 * 24 * 7); // 7 ngày
        response.addCookie(emailCookie);
    }

    private void clearRememberMeCookies(HttpServletResponse response) {
        Cookie emailCookie = new Cookie("email", "");
        emailCookie.setMaxAge(0);
        response.addCookie(emailCookie);
    }
    
}
