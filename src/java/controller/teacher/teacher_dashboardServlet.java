package controller.teacher;

import Dao.CoursesDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Course;
import model.User;

@WebServlet(name = "teacher_dashboard", urlPatterns = {"/teacher_dashboard"})
public class teacher_dashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy thông tin user hiện tại
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("authUser");
            
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            // Kiểm tra quyền giáo viên
            if (currentUser.getRoleID() != 3) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            CoursesDAO dao = new CoursesDAO();
            // Chỉ lấy khóa học do giáo viên này tạo
            List<Course> courses = dao.getCoursesByTeacher(currentUser.getUserID());
            request.setAttribute("courses", courses);
            request.getRequestDispatcher("teacher_dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi lấy dữ liệu khóa học!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
        
        
    }
}
