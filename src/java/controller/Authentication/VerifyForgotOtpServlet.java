package controller.Authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/verify-forgot-otp")
public class VerifyForgotOtpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String otp = request.getParameter("otp");

        HttpSession session = request.getSession();
        String sessionKey = "otp_" + email;
        String sessionOtp = (String) session.getAttribute("otp_" + email);
        Long otpTime = (Long) session.getAttribute("otp_time_" + email);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Kiểm tra OTP hợp lệ và không quá 5 phút
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            long now = System.currentTimeMillis();
            if (otpTime != null && now - otpTime <= 5 * 60 * 1000) {
                // Xác thực thành công
                session.removeAttribute(sessionKey);
                session.removeAttribute("otp_time_" + email);

                out.write("{\"success\": true, \"message\": \"Xác thực OTP thành công.\"}");
            } else {
                out.write("{\"success\": false, \"message\": \"Mã OTP đã hết hạn.\"}");
            }
        } else {
            out.write("{\"success\": false, \"message\": \"Mã OTP không chính xác.\"}");
        }
    }
}
