package controller.Authentication;

import Dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import service.PasswordService;

import java.io.IOException;
import java.sql.SQLException;

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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String oldpass = request.getParameter("oldPassword");
        String newpass = request.getParameter("newPassword");

        boolean isPasswordChanged = new PasswordService().changePassword(email, oldpass, newpass);
        User checkEmailExist = new UserDAO().getUserByEmail(email);

        if (isPasswordChanged) {
            request.setAttribute("message", "Password has been successfully changed!");
        } else {
            if (checkEmailExist == null) {
                request.setAttribute("message", "Email does not exist!");
            } else {
                request.setAttribute("message", "Incorrect old password!");
            }
        }

        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    private void handleSignIn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);

        if (user != null && checkPassword(password, user.getPasswordHash())) {

            if (!user.isActive()) {
                request.setAttribute("message", "Your account is not active. Please verify OTP.");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }

            try {
                User fullUser = dao.getUserById(user.getUserID());

                HttpSession session = request.getSession();
                session.setAttribute("authUser", fullUser);
                session.setAttribute("user", fullUser);
                session.setAttribute("userID", fullUser.getUserID());
                session.setAttribute("currentUser", fullUser); // ✅ Dòng quan trọng cho JSP
                session.setMaxInactiveInterval(60 * 60 * 24); // 24 giờ

                if ("on".equals(rememberMe)) {
                    setRememberMeCookies(response, email);
                } else {
                    clearRememberMeCookies(response);
                }

                response.sendRedirect("HomeServlet");
                return;

            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("message", "Lỗi hệ thống khi đăng nhập!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            }

        } else {
            request.setAttribute("message", "Invalid email or password!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

    private void handleSignUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String repass = request.getParameter("repass");
        String gender = request.getParameter("gender");

        request.setAttribute("showRegisterForm", true);
        request.setAttribute("registerActive", "active");

        if (!password.equals(repass)) {
            request.setAttribute("message_signup", "Passwords do not match!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        User existingUser = new UserDAO().getUserByEmail(email);
        if (existingUser != null) {
            request.setAttribute("message_signup", "Email already exists!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        new UserDAO().createNewUser(email, password, gender);
        request.setAttribute("showOtpForm", true);
        request.setAttribute("email", email);
        request.setAttribute("message_signup", "Registration successful!");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        // TODO: Nên dùng BCrypt thay vì so sánh chuỗi trực tiếp
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
