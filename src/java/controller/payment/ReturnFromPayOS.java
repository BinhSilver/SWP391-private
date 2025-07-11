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
        Connection conn = null;
        
        System.out.println("Session available: " + (session != null));
        
        if ("PAID".equals(status) && session != null) {
            try {
                // Parse orderCode
                long orderCode = 0;
                if (orderCodeParam != null) {
                    try {
                        orderCode = Long.parseLong(orderCodeParam);
                        System.out.println("Parsed OrderCode: " + orderCode);
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR: Invalid orderCode format: " + orderCodeParam);
                    }
                }
                
                // Bắt đầu transaction
                conn = JDBCConnection.getConnection();
                conn.setAutoCommit(false);
                
                System.out.println("Transaction started");
                
                // *** UPDATE PAYMENT STATUS IN DATABASE ***
                try {
                    PaymentDAO paymentDAO = new PaymentDAO();
                    
                    if (orderCode > 0) {
                        System.out.println("Updating payment status in database...");
                        boolean updateSuccess = paymentDAO.updatePaymentStatus(
                            orderCode, 
                            "Success", 
                            id, // Use PayOS transaction ID
                            null, // BankCode (may be provided by PayOS in some cases)
                            code,  // Response code from PayOS
                            conn  // Use transaction connection
                        );
                        
                        if (updateSuccess) {
                            System.out.println("✓ Payment status updated successfully in database");
                        } else {
                            System.out.println("✗ Failed to update payment status in database");
                            throw new SQLException("Failed to update payment status for OrderCode: " + orderCode);
                        }
                    } else {
                        System.out.println("WARNING: No valid orderCode provided, skipping payment status update");
                    }
                    
                } catch (SQLException e) {
                    System.out.println("✗ Database error while updating payment status: " + e.getMessage());
                    e.printStackTrace();
                    throw e; // Re-throw to trigger rollback
                }
                
                // Payment successful
                session.setAttribute("paymentSuccess", true);
                session.setAttribute("paymentMessage", "Thanh toán thành công!");

                // Lấy thông tin user và plan
                model.User user = (model.User) session.getAttribute("authUser");
                Integer planId = (Integer) session.getAttribute("selectedPlanId");
                
                System.out.println("User: " + (user != null ? user.getEmail() : "null"));
                System.out.println("Plan ID: " + planId);
                
                if (user != null && planId != null) {
                    // Cập nhật role thành premium (2) trong database
                    user.setRoleID(2);
                    service.UserService userService = new service.UserService();
                    userService.updateUserWithConnection(user, conn);
                    session.setAttribute("authUser", user);
                    
                    System.out.println("User role updated to Premium");

                    // Lấy thời lượng gói premium
                    PremiumPlanDAO planDAO = new PremiumPlanDAO();
                    int durationInMonths = planDAO.getPlanDuration(planId);
                    
                    System.out.println("Plan duration: " + durationInMonths + " months");

                    // Sử dụng method mới để cộng dồn/tạo premium
                    UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                    userPremiumDAO.extendOrCreatePremium(user.getUserID(), planId, durationInMonths, conn);
                    
                    // Commit transaction
                    conn.commit();
                    System.out.println("Transaction committed successfully");
                    
                    // Log thông tin timezone
                    Calendar vietnamCal = Calendar.getInstance(VIETNAM_TIMEZONE);
                    System.out.println("Payment processed at Vietnam time: " + vietnamCal.getTime());
                    
                    // Clean up session
                    session.removeAttribute("currentPaymentId");
                    session.removeAttribute("currentOrderCode");
                    session.removeAttribute("selectedPlanId");
                    
                    // Redirect to success page
                    response.sendRedirect(request.getContextPath() + "/PaymentJSP/PaymentSuccess.jsp");
                    return;
                } else {
                    System.out.println("ERROR: Missing user or plan information");
                    throw new SQLException("Missing user or plan information");
                }
            } catch (SQLException e) {
                // Rollback nếu có lỗi
                System.out.println("ERROR: SQL Exception occurred - " + e.getMessage());
                try {
                    if (conn != null) {
                        conn.rollback();
                        System.out.println("Transaction rolled back");
                    }
                } catch (SQLException ex) {
                    System.out.println("ERROR: Rollback failed - " + ex.getMessage());
                    ex.printStackTrace();
                }
                
                // Log lỗi và set thông báo lỗi
                e.printStackTrace();
                if (session != null) {
                    session.setAttribute("paymentSuccess", false);
                    session.setAttribute("paymentMessage", "Có lỗi xảy ra khi xử lý thanh toán: " + e.getMessage());
                }
            } catch (Exception e) {
                // Handle other exceptions
                System.out.println("ERROR: Unexpected exception - " + e.getMessage());
                try {
                    if (conn != null) {
                        conn.rollback();
                        System.out.println("Transaction rolled back due to unexpected error");
                    }
                } catch (SQLException ex) {
                    System.out.println("ERROR: Rollback failed - " + ex.getMessage());
                    ex.printStackTrace();
                }
                
                e.printStackTrace();
                if (session != null) {
                    session.setAttribute("paymentSuccess", false);
                    session.setAttribute("paymentMessage", "Có lỗi không mong đợi xảy ra: " + e.getMessage());
                }
            } finally {
                // Đóng connection
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                        System.out.println("Connection closed");
                    }
                } catch (SQLException e) {
                    System.out.println("ERROR: Failed to close connection - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            // Payment failed, cancelled, or other status
            System.out.println("Payment not successful. Status: " + status);
            
            // Try to update payment status in database if orderCode is available
            if (orderCodeParam != null) {
                try {
                    long orderCode = Long.parseLong(orderCodeParam);
                    PaymentDAO paymentDAO = new PaymentDAO();
                    
                    String failedStatus = "true".equals(cancel) ? "Cancelled" : "Failed";
                    System.out.println("Updating payment status to: " + failedStatus);
                    
                    paymentDAO.updatePaymentStatus(orderCode, failedStatus, id, null, code);
                    System.out.println("✓ Payment status updated to " + failedStatus);
                    
                } catch (Exception e) {
                    System.out.println("ERROR: Failed to update payment status for failed payment: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            if (session != null) {
                session.setAttribute("paymentSuccess", false);
                String message = "true".equals(cancel) ? "Thanh toán đã bị hủy!" : "Thanh toán không thành công!";
                session.setAttribute("paymentMessage", message);
                
                // Clean up session
                session.removeAttribute("currentPaymentId");
                session.removeAttribute("currentOrderCode");
            }
        }
        
        System.out.println("=== ReturnFromPayOS: Processing completed ===");
        
        // Redirect về trang chủ nếu có lỗi hoặc thanh toán không thành công
        response.sendRedirect(request.getContextPath() + "/HomeServlet");
    }
} 