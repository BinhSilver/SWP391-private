package controller.payment;

import Dao.UserPremiumDAO;
import Dao.PremiumPlanDAO;
import Dao.PaymentDAO;
import DB.JDBCConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Payment;
import model.PremiumPlan;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import model.UserPremium;

@WebServlet("/ReturnFromPayOS")
public class ReturnFromPayOS extends HttpServlet {
    
    // Vietnam timezone
    private static final TimeZone VIETNAM_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== ReturnFromPayOS: Processing payment callback ===");
        
        // Get payment status and other parameters from PayOS callback
        String status = request.getParameter("status");
        String orderCodeParam = request.getParameter("orderCode");
        String code = request.getParameter("code");
        String cancel = request.getParameter("cancel");
        String id = request.getParameter("id");
        
        System.out.println("Callback parameters:");
        System.out.println("- status: " + status);
        System.out.println("- orderCode: " + orderCodeParam);
        System.out.println("- code: " + code);
        System.out.println("- cancel: " + cancel);
        System.out.println("- id: " + id);
        
        HttpSession session = request.getSession(false);
        
        System.out.println("Session available: " + (session != null));
        
        // *** ALWAYS UPDATE PAYMENT STATUS FIRST - REGARDLESS OF SESSION ***
        boolean paymentStatusUpdated = false;
        long orderCode = 0;
        
        if (orderCodeParam != null) {
            try {
                orderCode = Long.parseLong(orderCodeParam);
                System.out.println("Parsed OrderCode: " + orderCode);
                
                PaymentDAO paymentDAO = new PaymentDAO();
                
                if ("PAID".equals(status)) {
                    // Payment successful - update to success
                    System.out.println("Payment successful - updating status to Success");
                    paymentStatusUpdated = paymentDAO.updatePaymentStatus(
                        orderCode, 
                        "Success", 
                        id, // Use PayOS transaction ID
                        code  // Response code from PayOS
                    );
                } else {
                    // Payment failed or cancelled
                    String failedStatus = "true".equals(cancel) ? "Cancelled" : "Failed";
                    System.out.println("Payment not successful - updating status to: " + failedStatus);
                    paymentStatusUpdated = paymentDAO.updatePaymentStatus(
                        orderCode, 
                        failedStatus, 
                        id, 
                        code
                    );
                }
                
                System.out.println("Payment status update result: " + (paymentStatusUpdated ? "SUCCESS" : "FAILED"));
                
            } catch (NumberFormatException e) {
                System.out.println("ERROR: Invalid orderCode format: " + orderCodeParam);
            } catch (SQLException e) {
                System.out.println("ERROR: Failed to update payment status: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("WARNING: No orderCode provided in callback");
        }
        
        // *** PROCESS PREMIUM UPGRADE IF PAYMENT SUCCESSFUL ***
        if ("PAID".equals(status) && paymentStatusUpdated && orderCode > 0) {
            System.out.println("Processing premium upgrade...");
            
            Connection conn = null;
            try {
                // Get payment info from database to get user and plan details
                PaymentDAO paymentDAO = new PaymentDAO();
                Payment payment = paymentDAO.getPaymentByOrderCode(orderCode);
                
                if (payment != null) {
                    System.out.println("Payment found in database:");
                    System.out.println("- UserID: " + payment.getUserID());
                    System.out.println("- PlanID: " + payment.getPlanID());
                    System.out.println("- Amount: " + payment.getAmount());
                    
                    // Start transaction for premium upgrade
                    conn = JDBCConnection.getConnection();
                    conn.setAutoCommit(false);
                    
                    System.out.println("Premium upgrade transaction started");
                    
                                         // Update user role to premium (2)
                     service.UserService userService = new service.UserService();
                     Dao.UserDAO userDAO = new Dao.UserDAO();
                     model.User user = userDAO.getUserById(payment.getUserID());
                    
                    if (user != null) {
                        // Chỉ cập nhật role thành Premium nếu user hiện tại là Free user (role 1)
                        // Teacher (role 3) và Admin (role 4) sẽ giữ nguyên role của họ
                        if (user.getRoleID() == 1) { // Free user
                            user.setRoleID(2); // Premium role
                            userService.updateUserWithConnection(user, conn);
                            System.out.println("User role updated to Premium for UserID: " + payment.getUserID());
                        } else {
                            System.out.println("User role unchanged - UserID: " + payment.getUserID() + " (Current role: " + user.getRoleID() + ")");
                        }
                        
                        // Get plan duration
                        PremiumPlanDAO planDAO = new PremiumPlanDAO();
                        int durationInMonths = planDAO.getPlanDuration(payment.getPlanID());
                        
                        System.out.println("Plan duration: " + durationInMonths + " months");
                        
                        // Extend or create premium subscription
                        UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                        userPremiumDAO.extendOrCreatePremium(payment.getUserID(), payment.getPlanID(), durationInMonths, conn);
                        
                        // Lấy thông tin gói premium đã mua
                        PremiumPlanDAO premiumPlanDAO = new PremiumPlanDAO();
                        PremiumPlan purchasedPlan = premiumPlanDAO.getPremiumPlanByID(payment.getPlanID());
                        
                        // Commit transaction
                        conn.commit();
                        System.out.println("Premium upgrade transaction committed successfully");
                        
                                                 // Update session if available
                         if (session != null) {
                             user = userDAO.getUserById(payment.getUserID()); // Get fresh user data
                            session.setAttribute("authUser", user);
                            session.setAttribute("paymentSuccess", true);
                            session.setAttribute("paymentMessage", "Thanh toán thành công!");
                            
                            // Lưu thông tin gói premium đã mua vào session
                            if (purchasedPlan != null) {
                                session.setAttribute("purchasedPlan", purchasedPlan);
                            }
                            
                            // Clean up session
                            session.removeAttribute("currentPaymentId");
                            session.removeAttribute("currentOrderCode");
                            session.removeAttribute("selectedPlanId");
                            
                            System.out.println("Session updated successfully");
                        } else {
                            System.out.println("WARNING: Session not available, but premium upgrade completed");
                        }
                        
                        // Redirect to success page
                        response.sendRedirect(request.getContextPath() + "/PaymentJSP/PaymentSuccess.jsp");
                        return;
                        
                    } else {
                        System.out.println("ERROR: User not found for UserID: " + payment.getUserID());
                        throw new SQLException("User not found for UserID: " + payment.getUserID());
                    }
                    
                } else {
                    System.out.println("ERROR: Payment not found in database for OrderCode: " + orderCode);
                    throw new SQLException("Payment not found in database for OrderCode: " + orderCode);
                }
                
            } catch (SQLException e) {
                // Rollback premium upgrade transaction if error
                System.out.println("ERROR: Premium upgrade failed - " + e.getMessage());
                try {
                    if (conn != null) {
                        conn.rollback();
                        System.out.println("Premium upgrade transaction rolled back");
                    }
                } catch (SQLException ex) {
                    System.out.println("ERROR: Rollback failed - " + ex.getMessage());
                    ex.printStackTrace();
                }
                
                e.printStackTrace();
                if (session != null) {
                    session.setAttribute("paymentSuccess", false);
                    session.setAttribute("paymentMessage", "Thanh toán thành công nhưng có lỗi khi nâng cấp Premium: " + e.getMessage());
                }
                
            } catch (Exception e) {
                // Handle other exceptions
                System.out.println("ERROR: Unexpected error during premium upgrade - " + e.getMessage());
                try {
                    if (conn != null) {
                        conn.rollback();
                        System.out.println("Premium upgrade transaction rolled back due to unexpected error");
                    }
                } catch (SQLException ex) {
                    System.out.println("ERROR: Rollback failed - " + ex.getMessage());
                    ex.printStackTrace();
                }
                
                e.printStackTrace();
                if (session != null) {
                    session.setAttribute("paymentSuccess", false);
                    session.setAttribute("paymentMessage", "Thanh toán thành công nhưng có lỗi không mong đợi khi nâng cấp Premium: " + e.getMessage());
                }
                
            } finally {
                // Close connection
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                        System.out.println("Premium upgrade connection closed");
                    }
                } catch (SQLException e) {
                    System.out.println("ERROR: Failed to close premium upgrade connection - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            // Payment failed, cancelled, or payment status update failed
            System.out.println("Payment not successful or payment status update failed");
            
            if (session != null) {
                session.setAttribute("paymentSuccess", false);
                String message;
                if (!paymentStatusUpdated) {
                    message = "Có lỗi khi cập nhật trạng thái thanh toán!";
                } else if ("true".equals(cancel)) {
                    message = "Thanh toán đã bị hủy!";
                } else {
                    message = "Thanh toán không thành công!";
                }
                session.setAttribute("paymentMessage", message);
                
                // Clean up session
                session.removeAttribute("currentPaymentId");
                session.removeAttribute("currentOrderCode");
            }
        }
        
        System.out.println("=== ReturnFromPayOS: Processing completed ===");
        
        // Redirect to home page if not already redirected
        response.sendRedirect(request.getContextPath() + "/HomeServlet");
    }
} 