package controller.admin;

import Dao.CoursesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Course;
import java.sql.*;

@WebServlet("/courseManagement")
public class CourseManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CoursesDAO coursesDAO;

    @Override
    public void init() throws ServletException {
        coursesDAO = new CoursesDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy các tham số từ yêu cầu
            String searchTerm = request.getParameter("search") != null ? request.getParameter("search").trim() : "";
            String selectedStatus = request.getParameter("status") != null ? request.getParameter("status") : "All Status";
            String selectedSuggested = request.getParameter("suggested") != null ? request.getParameter("suggested") : "All Suggested";
            int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
            int pageSize = 10; // Số khóa học mỗi trang

            // Lấy danh sách khóa học
            List<Course> courses;
            if (searchTerm.isEmpty() && selectedStatus.equals("All Status") && selectedSuggested.equals("All Suggested")) {
                courses = coursesDAO.getAllCourses();
            } else {
                courses = filterCourses(searchTerm, selectedStatus, selectedSuggested);
            }

            // Tính toán phân trang
            int totalCourses = courses.size();
            int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalCourses);
            List<Course> pagedCourses = courses.subList(start, end);

            // Lấy đánh giá trung bình cho từng khóa học
            Map<Integer, Double> courseRatings = getAverageRatings(pagedCourses);

            // Đặt các thuộc tính để gửi đến JSP
            request.setAttribute("courses", pagedCourses);
            request.setAttribute("courseRatings", courseRatings);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("selectedStatus", selectedStatus);
            request.setAttribute("selectedSuggested", selectedSuggested);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);

            // Chuyển tiếp đến trang JSP
            request.getRequestDispatcher("/admincourse.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi tải danh sách khóa học: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/courseManagement");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int courseId;
        try {
            courseId = Integer.parseInt(request.getParameter("courseId"));
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID khóa học không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/courseManagement");
            return;
        }

        try {
            Course course = coursesDAO.getCourseByID(courseId);
            if (course != null) {
                if ("hide".equals(action)) {
                    course.setHidden(true);
                    coursesDAO.update(course);
                    request.getSession().setAttribute("message", "Khóa học đã được ẩn thành công.");
                } else if ("show".equals(action)) {
                    course.setHidden(false);
                    coursesDAO.update(course);
                    request.getSession().setAttribute("message", "Khóa học đã được hiển thị thành công.");
                } else if ("suggest".equals(action)) {
                    course.setSuggested(true);
                    coursesDAO.update(course);
                    request.getSession().setAttribute("message", "Khóa học đã được gợi ý thành công.");
                } else if ("unsuggest".equals(action)) {
                    course.setSuggested(false);
                    coursesDAO.update(course);
                    request.getSession().setAttribute("message", "Khóa học đã được bỏ gợi ý thành công.");
                } else {
                    request.getSession().setAttribute("error", "Hành động không hợp lệ.");
                }
            } else {
                request.getSession().setAttribute("error", "Không tìm thấy khóa học với ID: " + courseId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi cập nhật trạng thái khóa học: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/courseManagement");
    }

    private List<Course> filterCourses(String searchTerm, String status, String suggested) throws SQLException {
        List<Course> courses = new ArrayList<>();
        if (!searchTerm.isEmpty()) {
            courses = coursesDAO.searchCourse(searchTerm);
        } else {
            courses = coursesDAO.getAllCourses();
        }

        List<Course> filteredCourses = new ArrayList<>();
        for (Course course : courses) {
            boolean statusMatch = status.equals("All Status") ||
                    (status.equals("Visible") && !course.getHidden()) ||
                    (status.equals("Hidden") && course.getHidden());
            boolean suggestedMatch = suggested.equals("All Suggested") ||
                    (suggested.equals("Suggested") && course.isSuggested()) ||
                    (suggested.equals("Not Suggested") && !course.isSuggested());

            if (statusMatch && suggestedMatch) {
                filteredCourses.add(course);
            }
        }
        return filteredCourses;
    }

    private Map<Integer, Double> getAverageRatings(List<Course> courses) throws SQLException {
        Map<Integer, Double> ratings = new HashMap<>();
                String sql = "SELECT CourseID, AVG(CAST(Rating AS FLOAT)) as AvgRating " +
                    "FROM [dbo].[Feedbacks] WHERE CourseID = ? GROUP BY CourseID";

        try (Connection conn = DB.JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Course course : courses) {
                stmt.setInt(1, course.getCourseID());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    ratings.put(course.getCourseID(), Math.round(rs.getDouble("AvgRating") * 10.0) / 10.0);
                }
            }
        }
        return ratings;
    }
}