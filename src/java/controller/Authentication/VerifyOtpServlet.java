package controller.Authentication;

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
        
        // Lấy tham số email và otp từ request
        String email = request.getParameter("email");
        String otpInput = request.getParameter("otp");

        // Lấy session để kiểm tra OTP
        HttpSession session = request.getSession();
        String otpSession = (String) session.getAttribute("otp_" + email);
        Long otpTime = (Long) session.getAttribute("otp_time_" + email);

        // Thiết lập header trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Kiểm tra OTP có tồn tại và khớp không
        if (otpSession == null || otpInput == null || !otpSession.equals(otpInput)) {
            response.getWriter().write("{\"success\":false, \"message\":\"Mã OTP không đúng!\"}");
            return;
        }

        // Kiểm tra thời gian hiệu lực OTP (5 phút)
        if (otpTime == null || (System.currentTimeMillis() - otpTime > 5 * 60 * 1000)) {
            response.getWriter().write("{\"success\":false, \"message\":\"Mã OTP đã hết hạn!\"}");
            return;
        }

        // Xóa OTP và thời gian OTP sau khi xác thực thành công
        session.removeAttribute("otp_" + email);
        session.removeAttribute("otp_time_" + email);

        // Trả về kết quả thành công
        response.getWriter().write("{\"success\":true}");
        request.getRequestDispatcher("/LoginJSP/LoginIndex.jsp").forward(request, response);
    }
}
