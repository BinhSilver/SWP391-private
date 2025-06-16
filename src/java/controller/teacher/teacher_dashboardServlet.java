package controller.teacher;

import Dao.CoursesDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Course;

@WebServlet(name = "teacher_dashboard", urlPatterns = {"/teacher_dashboard"})
public class teacher_dashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            CoursesDAO dao = new CoursesDAO();
            List<Course> courses = dao.getAllCourses(); // Lấy từ DB (phần chuẩn để hiển thị)
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
