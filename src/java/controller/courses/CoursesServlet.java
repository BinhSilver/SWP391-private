package controller.courses;

// ===== IMPORT STATEMENTS =====
import Dao.CoursesDAO;                      // Data Access Object cho Courses
import java.io.File;                        // File operations
import java.io.FileOutputStream;            // File output stream
import java.io.IOException;                 // IO Exception
import java.io.InputStream;                 // Input stream
import java.sql.SQLException;               // SQL Exception
import java.util.List;                      // List collection
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.MultipartConfig;  // Annotation cho file upload
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling
import jakarta.servlet.http.Part;                  // File upload part
import model.Course;                        // Course model
import model.User;                          // User model

// ===== SERVLET CONFIGURATION =====
@WebServlet(name = "CoursesServlet", urlPatterns = {"/CoursesServlet"})  // Map đến URL /CoursesServlet
@MultipartConfig(maxFileSize = 50 * 1024 * 1024)  // Tối đa 50MB cho file upload
public class CoursesServlet extends HttpServlet {

    // ===== CONSTANTS =====
    private static final String UPLOAD_DIR = "files";  // Thư mục upload files

    // ===== GET METHOD - DISPLAY COURSES =====
    /**
     * Xử lý GET request - Hiển thị danh sách khóa học
     * Phân quyền theo role của user:
     * - Guest: Chỉ xem khóa học không ẩn
     * - Teacher: Xem khóa học của mình
     * - Admin: Xem tất cả khóa học
     * - User thường: Chỉ xem khóa học không ẩn
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== SESSION HANDLING =====
        // Lấy session và user hiện tại
        HttpSession session = request.getSession();
        User currentUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        // ===== DATABASE OPERATIONS =====
        // Khởi tạo DAO và danh sách khóa học
        CoursesDAO dao = new CoursesDAO();
        List<Course> courses = null;
        
        try {
            // ===== ROLE-BASED COURSE FILTERING =====
            if (currentUser == null) {
                // ===== GUEST USER =====
                // Chưa đăng nhập → chỉ xem khóa học không ẩn
                courses = dao.getAllCourses();
                courses.removeIf(c -> c.getHidden());  // Lọc bỏ khóa học ẩn
                System.out.println("[CoursesServlet] Guest: chỉ hiển thị khóa học không ẩn, tổng: " + courses.size());
                
            } else if (currentUser.getRoleID() == 3) {
                // ===== TEACHER USER =====
                // Giáo viên → xem khóa học của mình
                courses = dao.getCoursesByTeacher(currentUser.getUserID());
                System.out.println("[CoursesServlet] Giáo viên " + currentUser.getUserID() + ": có " + courses.size() + " khóa học.");
                
            } else if (currentUser.getRoleID() == 4) {
                // ===== ADMIN USER =====
                // Admin → xem tất cả khóa học
                courses = dao.getAllCourses();
                System.out.println("[CoursesServlet] Admin: xem tất cả khóa học, tổng: " + courses.size());
                
            } else {
                // ===== REGULAR USER =====
                // User thường → chỉ xem khóa học không ẩn
                courses = dao.getAllCourses();
                courses.removeIf(c -> c.getHidden());  // Lọc bỏ khóa học ẩn
                System.out.println("[CoursesServlet] User thường: chỉ hiển thị khóa học không ẩn, tổng: " + courses.size());
            }

            // ===== SET ATTRIBUTES FOR JSP =====
            // Gửi dữ liệu đến JSP để hiển thị
            request.setAttribute("courses", courses);
            request.setAttribute("currentUser", currentUser);
            
            // ===== FORWARD TO COURSE PAGE =====
            // Chuyển hướng đến trang Course.jsp
            request.getRequestDispatcher("Course.jsp").forward(request, response);

        } catch (SQLException e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi database
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn dữ liệu: " + e.getMessage());
        }
    }

    // ===== POST METHOD - CREATE COURSE =====
    /**
     * Xử lý POST request - Tạo khóa học mới
     * Chỉ Admin và Teacher có quyền tạo khóa học
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== CHARACTER ENCODING =====
        // Đặt encoding UTF-8 cho request
        request.setCharacterEncoding("UTF-8");
        
        // ===== SESSION HANDLING =====
        // Lấy session và user hiện tại
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        // ===== GET FORM PARAMETERS =====
        // Lấy các tham số từ form
        String title = request.getParameter("title");                    // Tiêu đề khóa học
        String description = request.getParameter("description");        // Mô tả khóa học
        boolean isHidden = request.getParameter("isHidden") != null;    // Có ẩn khóa học không
        boolean isSuggested = request.getParameter("isSuggested") != null;  // Có đề xuất không

        // ===== DEFAULT IMAGE =====
        // Đặt ảnh mặc định
        String imageUrl = "files/default.png";

        // ===== FILE UPLOAD HANDLING =====
        // Xử lý upload file thumbnail (nếu có)
        Part imagePart = request.getPart("thumbnailFile");
        if (imagePart != null && imagePart.getSize() > 0) {
            // ===== GET FILE NAME =====
            String fileName = getFileName(imagePart);
            
            // ===== SETUP UPLOAD DIRECTORY =====
            String appPath = request.getServletContext().getRealPath("");
            String uploadPath = appPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();  // Tạo thư mục nếu chưa tồn tại
            }
            
            // ===== SAVE FILE =====
            try (InputStream is = imagePart.getInputStream();
                 FileOutputStream os = new FileOutputStream(uploadPath + File.separator + fileName)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            imageUrl = UPLOAD_DIR + "/" + fileName;
        }

        Course newCourse = new Course();
        newCourse.setTitle(title);
        newCourse.setDescription(description);
        newCourse.setHidden(isHidden);
        newCourse.setSuggested(isSuggested);
        newCourse.setImageUrl(imageUrl);

        if (currentUser != null) {
            newCourse.setCreatedBy(currentUser.getUserID());
        } else {
            newCourse.setCreatedBy(0); // hoặc giá trị khác nếu muốn đánh dấu guest
        }

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

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String s : contentDisp.split(";")) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
}
