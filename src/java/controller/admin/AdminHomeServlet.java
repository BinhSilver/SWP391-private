package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import java.io.IOException;

@WebServlet("/adminHome")
public class AdminHomeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // Kiểm tra xem người dùng đã đăng nhập và có phải admin không
        if (authUser == null || !authUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Forward to admin dashboard page
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }
} 