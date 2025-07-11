package controller.User;

import Dao.CoursesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Course;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HomeServlet", urlPatterns = {"/HomeServlet"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Gọi DAO để lấy danh sách các khóa học được đề xuất
        CoursesDAO dao = new CoursesDAO();
        List<Course> suggestedCourses = dao.getSuggestedCourses();

        // Gửi danh sách này sang index.jsp để hiển thị
        request.setAttribute("suggestedCourses", suggestedCourses);

        // Forward đến trang chủ
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tạm thời không xử lý POST ở HomeServlet
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method not supported.");
    }
}
