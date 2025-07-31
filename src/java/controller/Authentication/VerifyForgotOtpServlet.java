package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.*;              // HTTP Servlet classes
import java.io.IOException;                 // IO Exception
import java.io.PrintWriter;                 // PrintWriter for response

// ===== SERVLET CONFIGURATION =====
/**
 * VerifyForgotOtpServlet - Servlet xác thực OTP cho quên mật khẩu
 * 
 * Chức năng chính:
 * - Xác thực mã OTP người dùng nhập
 * - Kiểm tra thời gian hết hạn OTP (5 phút)
 * - Xóa OTP khỏi session sau khi xác thực thành công
 * - Trả về kết quả JSON
 * 
 * URL mapping: /verify-forgot-otp
 */
@WebServlet("/verify-forgot-otp")
public class VerifyForgotOtpServlet extends HttpServlet {
    
    // ===== POST METHOD - VERIFY FORGOT OTP =====
    /**
     * Xử lý POST request để xác thực OTP quên mật khẩu
     * Quy trình:
     * 1. Lấy OTP từ form và session
     * 2. Kiểm tra OTP có đúng không
     * 3. Kiểm tra OTP có hết hạn không
     * 4. Xóa OTP khỏi session
     * 5. Trả về kết quả JSON
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== GET FORM PARAMETERS =====
        // Lấy thông tin từ form xác thực OTP
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");

        // ===== SESSION RETRIEVAL =====
        // Lấy OTP và thời gian từ session
        HttpSession session = request.getSession();
        String sessionKey = "otp_" + email;
        String sessionOtp = (String) session.getAttribute("otp_" + email);
        Long otpTime = (Long) session.getAttribute("otp_time_" + email);

        // ===== RESPONSE SETUP =====
        // Thiết lập response type cho JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // ===== OTP VALIDATION =====
        // Kiểm tra OTP hợp lệ và không quá 5 phút
        if (sessionOtp != null && sessionOtp.equals(otp)) {
            long now = System.currentTimeMillis();
            if (otpTime != null && now - otpTime <= 5 * 60 * 1000) {
                // ===== SUCCESSFUL VERIFICATION =====
                // Xác thực thành công - xóa OTP khỏi session
                session.removeAttribute(sessionKey);
                session.removeAttribute("otp_time_" + email);
                System.out.println("✅ [VerifyForgotOtp] Xác thực OTP thành công cho email: " + email);

                out.write("{\"success\": true, \"message\": \"Xác thực OTP thành công.\"}");
            } else {
                // ===== EXPIRED OTP =====
                // OTP đã hết hạn
                System.out.println("⏰ [VerifyForgotOtp] OTP đã hết hạn cho email: " + email);
                out.write("{\"success\": false, \"message\": \"Mã OTP đã hết hạn.\"}");
            }
        } else {
            // ===== INVALID OTP =====
            // OTP không chính xác
            System.out.println("❌ [VerifyForgotOtp] OTP không chính xác cho email: " + email);
            out.write("{\"success\": false, \"message\": \"Mã OTP không chính xác.\"}");
        }
    }
}
