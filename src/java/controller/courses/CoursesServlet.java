package controller.courses;

import Dao.CoursesDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Course;
import model.User;

@WebServlet(name = "CoursesServlet", urlPatterns = {"/CoursesServlet"})
public class CoursesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CoursesDAO dao = new CoursesDAO();
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("authUser") : null;
        List<Course> courses = null;
        try {
            if (currentUser == null) {
                // Chưa đăng nhập: chỉ xem khóa học không ẩn
                courses = dao.getAllCourses();
                courses.removeIf(c -> c.getHidden());
                System.out.println("[CoursesServlet] Guest: chỉ hiển thị khóa học không ẩn, tổng: " + courses.size());
            } else if (currentUser.getRoleID() == 3) {
                // Giáo viên: chỉ xem khóa học của mình
                courses = dao.getCoursesByTeacher(currentUser.getUserID());
                System.out.println("[CoursesServlet] Giáo viên " + currentUser.getUserID() + ": có " + courses.size() + " khóa học.");
            } else if (currentUser.getRoleID() == 4) {
                // Admin: xem tất cả
                courses = dao.getAllCourses();
                System.out.println("[CoursesServlet] Admin: xem tất cả khóa học, tổng: " + courses.size());
            } else {
                // User thường: chỉ xem khóa học không ẩn
                courses = dao.getAllCourses();
                courses.removeIf(c -> c.getHidden());
                System.out.println("[CoursesServlet] User thường: chỉ hiển thị khóa học không ẩn, tổng: " + courses.size());
            }
            request.setAttribute("courses", courses);
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("Course.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn dữ liệu: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        boolean isHidden = request.getParameter("isHidden") != null;
        boolean isSuggested = request.getParameter("isSuggested") != null; // ✅ Thêm dòng này

        Course newCourse = new Course();
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setHidden(isHidden);
        newCourse.setSuggested(isSuggested); // ✅ Áp dụng vào model

        CoursesDAO dao = new CoursesDAO();
        try {
            dao.add(newCourse);
            response.sendRedirect("CoursesServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi thêm khóa học: " + e.getMessage());
            doGet(request, response);
        }
    }
}
