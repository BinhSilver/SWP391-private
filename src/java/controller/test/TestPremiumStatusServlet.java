package controller.test;

import Dao.UserDAO;
import Dao.UserPremiumDAO;
import Dao.PaymentDAO;
import Dao.PremiumPlanDAO;
import DB.JDBCConnection;
import service.UserService;
import model.User;
import model.UserPremium;
import model.Payment;
import model.PremiumPlan;
import java.sql.Connection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@WebServlet("/test/premium-status")
public class TestPremiumStatusServlet extends HttpServlet {
    
    // Vietnam timezone
    private static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Test Premium Status</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("pre { background: #f5f5f5; padding: 10px; border-left: 3px solid #007bff; }");
        out.println(".success { color: green; font-weight: bold; } .error { color: red; font-weight: bold; } .warning { color: orange; font-weight: bold; }");
        out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println(".status-free { background-color: #fff3cd; color: #856404; padding: 4px 8px; border-radius: 4px; }");
        out.println(".status-premium { background-color: #d4edda; color: #155724; padding: 4px 8px; border-radius: 4px; }");
        out.println(".btn { background-color: #007bff; color: white; padding: 8px 12px; text-decoration: none; border-radius: 4px; border: none; cursor: pointer; }");
        out.println(".btn-success { background-color: #28a745; }");
        out.println(".btn-warning { background-color: #ffc107; color: #212529; }");
        out.println(".btn-danger { background-color: #dc3545; }");
        out.println("</style></head><body>");
        out.println("<h1>🔍 Premium Status Test & Debug Tool</h1>");
        
        String action = request.getParameter("action");
        String userIdParam = request.getParameter("userId");
        String orderCodeParam = request.getParameter("orderCode");
        
        if ("check-user".equals(action) && userIdParam != null) {
            checkUserPremiumStatus(out, userIdParam);
        } else if ("fix-premium".equals(action) && userIdParam != null && orderCodeParam != null) {
            fixPremiumStatus(out, userIdParam, orderCodeParam);
        } else if ("extend-premium".equals(action) && userIdParam != null) {
            extendPremiumManually(out, userIdParam);
        } else {
            showTestMenu(out);
        }
        
        out.println("</body></html>");
    }
    
    private void showTestMenu(PrintWriter out) {
        out.println("<h2>Premium Status Tools</h2>");
        out.println("<p>Tools để debug và sửa premium status issues:</p>");
        
        out.println("<div style='margin: 20px 0; padding: 15px; background-color: #e9ecef; border-radius: 5px;'>");
        out.println("<h3>🔍 Check User Premium Status</h3>");
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='check-user'>");
        out.println("<label>User ID: <input type='number' name='userId' placeholder='Enter User ID' required min='1'></label>");
        out.println("<input type='submit' value='Check Status' class='btn'>");
        out.println("</form>");
        out.println("</div>");
        
        out.println("<div style='margin: 20px 0; padding: 15px; background-color: #fff3cd; border-radius: 5px;'>");
        out.println("<h3>🔧 Fix Premium Status (Based on Payment)</h3>");
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='fix-premium'>");
        out.println("<label>User ID: <input type='number' name='userId' placeholder='Enter User ID' required min='1'></label><br><br>");
        out.println("<label>OrderCode: <input type='text' name='orderCode' placeholder='Enter OrderCode from successful payment' required></label><br><br>");
        out.println("<input type='submit' value='Fix Premium Status' class='btn btn-warning'>");
        out.println("</form>");
        out.println("<small>⚠️ Sử dụng khi payment thành công nhưng premium chưa được activate</small>");
        out.println("</div>");
        
        out.println("<div style='margin: 20px 0; padding: 15px; background-color: #d4edda; border-radius: 5px;'>");
        out.println("<h3>➕ Extend Premium Manually</h3>");
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='extend-premium'>");
        out.println("<label>User ID: <input type='number' name='userId' placeholder='Enter User ID' required min='1'></label>");
        out.println("<input type='submit' value='Add 1 Month Premium' class='btn btn-success'>");
        out.println("</form>");
        out.println("<small>✅ Thêm 1 tháng premium cho user (for testing)</small>");
        out.println("</div>");
        
        out.println("<div style='margin: 20px 0; background-color: #f8f9fa; padding: 15px; border-radius: 5px;'>");
        out.println("<h4>📌 Common Use Cases:</h4>");
        out.println("<ul>");
        out.println("<li><strong>Payment thành công nhưng vẫn Free:</strong> Dùng 'Fix Premium Status'</li>");
        out.println("<li><strong>Test premium extension:</strong> Dùng 'Extend Premium Manually'</li>");
        out.println("<li><strong>Debug premium logic:</strong> Dùng 'Check User Premium Status'</li>");
        out.println("</ul>");
        out.println("</div>");
    }
    
    private void checkUserPremiumStatus(PrintWriter out, String userIdParam) {
        out.println("<h2>Check User Premium Status</h2>");
        out.println("<pre>");
        
        try {
            int userId = Integer.parseInt(userIdParam);
            System.out.println("=== CHECKING USER PREMIUM STATUS ===");
            System.out.println("UserID: " + userId);
            
            // Check user basic info
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);
            
            if (user != null) {
                out.println("✓ User Found!");
                out.println("===========================================");
                out.println("USER BASIC INFORMATION:");
                out.println("- UserID: " + user.getUserID());
                out.println("- Email: " + user.getEmail());
                out.println("- FullName: " + user.getFullName());
                out.println("- CreatedAt: " + user.getCreatedAt());
                out.println();
                
                // Check role
                out.println("ROLE INFORMATION:");
                out.print("- RoleID: " + user.getRoleID() + " → ");
                switch (user.getRoleID()) {
                    case 1:
                        out.println("🆓 FREE USER");
                        break;
                    case 2:
                        out.println("⭐ PREMIUM USER");
                        break;
                    case 3:
                        out.println("👨‍🏫 TEACHER");
                        break;
                    case 4:
                        out.println("👑 ADMIN");
                        break;
                    default:
                        out.println("❓ UNKNOWN ROLE");
                }
                out.println();
                
                // Check premium subscription
                out.println("PREMIUM SUBSCRIPTION INFO:");
                UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                UserPremium userPremium = userPremiumDAO.getCurrentUserPremium(userId);
                
                if (userPremium != null) {
                    out.println("✅ Premium subscription found:");
                    out.println("- PlanID: " + userPremium.getPlanID());
                    out.println("- StartDate: " + userPremium.getStartDate());
                    out.println("- EndDate: " + userPremium.getEndDate());
                    
                    // Check if expired
                    Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                    Date now = vietnamCal.getTime();
                    Date endDate = userPremium.getEndDate();
                    
                    if (endDate.after(now)) {
                        long daysLeft = (endDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                        out.println("- Status: ✅ ACTIVE (" + daysLeft + " days left)");
                    } else {
                        out.println("- Status: ❌ EXPIRED");
                    }
                    
                    // Get plan details
                    try {
                        PremiumPlanDAO planDAO = new PremiumPlanDAO();
                        PremiumPlan plan = planDAO.getPlanById(userPremium.getPlanID());
                        if (plan != null) {
                            out.println("- Plan Name: " + plan.getPlanName());
                            out.println("- Duration: " + plan.getDurationInMonths() + " months");
                            out.println("- Price: " + String.format("%,.0f", plan.getPrice()) + " VND");
                        }
                    } catch (SQLException e) {
                        out.println("- Plan Details: ERROR getting plan info");
                    }
                } else {
                    out.println("❌ No premium subscription found");
                }
                out.println();
                
                // Check payment history
                out.println("PAYMENT HISTORY:");
                try {
                    PaymentDAO paymentDAO = new PaymentDAO();
                    // Note: This would need a new method to get payments by userID
                    out.println("📝 To check payment history, use payment tools with specific OrderCode");
                } catch (Exception e) {
                    out.println("❌ Error checking payment history: " + e.getMessage());
                }
                out.println();
                
                // Diagnosis
                out.println("===========================================");
                out.println("🔍 DIAGNOSIS:");
                
                if (user.getRoleID() == 1 && userPremium == null) {
                    out.println("❗ User is FREE and has no premium subscription");
                    out.println("   → This is normal for free users");
                } else if (user.getRoleID() == 1 && userPremium != null) {
                    out.println("⚠️  User has premium subscription but role is still FREE");
                    out.println("   → Role should be updated to Premium (2)");
                    out.println("   → Use 'Fix Premium Status' if payment was successful");
                } else if (user.getRoleID() == 2 && userPremium == null) {
                    out.println("⚠️  User role is PREMIUM but no subscription found");
                    out.println("   → UserPremium record missing");
                    out.println("   → Use 'Fix Premium Status' to create subscription");
                } else if (user.getRoleID() == 2 && userPremium != null) {
                    Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                    Date now = vietnamCal.getTime();
                    if (userPremium.getEndDate().after(now)) {
                        out.println("✅ Everything looks good! User has active premium");
                    } else {
                        out.println("⚠️  Premium subscription has expired");
                        out.println("   → Role should be downgraded to Free (1)");
                    }
                }
                
            } else {
                out.println("❌ User not found with ID: " + userId);
            }
            
        } catch (NumberFormatException e) {
            out.println("❌ ERROR: Invalid User ID format: " + userIdParam);
        } catch (Exception e) {
            out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
    }
    
    private void fixPremiumStatus(PrintWriter out, String userIdParam, String orderCodeParam) {
        out.println("<h2>Fix Premium Status</h2>");
        out.println("<pre>");
        
        try {
            int userId = Integer.parseInt(userIdParam);
            long orderCode = Long.parseLong(orderCodeParam);
            
            System.out.println("=== FIXING PREMIUM STATUS ===");
            System.out.println("UserID: " + userId);
            System.out.println("OrderCode: " + orderCode);
            
            // Verify payment exists and is successful
            PaymentDAO paymentDAO = new PaymentDAO();
            Payment payment = paymentDAO.getPaymentByOrderCode(orderCode);
            
            if (payment == null) {
                out.println("❌ ERROR: Payment not found with OrderCode: " + orderCode);
                out.println("</pre>");
                out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
                return;
            }
            
            if (payment.getUserID() != userId) {
                out.println("❌ ERROR: Payment OrderCode " + orderCode + " belongs to UserID " + 
                          payment.getUserID() + ", not " + userId);
                out.println("</pre>");
                out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
                return;
            }
            
            if (!"Success".equals(payment.getTransactionStatus())) {
                out.println("❌ ERROR: Payment status is " + payment.getTransactionStatus() + ", not Success");
                out.println("   Use payment tools to fix payment status first");
                out.println("</pre>");
                out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
                return;
            }
            
            out.println("✅ Payment verified:");
            out.println("- OrderCode: " + payment.getOrderCode());
            out.println("- UserID: " + payment.getUserID());
            out.println("- PlanID: " + payment.getPlanID());
            out.println("- Amount: " + String.format("%,.0f", payment.getAmount()) + " VND");
            out.println("- Status: " + payment.getTransactionStatus());
            out.println();
            
            // Update user role to premium
            out.println("🔧 Updating user role to Premium...");
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);
            
            if (user != null) {
                user.setRoleID(2); // Premium role
                UserService userService = new UserService();
                boolean roleUpdated = userService.updateUser(user);
                
                if (roleUpdated) {
                    out.println("✅ User role updated to Premium (2)");
                } else {
                    out.println("❌ Failed to update user role");
                }
            } else {
                out.println("❌ User not found");
                out.println("</pre>");
                out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
                return;
            }
            
            // Create/extend premium subscription
            out.println("🔧 Creating/extending premium subscription...");
            try (Connection conn = JDBCConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                PremiumPlanDAO planDAO = new PremiumPlanDAO();
                int durationInMonths = planDAO.getPlanDuration(payment.getPlanID());
                
                UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                userPremiumDAO.extendOrCreatePremium(userId, payment.getPlanID(), durationInMonths, conn);
                
                conn.commit();
                
                out.println("✅ Premium subscription created/extended");
                out.println("- Plan ID: " + payment.getPlanID());
                out.println("- Duration: " + durationInMonths + " months");
                
                // Verify the fix
                UserPremium userPremium = userPremiumDAO.getCurrentUserPremium(userId);
                if (userPremium != null) {
                    out.println("✅ Verification - Premium subscription found:");
                    out.println("- Start Date: " + userPremium.getStartDate());
                    out.println("- End Date: " + userPremium.getEndDate());
                    
                    Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                    Date now = vietnamCal.getTime();
                    long daysLeft = (userPremium.getEndDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    out.println("- Days Left: " + daysLeft);
                }
                
                out.println();
                out.println("🎉 SUCCESS: Premium status fixed!");
                out.println("   User should now see premium benefits on profile page.");
                out.println("   User may need to refresh or login again.");
                
            } catch (SQLException e) {
                out.println("❌ ERROR creating premium subscription: " + e.getMessage());
                e.printStackTrace(out);
            }
            
        } catch (NumberFormatException e) {
            out.println("❌ ERROR: Invalid number format");
        } catch (Exception e) {
            out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
    }
    
    private void extendPremiumManually(PrintWriter out, String userIdParam) {
        out.println("<h2>Extend Premium Manually</h2>");
        out.println("<pre>");
        
        try {
            int userId = Integer.parseInt(userIdParam);
            
            System.out.println("=== EXTENDING PREMIUM MANUALLY ===");
            System.out.println("UserID: " + userId);
            
            // Check if user exists
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);
            
            if (user == null) {
                out.println("❌ ERROR: User not found with ID: " + userId);
                out.println("</pre>");
                out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
                return;
            }
            
            out.println("✅ User found: " + user.getEmail());
            out.println();
            
            // Update role to premium if needed
            if (user.getRoleID() != 2) {
                out.println("🔧 Updating user role to Premium...");
                user.setRoleID(2);
                UserService userService = new UserService();
                boolean roleUpdated = userService.updateUser(user);
                
                if (roleUpdated) {
                    out.println("✅ User role updated to Premium (2)");
                } else {
                    out.println("❌ Failed to update user role");
                }
            } else {
                out.println("ℹ️ User already has Premium role");
            }
            
            // Extend premium by 1 month (using plan ID 1 - monthly plan)
            out.println("🔧 Adding 1 month premium...");
            try (Connection conn = JDBCConnection.getConnection()) {
                conn.setAutoCommit(false);
                
                UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                userPremiumDAO.extendOrCreatePremium(userId, 1, 1, conn); // Plan ID 1, 1 month
                
                conn.commit();
                out.println("✅ Premium extended by 1 month");
                
                // Show updated premium info
                UserPremium userPremium = userPremiumDAO.getCurrentUserPremium(userId);
                if (userPremium != null) {
                    out.println("✅ Updated premium subscription:");
                    out.println("- Start Date: " + userPremium.getStartDate());
                    out.println("- End Date: " + userPremium.getEndDate());
                    
                    Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                    Date now = vietnamCal.getTime();
                    long daysLeft = (userPremium.getEndDate().getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
                    out.println("- Days Left: " + daysLeft);
                }
                
                out.println();
                out.println("🎉 SUCCESS: Premium extended!");
                out.println("   User now has additional 1 month of premium access.");
                
            } catch (SQLException e) {
                out.println("❌ ERROR extending premium: " + e.getMessage());
                e.printStackTrace(out);
            }
            
        } catch (NumberFormatException e) {
            out.println("❌ ERROR: Invalid User ID format: " + userIdParam);
        } catch (Exception e) {
            out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?' class='btn'>← Back to Menu</a>");
    }
} 