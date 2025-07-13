package controller.Authentication;

import controller.Email.EmailUtil;
import Dao.CoursesDAO;
import Dao.UserDAO;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Course;
import model.User;
import service.PasswordService;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
@MultipartConfig
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

        if ("change_pass".equals(action)) {
            doPut(request, response);
            return;
        }

        if ("forgot_pass".equals(action)) {
            handleForgotPassword(request, response);
            return;
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String oldpass = request.getParameter("oldPassword");
        String newpass = request.getParameter("newPassword");

        boolean isPasswordChanged = new PasswordService().changePassword(email, oldpass, newpass);
        User checkEmailExist = new UserDAO().getUserByEmail(email);
        if (isPasswordChanged) {
            request.setAttribute("message", "Đã thay đổi mật khẩu thành công!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        } else {
            if (checkEmailExist == null) {
                request.setAttribute("message", "Email không tồn tại!");

                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            request.setAttribute("message", "Mật khẩu cũ không đúng!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }
//Dang nhap
    private void handleSignIn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);

        if (user != null && checkPassword(password, user.getPasswordHash())) {
            if (!user.isActive()) {
                request.setAttribute("message", "Tài khoản chưa được mở khóa. Vui lòng xác thực tài khoản .");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            User fullUser = null;
            try {
                fullUser = dao.getUserById(user.getUserID());
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("message", "Lỗi hệ thống khi đăng nhập!");

                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);

                request.getRequestDispatcher("/LoginJSP/LoginIndex.jsp").forward(request, response);

                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("authUser", fullUser);
            session.setAttribute("userID", fullUser.getUserID());
            session.setMaxInactiveInterval(60 * 60 * 24);

            if ("on".equals(rememberMe)) {
                setRememberMeCookies(response, email);
            } else {
                clearRememberMeCookies(response);
            }

            // ✅ Thêm danh sách khóa học đề xuất
            CoursesDAO coursesDAO = new CoursesDAO();
            List<Course> suggestedCourses = coursesDAO.getSuggestedCourses();
            request.setAttribute("suggestedCourses", suggestedCourses);

            // ✅ Forward về index.jsp để giữ lại dữ liệu
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "Sai email hoặc mật khẩu");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

// Đăng ký
    private void handleSignUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String repass = request.getParameter("repass");
        String gender = request.getParameter("gender");

        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!password.equals(repass)) {
            request.setAttribute("message_signup", "Mật khẩu không trùng khớp!");
            request.setAttribute("registerActive", "true");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        // Kiểm tra email đã tồn tại
        User existingUser = new UserDAO().getUserByEmail(email);
        if (existingUser != null) {
            request.setAttribute("message_signup", "Email đã tồn tại!");
            request.setAttribute("registerActive", "true");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("pending_email", email);
        session.setAttribute("pending_password", password);
        session.setAttribute("pending_gender", gender);
        //session.setAttribute("pending_role", role);

        request.setAttribute("email", email);
        request.setAttribute("password", password);
        request.setAttribute("gender", gender);
        //request.setAttribute("role", role);
        request.setAttribute("registerActive", "true");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);

    }

// Quên mật khẩu
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        // Kích hoạt giao diện form quên mật khẩu (ẩn hiện bằng JSTL)
        request.setAttribute("showForgotForm", true);

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("message_forgot", "Vui lòng nhập email.");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        User user = new UserDAO().getUserByEmail(email);
        if (user == null) {
            request.setAttribute("message_forgot", "Email không tồn tại.");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        // ✅ Email hợp lệ → hiển thị form OTP giống như khi đăng ký
        request.getSession().setAttribute("resetEmail", email); // dùng cho gửi OTP và verify
        request.setAttribute("showOtpForm", true);              // JSP dùng để hiện form nhập mã OTP
        request.setAttribute("email", email);                   // binding lại để form hiển thị email

        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return rawPassword.equals(hashedPassword);
    }

    private void setRememberMeCookies(HttpServletResponse response, String email) {
        Cookie emailCookie = new Cookie("email", email);
        emailCookie.setHttpOnly(true);
        emailCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(emailCookie);
    }

    private void clearRememberMeCookies(HttpServletResponse response) {
        Cookie emailCookie = new Cookie("email", "");
        emailCookie.setMaxAge(0);
        response.addCookie(emailCookie);
    }
}
