package controller.Email;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Random;
import jakarta.mail.MessagingException;

@WebServlet("/send-forgot-otp")
public class SendForgotOtpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        if (email == null || email.isEmpty()) {
            response.getWriter().write("error");
            return;
        }

        // Tạo mã OTP ngẫu nhiên 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Lưu OTP vào session
        HttpSession session = request.getSession();
        session.setAttribute("otp_" + email, otp);
        session.setAttribute("otp_time_" + email, System.currentTimeMillis());

        try {
            EmailUtil.sendOtpEmailForResetPassword(email, otp);  // Gửi email khác biệt
            response.getWriter().write("ok");
        } catch (MessagingException e) {
            e.printStackTrace();
            response.getWriter().write("error");
        }
    }
}
