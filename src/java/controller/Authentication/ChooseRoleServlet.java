package controller.Authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import java.io.IOException;

@WebServlet("/choose-role")
public class ChooseRoleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Kiểm tra xem user đã chọn vai trò chưa
        if (authUser.getRoleID() != 1) { // Nếu không phải student mặc định
            response.sendRedirect(request.getContextPath() + "/HomeServlet");
            return;
        }
        
        request.getRequestDispatcher("/choose-role.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String role = request.getParameter("role");
        
        if ("student".equals(role)) {
            // User chọn làm học sinh, chuyển về trang chủ
            response.sendRedirect(request.getContextPath() + "/HomeServlet");
        } else if ("teacher".equals(role)) {
            // User chọn làm giáo viên, chuyển đến trang upload chứng chỉ
            response.sendRedirect(request.getContextPath() + "/upload-certificate");
        } else {
            // Role không hợp lệ
            request.setAttribute("error", "Vui lòng chọn vai trò hợp lệ");
            request.getRequestDispatcher("/choose-role.jsp").forward(request, response);
        }
    }
} 