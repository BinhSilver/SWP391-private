package controller.Authentication;

import controller.Email.EmailUtil;
import Dao.UserDAO;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        if ("change_pass".equals(action)) {
            doPut(request, response);
            return;
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String oldpass = request.getParameter("oldPassword");
        String newpass = request.getParameter("newPassword");

// Gọi dịch vụ để thay đổi mật khẩu
        boolean isPasswordChanged = new PasswordService().changePassword(email, oldpass, newpass);
        User checkEmailExist = new UserDAO().getUserByEmail(email);
        if (isPasswordChanged) {
            request.setAttribute("message", "Password has been successfully changed!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        } else {
            if (checkEmailExist == null) {
                request.setAttribute("message", "Email does not exist!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            request.setAttribute("message", "Incorrect old password!");
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
            // Kiểm tra nếu tài khoản chưa được kích hoạt (IsActive = 0)
            if (!user.isActive()) {  // Sử dụng phương thức getter isActive() nếu trường là boolean
                request.setAttribute("message", "Your account is not active. Please verify OTP.");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            // Đăng nhập thành công
            // Lấy lại user mới nhất từ DB (để đảm bảo luôn đồng bộ thông tin)
            User fullUser = null;
            try {
                fullUser = dao.getUserById(user.getUserID());
            } catch (SQLException e) {
                e.printStackTrace(); // log lỗi hoặc chuyển hướng đến trang báo lỗi
                request.setAttribute("message", "Lỗi hệ thống khi đăng nhập!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("authUser", fullUser); // Cập nhật lại phiên bản user đầy đủ
            session.setAttribute("userID", fullUser.getUserID()); // Có thể dùng để truy xuất lại sau này
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

        // Đặt trạng thái form để hiển thị form đăng ký khi có lỗi
        request.setAttribute("showRegisterForm", true);
        request.setAttribute("registerActive", "active");

        // Kiểm tra mật khẩu có khớp không
        if (!password.equals(repass)) {
            request.setAttribute("message_signup", "Passwords do not match!");
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
        request.setAttribute("showOtpForm", true);
        request.setAttribute("email", email);
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);

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
