package controller.teacher;

import Dao.CoursesDAO;
import Dao.UserDAO;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Course;
import model.User;

@WebServlet(name = "teacher_dashboard", urlPatterns = {"/teacher_dashboard"})
public class teacher_dashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(teacher_dashboardServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            User teacher = (User) session.getAttribute("authUser");
            
            // Check if user is logged in and is a teacher
            if (teacher == null) {
                LOGGER.warning("Unauthorized access attempt to teacher dashboard - user not logged in");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            if (teacher.getRoleID() != 3) {
                LOGGER.warning("Unauthorized access attempt to teacher dashboard by user: " + teacher.getUserID());
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }

            // Get courses for this teacher only
            CoursesDAO dao = new CoursesDAO();
            List<Course> courses = dao.getCoursesByTeacher(teacher.getUserID());
            
            request.setAttribute("teacher", teacher);
            request.setAttribute("courses", courses);

            LOGGER.info("Teacher " + teacher.getUserID() + " accessed dashboard, found " + courses.size() + " courses");
            
            request.getRequestDispatcher("teacher_dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in teacher dashboard", e);
            response.sendError(500, "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}