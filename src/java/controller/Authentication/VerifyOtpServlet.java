package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import Dao.UserDAO;                         // Data Access Object cho Users
import model.User;                          // User model
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling
import java.io.IOException;                 // IO Exception

// ===== SERVLET CONFIGURATION =====
/**
 * VerifyOtpServlet - Servlet xử lý xác thực OTP khi đăng ký
 * 
 * Chức năng chính:
 * - Xác thực mã OTP người dùng nhập
 * - Kiểm tra thời gian hết hạn OTP (5 phút)
 * - Tạo tài khoản mới sau khi xác thực thành công
 * - Xóa dữ liệu tạm trong session
 * 
 * URL mapping: /verifyOtp
 */
@WebServlet(name = "VerifyOtpServlet", urlPatterns = {"/verifyOtp"})
public class VerifyOtpServlet extends HttpServlet {

    // ===== POST METHOD - VERIFY OTP =====
    /**
     * Xử lý POST request để xác thực OTP
     * Quy trình:
     * 1. Lấy OTP từ form và session
     * 2. Kiểm tra OTP có đúng không
     * 3. Kiểm tra OTP có hết hạn không
     * 4. Tạo tài khoản mới
     * 5. Xóa dữ liệu tạm
     * 6. Trả về kết quả JSON
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== GET FORM PARAMETERS =====
        // Lấy thông tin từ form xác thực OTP
        String email = request.getParameter("email");
        String otpInput = request.getParameter("otp");

        // ===== SESSION RETRIEVAL =====
        // Lấy OTP và thời gian từ session
        HttpSession session = request.getSession();
        String otpSession = (String) session.getAttribute("otp_" + email);
        Long otpTime = (Long) session.getAttribute("otp_time_" + email);

        // ===== RESPONSE SETUP =====
        // Thiết lập response type cho JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // ===== OTP VALIDATION =====
        // Kiểm tra OTP có đúng không
        if (otpSession == null || otpInput == null || !otpSession.equals(otpInput)) {
            response.getWriter().write("{\"success\":false, \"message\":\"Mã OTP không đúng!\"}");
            return;
        }

        // ===== OTP EXPIRATION CHECK =====
        // Kiểm tra OTP có hết hạn không (5 phút)
        if (otpTime == null || (System.currentTimeMillis() - otpTime > 5 * 60 * 1000)) {
            response.getWriter().write("{\"success\":false, \"message\":\"Mã OTP đã hết hạn!\"}");
            return;
        }

        // ===== SESSION DATA RETRIEVAL =====
        // Lấy thông tin người dùng đã lưu tạm trong session
        String fullName = (String) session.getAttribute("pending_fullName");
        String password = (String) session.getAttribute("pending_password");
        String gender = (String) session.getAttribute("pending_gender");
        String role = (String) session.getAttribute("pending_role");
        Boolean isTeacherPending = (Boolean) session.getAttribute("pending_isTeacherPending");
        String certificatePath = (String) session.getAttribute("pending_certificatePath");

        // ===== USER CREATION =====
        // Tạo tài khoản mới dựa trên role
        if (role != null && "teacher".equals(role)) {
            // ===== TEACHER ACCOUNT CREATION =====
            // Tạo tài khoản giáo viên với thông tin chứng chỉ
            new UserDAO().createNewUser(email, fullName, password, gender, role, isTeacherPending != null && isTeacherPending, certificatePath);
            System.out.println("✅ [VerifyOtp] Đã tạo tài khoản giáo viên: " + email);
        } else {
            // ===== REGULAR ACCOUNT CREATION =====
            // Tạo tài khoản thường
            new UserDAO().createNewUser(email, fullName, password, gender, role, false, null);
            System.out.println("✅ [VerifyOtp] Đã tạo tài khoản thường: " + email);
        }

        // ===== SESSION CLEANUP =====
        // Xóa dữ liệu tạm trong session sau khi tạo tài khoản thành công
        session.removeAttribute("otp_" + email);
        session.removeAttribute("otp_time_" + email);
        session.removeAttribute("pending_email");
        session.removeAttribute("pending_fullName");
        session.removeAttribute("pending_password");
        session.removeAttribute("pending_gender");
        session.removeAttribute("pending_role");
        session.removeAttribute("pending_isTeacherPending");
        session.removeAttribute("pending_certificatePath");
        System.out.println("🧹 [VerifyOtp] Đã xóa dữ liệu tạm trong session cho: " + email);

        // ===== SUCCESS RESPONSE =====
        // Trả về JSON thành công
        response.getWriter().write("{\"success\":true}");
    }
}
