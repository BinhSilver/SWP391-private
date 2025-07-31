package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import jakarta.servlet.*;                   // Servlet classes
import jakarta.servlet.http.*;              // HTTP Servlet classes
import jakarta.servlet.annotation.*;        // Servlet annotations
import java.io.IOException;                 // IO Exception
import Dao.UserDAO;                         // Data Access Object cho Users

// ===== SERVLET CONFIGURATION =====
/**
 * ResetPasswordServlet - Servlet xử lý đặt lại mật khẩu
 * 
 * Chức năng chính:
 * - Nhận email và mật khẩu mới từ form
 * - Validate input
 * - Cập nhật mật khẩu trong database
 * - Trả về kết quả JSON
 * 
 * URL mapping: /reset-password
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    
    // ===== POST METHOD - RESET PASSWORD =====
    /**
     * Xử lý POST request để đặt lại mật khẩu
     * Quy trình:
     * 1. Nhận email và mật khẩu mới
     * 2. Validate input
     * 3. Cập nhật mật khẩu trong database
     * 4. Trả về kết quả
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // ===== CHARACTER ENCODING SETUP =====
        // Thiết lập encoding cho request và response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // ===== GET FORM PARAMETERS =====
        // Lấy thông tin từ form đặt lại mật khẩu
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");

        // ===== INPUT VALIDATION =====
        // Kiểm tra hợp lệ đơn giản
        if (email == null || email.isEmpty() || newPassword == null || newPassword.length() < 6) {
            response.getWriter().write("error");
            return;
        }

        // ===== DATABASE OPERATION =====
        // Gọi DAO để cập nhật mật khẩu
        boolean success = UserDAO.updatePassword(email, newPassword);

        // ===== RESPONSE HANDLING =====
        // Trả về kết quả dựa trên thành công hay thất bại
        if (success) {
            response.getWriter().write("ok");
            System.out.println("✅ [ResetPassword] Đã đặt lại mật khẩu cho email: " + email);
        } else {
            response.getWriter().write("error");
            System.out.println("❌ [ResetPassword] Lỗi đặt lại mật khẩu cho email: " + email);
        }
    }
}
