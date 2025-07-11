/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import Dao.UserPremiumDAO;
import DB.JDBCConnection;
import model.UserPremium;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;

public class Testcase {
    
    // Vietnam timezone
    private static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    
    public static void main(String[] args) {
        testPremiumExtension();
    }
    
    /**
     * Test logic cộng dồn premium
     */
    public static void testPremiumExtension() {
        System.out.println("=== TEST PREMIUM EXTENSION LOGIC ===");
        
        try (Connection conn = JDBCConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            UserPremiumDAO dao = new UserPremiumDAO();
            int testUserId = 1; // Sử dụng user ID có sẵn
            int testPlanId = 1; // Gói 1 tháng
            
            System.out.println("Testing with UserID: " + testUserId + ", PlanID: " + testPlanId);
            
            // Kiểm tra premium hiện tại
            UserPremium currentPremium = dao.getCurrentUserPremium(testUserId);
            if (currentPremium != null) {
                System.out.println("Current premium found:");
                System.out.println("- Start Date: " + currentPremium.getStartDate());
                System.out.println("- End Date: " + currentPremium.getEndDate());
                
                // Kiểm tra còn hạn không
                Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                Date currentTime = vietnamCal.getTime();
                System.out.println("- Current Vietnam time: " + currentTime);
                System.out.println("- Is expired: " + currentPremium.getEndDate().before(currentTime));
            } else {
                System.out.println("No current premium found for user");
            }
            
            System.out.println("\nTesting premium extension (adding 1 month)...");
            
            // Test cộng thêm 1 tháng
            dao.extendOrCreatePremium(testUserId, testPlanId, 1, conn);
            
            // Kiểm tra kết quả
            UserPremium updatedPremium = dao.getCurrentUserPremium(testUserId);
            if (updatedPremium != null) {
                System.out.println("\nAfter extension:");
                System.out.println("- Start Date: " + updatedPremium.getStartDate());
                System.out.println("- End Date: " + updatedPremium.getEndDate());
            }
            
            // Test cộng thêm 2 tháng nữa
            System.out.println("\nTesting another extension (adding 2 more months)...");
            dao.extendOrCreatePremium(testUserId, testPlanId, 2, conn);
            
            // Kiểm tra kết quả cuối cùng
            UserPremium finalPremium = dao.getCurrentUserPremium(testUserId);
            if (finalPremium != null) {
                System.out.println("\nFinal result:");
                System.out.println("- Start Date: " + finalPremium.getStartDate());
                System.out.println("- End Date: " + finalPremium.getEndDate());
                
                // Tính số ngày còn lại
                Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                Date currentTime = vietnamCal.getTime();
                long diffInMillis = finalPremium.getEndDate().getTime() - currentTime.getTime();
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
                System.out.println("- Days remaining: " + diffInDays);
            }
            
            // Rollback để không ảnh hưởng data thật
            conn.rollback();
            System.out.println("\nTest completed (rolled back)");
            
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== END TEST ===");
    }
    
    public static void printlist(List<?> list) {
        if (list == null || list.isEmpty()) {
            System.out.println("List is empty or null");
            return;
        }
        
        for (Object item : list) {
            System.out.println(item.toString());
        }
    }
}
