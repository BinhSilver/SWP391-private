package controller.teacher;

import Dao.CoursesDAO;
import Dao.UserDAO;
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
            // Lấy giáo viên đang đăng nhập từ session (bạn cần chắc chắn login xong đã set user vào session)
            User teacher = (User) request.getSession().getAttribute("user");
            
            // Nếu chưa có teacher trên session (hiếm khi xảy ra), lấy bằng email (ví dụ)
            if (teacher == null) {
                String email = (String) request.getSession().getAttribute("email");
                if (email != null) {
                    teacher = new UserDAO().getUserByEmail(email);
                }
            }
            // Truyền sang JSP để hiển thị
            request.setAttribute("teacher", teacher);

            // Lấy danh sách khóa học (ở đây là toàn bộ, nếu muốn chỉ lấy của giáo viên này thì sửa DAO)
            CoursesDAO dao = new CoursesDAO();
            List<Course> courses = dao.getAllCourses();
            request.setAttribute("courses", courses);

            request.getRequestDispatcher("teacher_dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi lấy dữ liệu khóa học hoặc giáo viên!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}