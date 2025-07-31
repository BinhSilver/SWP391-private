package controller.User;

// ===== IMPORT STATEMENTS =====
import Dao.CoursesDAO;                      // Data Access Object cho Courses
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import model.Course;                        // Course model

import java.io.IOException;                 // IO Exception
import java.util.List;                      // List collection

// ===== SERVLET CONFIGURATION =====
@WebServlet(name = "HomeServlet", urlPatterns = {"/HomeServlet"})  // Map đến URL /HomeServlet
public class HomeServlet extends HttpServlet {

    // ===== GET METHOD - DISPLAY HOME PAGE =====
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== GET SUGGESTED COURSES =====
        // Gọi DAO để lấy danh sách các khóa học được đề xuất
        CoursesDAO dao = new CoursesDAO();
        List<Course> suggestedCourses = dao.getSuggestedCourses();

        // ===== SET ATTRIBUTE FOR JSP =====
        // Gửi danh sách này sang index.jsp để hiển thị
        request.setAttribute("suggestedCourses", suggestedCourses);

        // ===== FORWARD TO HOME PAGE =====
        // Forward đến trang chủ (index.jsp)
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    // ===== POST METHOD - NOT SUPPORTED =====
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tạm thời không xử lý POST ở HomeServlet
        // Trả về lỗi 405 Method Not Allowed
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported.");
    }
}
