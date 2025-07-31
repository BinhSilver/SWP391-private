package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling
import model.User;                          // User model
import java.io.IOException;                 // IO Exception

// ===== SERVLET CONFIGURATION =====
/**
 * ChooseRoleServlet - Servlet xử lý chọn vai trò cho user mới
 * 
 * Chức năng chính:
 * - Hiển thị trang chọn vai trò cho user mới đăng ký
 * - Xử lý việc chọn vai trò (student/teacher)
 * - Redirect đến trang phù hợp dựa trên vai trò
 * 
 * URL mapping: /choose-role
 */
@WebServlet("/choose-role")
public class ChooseRoleServlet extends HttpServlet {

    // ===== GET METHOD - DISPLAY ROLE CHOICE PAGE =====
    /**
     * Xử lý GET request để hiển thị trang chọn vai trò
     * Quy trình:
     * 1. Kiểm tra user đã đăng nhập chưa
     * 2. Kiểm tra user có cần chọn vai trò không
     * 3. Hiển thị trang chọn vai trò
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== SESSION VALIDATION =====
        // Lấy thông tin user từ session
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // ===== LOGIN CHECK =====
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // ===== ROLE CHECK =====
        // Kiểm tra xem user đã chọn vai trò chưa
        if (authUser.getRoleID() != 1) { // Nếu không phải student mặc định
            response.sendRedirect(request.getContextPath() + "/HomeServlet");
            return;
        }
        
        // ===== FORWARD TO ROLE CHOICE PAGE =====
        // Hiển thị trang chọn vai trò
        request.getRequestDispatcher("/choose-role.jsp").forward(request, response);
    }

    // ===== POST METHOD - HANDLE ROLE SELECTION =====
    /**
     * Xử lý POST request để xử lý việc chọn vai trò
     * Quy trình:
     * 1. Kiểm tra user đã đăng nhập chưa
     * 2. Lấy vai trò được chọn
     * 3. Redirect đến trang phù hợp
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== SESSION VALIDATION =====
        // Lấy thông tin user từ session
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // ===== LOGIN CHECK =====
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // ===== GET SELECTED ROLE =====
        // Lấy vai trò được chọn từ form
        String role = request.getParameter("role");
        
        // ===== ROLE PROCESSING =====
        // Xử lý dựa trên vai trò được chọn
        if ("student".equals(role)) {
            // ===== STUDENT ROLE =====
            // User chọn làm học sinh, chuyển về trang chủ
            System.out.println("👨‍🎓 [ChooseRole] User " + authUser.getEmail() + " chọn vai trò student");
            response.sendRedirect(request.getContextPath() + "/HomeServlet");
        } else if ("teacher".equals(role)) {
            // ===== TEACHER ROLE =====
            // User chọn làm giáo viên, chuyển đến trang upload chứng chỉ
            System.out.println("👨‍🏫 [ChooseRole] User " + authUser.getEmail() + " chọn vai trò teacher");
            response.sendRedirect(request.getContextPath() + "/upload-certificate");
        } else {
            // ===== INVALID ROLE =====
            // Role không hợp lệ
            System.out.println("❌ [ChooseRole] User " + authUser.getEmail() + " chọn vai trò không hợp lệ: " + role);
            request.setAttribute("error", "Vui lòng chọn vai trò hợp lệ");
            request.getRequestDispatcher("/choose-role.jsp").forward(request, response);
        }
    }
} 