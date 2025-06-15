package controller.chart;

import Dao.CoursesDAO;
import Dao.UserDAO;
import Dao.UserPremiumDAO;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@WebServlet("/UserStatsServlet")
public class UserStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Khởi tạo biến
        int totalUsers = 0;
        int currentMonthUsers = 0;
        int previousMonthUsers = 0;
        double userGrowthRate = 0.0;
        int currentMonthPremium = 0;
        int previousMonthPremium = 0;
        double premiumGrowthRate = 0.0;
        int totalCourses = 0;
        int currentMonthCourses = 0;
        int previousMonthCourses = 0;
        double courseGrowthRate = 0.0;

        // Log bắt đầu xử lý yêu cầu
        System.out.println("Bắt đầu xử lý yêu cầu UserStatsServlet: " + request.getQueryString());

        // Luôn sử dụng tháng và năm hiện tại
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        System.out.println("Tháng/năm hiện tại: currentMonth=" + currentMonth + ", currentYear=" + currentYear);

        // Tính tháng và năm trước đó
        LocalDate currentDate = LocalDate.of(currentYear, currentMonth, 1);
        LocalDate previousMonthDate = currentDate.minusMonths(1);
        int previousMonth = previousMonthDate.getMonthValue();
        int previousYear = previousMonthDate.getYear();
        System.out.println("Tháng/năm trước đó: previousMonth=" + previousMonth + ", previousYear=" + previousYear);

        UserPremiumDAO userp = new UserPremiumDAO();
        UserDAO user = new UserDAO();
        try {
            // Lấy dữ liệu người dùng
            totalUsers = user.getTotalUsers();
            System.out.println("Tổng số người dùng: totalUsers=" + totalUsers);
            currentMonthUsers = user.getUsersByMonthAndYear(currentMonth, currentYear);
            System.out.println("Người dùng tháng hiện tại: currentMonthUsers=" + currentMonthUsers);
            previousMonthUsers = user.getUsersByMonthAndYear(previousMonth, previousYear);
            System.out.println("Người dùng tháng trước: previousMonthUsers=" + previousMonthUsers);

            // Tính tỷ lệ tăng trưởng người dùng theo công thức mới
            if (previousMonthUsers > 0) {
                userGrowthRate = ((double) (currentMonthUsers - previousMonthUsers) / previousMonthUsers) * 100;
            } else if (currentMonthUsers > 0) {
                userGrowthRate = 100.0; // 100% nếu tháng trước = 0 và tháng hiện tại > 0
            } else {
                userGrowthRate = 0.0; // 0% nếu cả hai tháng đều = 0
            }
            System.out.println("Tỷ lệ tăng trưởng người dùng: userGrowthRate=" + userGrowthRate);

            // Lấy dữ liệu người dùng Premium
            currentMonthPremium = userp.getPremiumUsersByMonthAndYear(currentMonth, currentYear);
            System.out.println("Người dùng Premium tháng hiện tại: currentMonthPremium=" + currentMonthPremium);
            previousMonthPremium = userp.getPremiumUsersByMonthAndYear(previousMonth, previousYear);
            System.out.println("Người dùng Premium tháng trước: previousMonthPremium=" + previousMonthPremium);

            // Tính tỷ lệ tăng trưởng Premium theo công thức mới
            if (previousMonthPremium > 0) {
                premiumGrowthRate = ((double) (currentMonthPremium - previousMonthPremium) / previousMonthPremium) * 100;
            } else if (currentMonthPremium > 0) {
                premiumGrowthRate = 100.0; // 100% nếu tháng trước = 0 và tháng hiện tại > 0
            } else {
                premiumGrowthRate = 0.0; // 0% nếu cả hai tháng đều = 0
            }
            System.out.println("Tỷ lệ tăng trưởng Premium: premiumGrowthRate=" + premiumGrowthRate);

            // Lấy dữ liệu khóa học
            CoursesDAO coursesDAO = new CoursesDAO();
            totalCourses = coursesDAO.getTotalCourses();
            System.out.println("Tổng số khóa học: totalCourses=" + totalCourses);
            currentMonthCourses = coursesDAO.getCoursesByMonthAndYear(currentMonth, currentYear);
            System.out.println("Khóa học tháng hiện tại: currentMonthCourses=" + currentMonthCourses);
            previousMonthCourses = coursesDAO.getCoursesByMonthAndYear(previousMonth, previousYear);
            System.out.println("Khóa học tháng trước: previousMonthCourses=" + previousMonthCourses);

            // Tính tỷ lệ tăng trưởng khóa học theo công thức mới
            if (previousMonthCourses > 0) {
                courseGrowthRate = ((double) (currentMonthCourses - previousMonthCourses) / previousMonthCourses) * 100;
            } else if (currentMonthCourses > 0) {
                courseGrowthRate = 100.0; // 100% nếu tháng trước = 0 và tháng hiện tại > 0
            } else {
                courseGrowthRate = 0.0; // 0% nếu cả hai tháng đều = 0
            }
            System.out.println("Tỷ lệ tăng trưởng khóa học: courseGrowthRate=" + courseGrowthRate);

        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Lỗi truy vấn cơ sở dữ liệu: " + e.getMessage() + "\"}");
            return;
        } catch (Exception e) {
            System.out.println("Lỗi khác: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Lỗi server: " + e.getMessage() + "\"}");
            return;
        }

        // Trả về JSON
        System.out.println("Trả về JSON: ");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format(
                "{ \"totalUsers\": %d, \"userGrowthRate\": %.1f, \"currentMonthPremium\": %d, \"premiumGrowthRate\": %.1f, \"totalCourses\": %d, \"courseGrowthRate\": %.1f }",
                totalUsers, userGrowthRate, currentMonthPremium, premiumGrowthRate, totalCourses, courseGrowthRate
        );
        System.out.println(json);

        response.getWriter().write(json);
        System.out.println("Hoàn tất xử lý yêu cầu UserStatsServlet");
    }
}