package controller.courses;

import Dao.CoursesDAO;
import Dao.UserDAO;
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

@WebServlet(name = "CoursesServlet", urlPatterns = {"/CoursesServlet"})
public class CoursesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userID");

        CoursesDAO dao = new CoursesDAO();
        try {
            List<Course> courses = dao.getAllCourses();
            if (courses == null) {
                throw new RuntimeException("getAllCourses() trả về null!");
            }
            request.setAttribute("courses", courses);

            request.getRequestDispatcher("UserCourse.jsp").forward(request, response);

        } catch (Exception e) {
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
