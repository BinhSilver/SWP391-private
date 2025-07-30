package controller.courses;

import Dao.CoursesDAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Course;
import model.User;

@WebServlet(name = "CoursesServlet", urlPatterns = {"/CoursesServlet"})
@MultipartConfig(maxFileSize = 50 * 1024 * 1024) // Tối đa 50MB
public class CoursesServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "files";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        CoursesDAO dao = new CoursesDAO();
        List<Course> courses = null;
        try {
            if (currentUser == null) {
                // Chưa đăng nhập → chỉ xem khóa học không ẩn
                courses = dao.getAllCourses();
                courses.removeIf(c -> c.getHidden());
                System.out.println("[CoursesServlet] Guest: chỉ hiển thị khóa học không ẩn, tổng: " + courses.size());
            } else if (currentUser.getRoleID() == 3) {
                // Giáo viên → xem khóa học của mình
                courses = dao.getCoursesByTeacher(currentUser.getUserID());
                System.out.println("[CoursesServlet] Giáo viên " + currentUser.getUserID() + ": có " + courses.size() + " khóa học.");
            } else if (currentUser.getRoleID() == 4) {
                // Admin → xem tất cả
                courses = dao.getAllCourses();
                System.out.println("[CoursesServlet] Admin: xem tất cả khóa học, tổng: " + courses.size());
            } else {
                // User thường → chỉ xem khóa học không ẩn
                courses = dao.getAllCourses();
                courses.removeIf(c -> c.getHidden());
                System.out.println("[CoursesServlet] User thường: chỉ hiển thị khóa học không ẩn, tổng: " + courses.size());
            }

            request.setAttribute("courses", courses);
            request.setAttribute("currentUser", currentUser);
            request.getRequestDispatcher("Course.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn dữ liệu: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("authUser") : null;

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        boolean isHidden = request.getParameter("isHidden") != null;
        boolean isSuggested = request.getParameter("isSuggested") != null;

        String imageUrl = "files/default.png";

        // Xử lý upload file (nếu có)
        Part imagePart = request.getPart("thumbnailFile");
        if (imagePart != null && imagePart.getSize() > 0) {
            String fileName = getFileName(imagePart);
            String appPath = request.getServletContext().getRealPath("");
            String uploadPath = appPath + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
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
