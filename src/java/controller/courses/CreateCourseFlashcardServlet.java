package controller.courses;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.Course;
import model.User;
import service.CourseFlashcardService;
import Dao.CoursesDAO;

/**
 * Servlet để xử lý việc tạo flashcard từ từ vựng của khóa học
 */
@WebServlet(name = "CreateCourseFlashcardServlet", urlPatterns = {"/create-course-flashcard"})
public class CreateCourseFlashcardServlet extends HttpServlet {
    
    private CourseFlashcardService flashcardService;
    private CoursesDAO coursesDAO;
    
    @Override
    public void init() throws ServletException {
        flashcardService = new CourseFlashcardService();
        coursesDAO = new CoursesDAO();
    }
    
    /**
     * Xử lý yêu cầu GET - hiển thị trang tạo flashcard
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Kiểm tra quyền - chỉ giáo viên và admin mới có quyền tạo flashcard từ khóa học
        if (authUser.getRoleID() != 3 && authUser.getRoleID() != 4) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này");
            return;
        }
        
        // Lấy ID khóa học từ request
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số courseId");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            Course course = coursesDAO.getCourseByID(courseId);
            
            if (course == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy khóa học");
                return;
            }
            
            // Kiểm tra quyền - chỉ người tạo khóa học hoặc admin mới có quyền tạo flashcard
            if (authUser.getRoleID() != 4 && course.getCreatedBy() != authUser.getUserID()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này");
                return;
            }
            
            // Kiểm tra xem khóa học đã có flashcard chưa
            boolean hasFlashcard = flashcardService.courseHasFlashcard(courseId);
            
            // Truyền dữ liệu vào request
            request.setAttribute("course", course);
            request.setAttribute("hasFlashcard", hasFlashcard);
            
            if (hasFlashcard) {
                int flashcardId = flashcardService.getCourseFlashcardID(courseId);
                request.setAttribute("flashcardId", flashcardId);
            }
            
            // Chuyển hướng đến trang xác nhận tạo flashcard
            request.getRequestDispatcher("/create-course-flashcard.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID khóa học không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý yêu cầu POST - tạo flashcard từ khóa học
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Kiểm tra quyền - chỉ giáo viên và admin mới có quyền tạo flashcard từ khóa học
        if (authUser.getRoleID() != 3 && authUser.getRoleID() != 4) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này");
            return;
        }
        
        // Lấy ID khóa học từ request
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam == null || courseIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số courseId");
            return;
        }
        
        try {
            int courseId = Integer.parseInt(courseIdParam);
            Course course = coursesDAO.getCourseByID(courseId);
            
            if (course == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy khóa học");
                return;
            }
            
            // Kiểm tra quyền - chỉ người tạo khóa học hoặc admin mới có quyền tạo flashcard
            if (authUser.getRoleID() != 4 && course.getCreatedBy() != authUser.getUserID()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện hành động này");
                return;
            }
            
            // Tạo flashcard từ khóa học
            int flashcardId = flashcardService.createFlashcardFromCourse(courseId, authUser.getUserID());
            
            if (flashcardId > 0) {
                // Chuyển hướng đến trang xem flashcard
                response.sendRedirect("view-flashcard?id=" + flashcardId);
            } else {
                // Nếu có lỗi, quay lại trang trước đó với thông báo lỗi
                request.setAttribute("error", "Không thể tạo flashcard từ khóa học này. Vui lòng thử lại sau.");
                request.setAttribute("course", course);
                request.getRequestDispatcher("/create-course-flashcard.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID khóa học không hợp lệ");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/create-course-flashcard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Có lỗi xảy ra: " + e.getMessage());
        }
    }
} 