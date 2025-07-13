package controller.Authentication;

import Dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "VerifyOtpServlet", urlPatterns = {"/verifyOtp"})
public class VerifyOtpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String otpInput = request.getParameter("otp");

        HttpSession session = request.getSession();
        String otpSession = (String) session.getAttribute("otp_" + email);
        Long otpTime = (Long) session.getAttribute("otp_time_" + email);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (otpSession == null || otpInput == null || !otpSession.equals(otpInput)) {
            response.getWriter().write("{\"success\":false, \"message\":\"Mã OTP không đúng!\"}");
            return;
        }

        if (otpTime == null || (System.currentTimeMillis() - otpTime > 5 * 60 * 1000)) {
            response.getWriter().write("{\"success\":false, \"message\":\"Mã OTP đã hết hạn!\"}");
            return;
        }

        // Lấy thông tin người dùng đã lưu tạm
        String password = (String) session.getAttribute("pending_password");
        String gender = (String) session.getAttribute("pending_gender");

        // TODO: gọi UserDAO.createNewUser(...) để tạo tài khoản
        UserDAO dao = new UserDAO();
        new UserDAO().createNewUser(email, password, gender);

        // ✅ Sau khi tạo user, lấy lại từ DB
        User user = dao.getUserByEmail(email);
        User fullUser = null;
            try {
                fullUser = dao.getUserById(user.getUserID());
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
                request.setAttribute("message", "Lỗi hệ thống khi đăng nhập!");
                return;
            }

        // ✅ Đăng nhập bằng session
        session.setAttribute("authUser", fullUser);
        session.setAttribute("userID", fullUser.getUserID());
        session.setMaxInactiveInterval(60 * 60 * 24); // 1 ngày

        // Xóa dữ liệu tạm
        session.removeAttribute("otp_" + email);
        session.removeAttribute("otp_time_" + email);
        session.removeAttribute("pending_email");
        session.removeAttribute("pending_password");
        session.removeAttribute("pending_gender");

        // Trả về JSON thành công
        response.getWriter().write("{\"success\":true}");
    }
}
