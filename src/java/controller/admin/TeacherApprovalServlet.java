package controller.admin;

import Dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.User;

@WebServlet("/teacherApproval")
public class TeacherApprovalServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy danh sách giáo viên chờ xác nhận
        List<User> pendingTeachers = userDAO.getPendingTeachers();
        request.setAttribute("pendingTeachers", pendingTeachers);
        
        request.getRequestDispatcher("/admin/teacher-approval.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int userId = Integer.parseInt(request.getParameter("userId"));
        
        try {
            if ("approve".equals(action)) {
                // Xác nhận giáo viên
                userDAO.approveTeacher(userId);
                request.getSession().setAttribute("success", "Đã xác nhận giáo viên thành công!");
            } else if ("reject".equals(action)) {
                // Từ chối giáo viên - cần thêm phương thức này vào UserDAO
                // userDAO.rejectTeacher(userId);
                request.getSession().setAttribute("success", "Đã từ chối giáo viên!");
            }
            
            response.sendRedirect(request.getContextPath() + "/teacherApproval");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/teacherApproval");
        }
    }
} 