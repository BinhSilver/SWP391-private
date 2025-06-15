package controller.courses;

import Dao.CoursesDAO;
import model.Course;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "EditCourseServlet", urlPatterns = {"/EditCourseServlet"})
public class EditCourseServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        boolean isHidden = request.getParameter("isHidden") != null;

        Course course = new Course();
        course.setCourseID(courseId);
        course.setTitle(title);
        course.setDescription(description);
        course.setIsHidden(isHidden);

        CoursesDAO dao = new CoursesDAO();
        try {
            dao.update(course);
            response.sendRedirect("CourseServlet");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi cập nhật khóa học: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
}
