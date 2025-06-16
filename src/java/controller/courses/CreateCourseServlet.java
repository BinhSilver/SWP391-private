package controller.courses;

import Dao.CoursesDAO;
import model.Course;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "CreateCourseServlet", urlPatterns = {"/CreateCourseServlet"})
public class CreateCourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("create_course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String title = request.getParameter("courseTitle");
        String description = request.getParameter("courseDescription");
        boolean isHidden = request.getParameter("isHidden") != null;

        // Lấy người dùng hiện tại từ session
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("authUser") : null;

        // Mặc định không phải khóa học nổi bật
        boolean isSuggested = false;

        // Chỉ người có roleID == 4 (admin) mới được đánh dấu nổi bật
        if (user != null && user.getRoleID() == 4 && request.getParameter("isSuggested") != null) {
            isSuggested = true;
        }

        Course newCourse = new Course();
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setHidden(isHidden);
        newCourse.setSuggested(isSuggested);

        CoursesDAO dao = new CoursesDAO();
        try {
            int newCourseId = dao.addAndReturnID(newCourse);
            response.sendRedirect("CourseDetailServlet?id=" + newCourseId);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi thêm khóa học: " + e.getMessage());
            request.getRequestDispatcher("create_course.jsp").forward(request, response);
        }
    }
}
