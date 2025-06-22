package controller.courses;

import Dao.CoursesDAO;
import model.Course;
import model.User;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@MultipartConfig // BẮT BUỘC nếu form dùng enctype="multipart/form-data"
@WebServlet(name = "CreateCourseServlet", urlPatterns = {"/CreateCourseServlet"})
public class CreateCourseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển đến trang form tạo khóa học
        request.getRequestDispatcher("create_course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Thiết lập mã hóa UTF-8 để đọc tiếng Việt
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. Lấy dữ liệu từ form
        String title = request.getParameter("courseTitle");
        String description = request.getParameter("courseDescription");
        boolean isHidden = request.getParameter("isHidden") != null;

        // 2. Lấy người dùng hiện tại từ session
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("authUser") : null;

        // 3. Kiểm tra nếu là admin thì mới được đánh dấu là khóa học nổi bật
        boolean isSuggested = false;
        if (user != null && user.getRoleID() == 4 && request.getParameter("isSuggested") != null) {
            isSuggested = true;
        }

        // 4. Tạo đối tượng Course và gán dữ liệu
        Course newCourse = new Course();
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setHidden(isHidden);
        newCourse.setSuggested(isSuggested);

        // 5. Gọi DAO để lưu vào DB
        CoursesDAO dao = new CoursesDAO();
        try {
            int newCourseId = dao.addAndReturnID(newCourse);
            // 6. Chuyển hướng sang trang chi tiết khóa học
            response.sendRedirect("CourseDetailServlet?id=" + newCourseId);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi thêm khóa học: " + e.getMessage());
            request.getRequestDispatcher("create_course.jsp").forward(request, response);
        }
    }
}
