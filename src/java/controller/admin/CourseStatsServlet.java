
package controller.admin;

import com.google.gson.Gson;
import Dao.CoursesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/CourseStatsServlet")
public class CourseStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = response.getWriter();
    CoursesDAO coursesDAO = new CoursesDAO();
    String type = request.getParameter("type");
    String period = request.getParameter("period");

    System.out.println("Bắt đầu xử lý yêu cầu CourseStatsServlet: " + request.getQueryString());

    try {
        if ("hidden".equals(type)) {
            // Data cho biểu đồ tròn (hidden vs. visible courses)
            int hidden = coursesDAO.getHiddenCoursesCount();
            int visible = coursesDAO.getTotalCourses() - hidden;
            System.out.println("Hidden courses: " + hidden + ", Visible courses: " + visible);
            out.print(new Gson().toJson(new HiddenCourseStats(visible, hidden)));
        } else if ("chart".equals(type)) {
            // Data cho biểu đồ cột (số khóa học theo tháng hoặc năm)
            List<Map<String, Object>> chartData = new ArrayList<>();
            if ("month".equals(period)) {
                // Lấy dữ liệu 12 tháng gần nhất
                for (int i = 11; i >= 0; i--) {
                    LocalDate date = LocalDate.now().minusMonths(i);
                    int month = date.getMonthValue();
                    int year = date.getYear();
                    int count = coursesDAO.getCoursesByMonthAndYear(month, year);
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", month + "/" + year);
                    item.put("count", count);
                    chartData.add(item);
                }
            } else if ("year".equals(period)) {
                // Lấy dữ liệu 5 năm gần nhất
                for (int i = 4; i >= 0; i--) {
                    int year = LocalDate.now().getYear() - i;
                    int count = coursesDAO.getCoursesByYear(year);
                    Map<String, Object> item = new HashMap<>();
                    item.put("period", String.valueOf(year));
                    item.put("count", count);
                    chartData.add(item);
                }
            }
            out.print(new Gson().toJson(chartData));
        } else {
            // Data cho thẻ thông tin
            LocalDate today = LocalDate.now();
            int currentMonth = today.getMonthValue();
            int currentYear = today.getYear();
            LocalDate previousMonthDate = today.minusMonths(1);
            int previousMonth = previousMonthDate.getMonthValue();
            int previousYear = previousMonthDate.getYear();

            System.out.println("Tháng/năm hiện tại: currentMonth=" + currentMonth + ", currentYear=" + currentYear);
            System.out.println("Tháng/năm trước đó: previousMonth=" + previousMonth + ", previousYear=" + previousYear);

            int totalCourses = coursesDAO.getTotalCourses();
            int currentMonthCourses = coursesDAO.getCoursesByMonthAndYear(currentMonth, currentYear);
            int previousMonthCourses = coursesDAO.getCoursesByMonthAndYear(previousMonth, previousYear);
            double courseGrowthRate = calculateGrowthRate(currentMonthCourses, previousMonthCourses);

            int totalEnrollments = coursesDAO.getTotalEnrollments();
            int currentMonthEnrollments = coursesDAO.getEnrollmentsByMonthAndYear(currentMonth, currentYear);
            int previousMonthEnrollments = coursesDAO.getEnrollmentsByMonthAndYear(previousMonth, previousYear);
            double enrollmentGrowthRate = calculateGrowthRate(currentMonthEnrollments, previousMonthEnrollments);

            String json = String.format(
                    "{ \"totalCourses\": %d, \"courseGrowthRate\": %.1f, \"totalEnrollments\": %d, \"enrollmentGrowthRate\": %.1f }",
                    totalCourses, courseGrowthRate, totalEnrollments, enrollmentGrowthRate
            );
            System.out.println("Trả về JSON: " + json);
            out.print(json);
        }
    } catch (SQLException e) {
        System.out.println("Lỗi SQL: " + e.getMessage());
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print("{\"error\": \"Lỗi truy vấn cơ sở dữ liệu: " + e.getMessage() + "\"}");
    } catch (Exception e) {
        System.out.println("Lỗi khác: " + e.getMessage());
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print("{\"error\": \"Lỗi server: " + e.getMessage() + "\"}");
    } finally {
        out.flush();
    }

    System.out.println("Hoàn tất xử lý yêu cầu CourseStatsServlet");
}

    private double calculateGrowthRate(int current, int previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        double growth = ((double) (current - previous) / previous) * 100;
        return growth <= 0 ? 0.0 : growth;
    }

    private static class HiddenCourseStats {
        int visible;
        int hidden;

        HiddenCourseStats(int visible, int hidden) {
            this.visible = visible;
            this.hidden = hidden;
        }
    }
}
