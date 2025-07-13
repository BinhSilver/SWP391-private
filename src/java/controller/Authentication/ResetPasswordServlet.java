package controller.Authentication;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import Dao.UserDAO;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");

        // Kiểm tra hợp lệ đơn giản
        if (email == null || email.isEmpty() || newPassword == null || newPassword.length() < 6) {
            response.getWriter().write("error");
            return;
        }

        // Gọi DAO để cập nhật mật khẩu
        boolean success = UserDAO.updatePassword(email, newPassword);

        if (success) {
            response.getWriter().write("ok");
        } else {
            response.getWriter().write("error");
        }
    }
}
