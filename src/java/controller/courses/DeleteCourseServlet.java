package controller.courses;

import Dao.CoursesDAO;
import model.Course;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "DeleteCourseServlet", urlPatterns = {"/DeleteCourseServlet"})
public class DeleteCourseServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User currentUser = (session != null) ? (User) session.getAttribute("authUser") : null;
        
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để thực hiện thao tác này");
            return;
        }
        
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số courseId");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            
            // Kiểm tra quyền xóa khóa học
            CoursesDAO coursesDAO = new CoursesDAO();
            Course course = coursesDAO.getCourseByID(courseId);
            
            if (course == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Khóa học không tồn tại");
                return;
            }
            
            // Chỉ cho phép giáo viên sở hữu khóa học hoặc admin xóa
            if (currentUser.getRoleID() != 4 && course.getCreatedBy() != currentUser.getUserID()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xóa khóa học này");
                return;
            }
            
            // Thực hiện xóa khóa học
            coursesDAO.delete(courseId);
            
            // Trả về response thành công
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": true, \"message\": \"Khóa học đã được xóa thành công\"}");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID khóa học không hợp lệ");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Lỗi khi xóa khóa học: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Lỗi không xác định: " + e.getMessage());
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Method GET không được hỗ trợ");
    }
}
