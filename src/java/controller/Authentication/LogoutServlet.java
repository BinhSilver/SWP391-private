package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import java.io.IOException;                 // IO Exception
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling

// ===== SERVLET CONFIGURATION =====
/**
 * LogoutServlet - Servlet xử lý đăng xuất user
 * 
 * Chức năng chính:
 * - Hủy session hiện tại
 * - Xóa thông tin user khỏi session
 * - Redirect về trang chủ
 * 
 * URL mapping: /logout
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    // ===== GET METHOD - HANDLE LOGOUT =====
    /**
     * Xử lý GET request để đăng xuất user
     * Quy trình:
     * 1. Lấy session hiện tại
     * 2. Hủy session để đăng xuất
     * 3. Redirect về trang chủ
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== SESSION INVALIDATION =====
        // Lấy session hiện tại nếu có và hủy nó
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Hủy session để đăng xuất
            System.out.println("🚪 [Logout] User đã đăng xuất thành công");
        }

        // ===== REDIRECT TO HOME =====
        // Chuyển hướng về trang chủ sau khi đăng xuất
        response.sendRedirect(request.getContextPath() + "/HomeServlet");
    }
}
