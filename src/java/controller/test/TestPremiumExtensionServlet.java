package controller.test;

import Dao.UserPremiumDAO;
import Dao.PremiumPlanDAO;
import DB.JDBCConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UserPremium;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@WebServlet("/test/premium-extension")
public class TestPremiumExtensionServlet extends HttpServlet {
    
    // Vietnam timezone
    private static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Test Premium Extension</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("pre { background: #f5f5f5; padding: 10px; border-left: 3px solid #007bff; }");
        out.println(".success { color: green; } .error { color: red; }");
        out.println("</style></head><body>");
        out.println("<h1>Test Premium Extension Logic</h1>");
        
        String action = request.getParameter("action");
        
        if ("test".equals(action)) {
            testPremiumExtension(out);
        } else {
            showTestForm(out);
        }
        
        out.println("</body></html>");
    }
    
    private void showTestForm(PrintWriter out) {
        out.println("<p>Servlet này sẽ test logic cộng dồn premium với các case sau:</p>");
        out.println("<ul>");
        out.println("<li>User chưa có premium → tạo mới</li>");
        out.println("<li>User có premium còn hạn → cộng dồn từ ngày hết hạn</li>");
        out.println("<li>User có premium hết hạn → tạo mới từ ngày hiện tại</li>");
        out.println("</ul>");
        out.println("<p><strong>Lưu ý:</strong> Test sẽ rollback nên không ảnh hưởng đến dữ liệu thật.</p>");
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='test'>");
        out.println("<button type='submit'>Chạy Test</button>");
        out.println("</form>");
    }
    
    private void testPremiumExtension(PrintWriter out) {
        out.println("<h2>Kết quả Test</h2>");
        out.println("<pre>");
        
        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            UserPremiumDAO dao = new UserPremiumDAO();
            PremiumPlanDAO planDAO = new PremiumPlanDAO();
            
            int testUserId = 1; // User ID có sẵn
            int testPlanId = 1; // Plan ID có sẵn
            
            out.println("=== TESTING PREMIUM EXTENSION LOGIC ===");
            out.println("Test User ID: " + testUserId);
            out.println("Test Plan ID: " + testPlanId);
            
            // Lấy thông tin plan
            try {
                int planDuration = planDAO.getPlanDuration(testPlanId);
                out.println("Plan Duration: " + planDuration + " months");
            } catch (Exception e) {
                out.println("Plan Duration: Could not fetch (" + e.getMessage() + ")");
            }
            
            Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
            Date currentTime = vietnamCal.getTime();
            out.println("Current Vietnam Time: " + currentTime);
            out.println();
            
            // Kiểm tra premium hiện tại
            out.println("--- CHECKING CURRENT PREMIUM ---");
            UserPremium currentPremium = dao.getCurrentUserPremium(testUserId);
            if (currentPremium != null) {
                out.println("✓ Current premium found:");
                out.println("  Start Date: " + currentPremium.getStartDate());
                out.println("  End Date: " + currentPremium.getEndDate());
                out.println("  Is Expired: " + currentPremium.getEndDate().before(currentTime));
            } else {
                out.println("✗ No current premium found");
            }
            out.println();
            
            // Test 1: Extend premium 1 tháng
            out.println("--- TEST 1: EXTENDING 1 MONTH ---");
            dao.extendOrCreatePremium(testUserId, testPlanId, 1, conn);
            
            UserPremium afterTest1 = dao.getCurrentUserPremium(testUserId);
            if (afterTest1 != null) {
                out.println("✓ After extending 1 month:");
                out.println("  Start Date: " + afterTest1.getStartDate());
                out.println("  End Date: " + afterTest1.getEndDate());
                
                long diffInMillis = afterTest1.getEndDate().getTime() - currentTime.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                out.println("  Days from now: " + diffInDays);
            }
            out.println();
            
            // Test 2: Extend thêm 2 tháng nữa
            out.println("--- TEST 2: EXTENDING 2 MORE MONTHS ---");
            dao.extendOrCreatePremium(testUserId, testPlanId, 2, conn);
            
            UserPremium afterTest2 = dao.getCurrentUserPremium(testUserId);
            if (afterTest2 != null) {
                out.println("✓ After extending 2 more months:");
                out.println("  Start Date: " + afterTest2.getStartDate());
                out.println("  End Date: " + afterTest2.getEndDate());
                
                long diffInMillis = afterTest2.getEndDate().getTime() - currentTime.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                out.println("  Days from now: " + diffInDays);
                
                // So sánh với test 1
                if (afterTest1 != null) {
                    long extendedMillis = afterTest2.getEndDate().getTime() - afterTest1.getEndDate().getTime();
                    long extendedDays = extendedMillis / (24 * 60 * 60 * 1000);
                    out.println("  Extended by: " + extendedDays + " days (~" + (extendedDays/30) + " months)");
                }
            }
            out.println();
            
            // Test 3: Kiểm tra timezone
            out.println("--- TEST 3: TIMEZONE VERIFICATION ---");
            Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            out.println("UTC Time: " + utcCal.getTime());
            out.println("Vietnam Time: " + vietnamCal.getTime());
            
            long timezoneDiff = vietnamCal.getTimeInMillis() - utcCal.getTimeInMillis();
            int hoursDiff = (int) (timezoneDiff / (1000 * 60 * 60));
            out.println("Timezone Difference: " + hoursDiff + " hours");
            out.println();
            
            // Rollback
            conn.rollback();
            out.println("--- ROLLBACK COMPLETED ---");
            out.println("✓ All changes have been rolled back");
            out.println("✓ Database remains unchanged");
            
            out.println("\n=== TEST COMPLETED SUCCESSFULLY ===");
            
        } catch (Exception e) {
            out.println("\n=== TEST FAILED ===");
            out.println("Error: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?'>← Quay lại</a>");
    }
} 