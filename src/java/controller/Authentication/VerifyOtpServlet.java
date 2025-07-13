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
        String role = (String) session.getAttribute("pending_role");
        Boolean isTeacherPending = (Boolean) session.getAttribute("pending_isTeacherPending");
        String certificatePath = (String) session.getAttribute("pending_certificatePath");

        if (role != null && "teacher".equals(role)) {
            new UserDAO().createNewUser(email, password, gender, role, isTeacherPending != null && isTeacherPending, certificatePath);
        } else {
            new UserDAO().createNewUser(email, password, gender, role, false, null);
        }

        // Xóa dữ liệu tạm
        session.removeAttribute("otp_" + email);
        session.removeAttribute("otp_time_" + email);
        session.removeAttribute("pending_email");
        session.removeAttribute("pending_password");
        session.removeAttribute("pending_gender");
        session.removeAttribute("pending_role");
        session.removeAttribute("pending_isTeacherPending");
        session.removeAttribute("pending_certificatePath");

        // Trả về JSON thành công
        response.getWriter().write("{\"success\":true}");
    }
}
