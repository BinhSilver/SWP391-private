package controller.test;

import Dao.PaymentDAO;
import model.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/test/payment-status")
public class TestPaymentStatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Test Payment Status</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("pre { background: #f5f5f5; padding: 10px; border-left: 3px solid #007bff; }");
        out.println(".success { color: green; } .error { color: red; } .warning { color: orange; }");
        out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println(".status-pending { background-color: #fff3cd; color: #856404; }");
        out.println(".status-success { background-color: #d4edda; color: #155724; }");
        out.println(".status-failed { background-color: #f8d7da; color: #721c24; }");
        out.println("</style></head><body>");
        out.println("<h1>Payment Status Test & Debug Tool</h1>");
        
        String action = request.getParameter("action");
        String orderCodeParam = request.getParameter("orderCode");
        
        if ("check-status".equals(action) && orderCodeParam != null) {
            checkPaymentStatus(out, orderCodeParam);
        } else if ("force-update".equals(action) && orderCodeParam != null) {
            forceUpdatePaymentStatus(out, orderCodeParam);
        } else if ("list-pending".equals(action)) {
            listPendingPayments(out);
        } else {
            showTestMenu(out);
        }
        
        out.println("</body></html>");
    }
    
    private void showTestMenu(PrintWriter out) {
        out.println("<h2>Payment Status Tools</h2>");
        out.println("<p>Tools để debug và sửa payment pending:</p>");
        
        out.println("<div style='margin: 20px 0;'>");
        out.println("<h3>🔍 Check Payment Status</h3>");
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='check-status'>");
        out.println("<label>OrderCode: <input type='text' name='orderCode' placeholder='Enter OrderCode' required></label>");
        out.println("<input type='submit' value='Check Status'>");
        out.println("</form>");
        out.println("</div>");
        
        out.println("<div style='margin: 20px 0;'>");
        out.println("<h3>🔧 Force Update Payment Status</h3>");
        out.println("<form method='get'>");
        out.println("<input type='hidden' name='action' value='force-update'>");
        out.println("<label>OrderCode: <input type='text' name='orderCode' placeholder='Enter OrderCode' required></label>");
        out.println("<input type='submit' value='Force Update to Success' style='background-color: #28a745; color: white; border: none; padding: 8px 12px;'>");
        out.println("</form>");
        out.println("</div>");
        
        out.println("<div style='margin: 20px 0;'>");
        out.println("<h3>📋 List All Pending Payments</h3>");
        out.println("<p><a href='?action=list-pending' style='background-color: #007bff; color: white; padding: 8px 12px; text-decoration: none;'>Show Pending Payments</a></p>");
        out.println("</div>");
        
        out.println("<div style='margin: 20px 0; background-color: #e9ecef; padding: 15px; border-radius: 5px;'>");
        out.println("<h4>📌 Instructions:</h4>");
        out.println("<ul>");
        out.println("<li><strong>Check Status:</strong> Xem thông tin chi tiết của một payment</li>");
        out.println("<li><strong>Force Update:</strong> Cập nhật thủ công payment thành Success (dùng khi PayOS callback fail)</li>");
        out.println("<li><strong>List Pending:</strong> Hiển thị tất cả payments đang pending</li>");
        out.println("</ul>");
        out.println("</div>");
    }
    
    private void checkPaymentStatus(PrintWriter out, String orderCodeParam) {
        out.println("<h2>Check Payment Status</h2>");
        out.println("<pre>");
        
        try {
            long orderCode = Long.parseLong(orderCodeParam);
            System.out.println("=== CHECKING PAYMENT STATUS ===");
            System.out.println("OrderCode: " + orderCode);
            
            PaymentDAO paymentDAO = new PaymentDAO();
            Payment payment = paymentDAO.getPaymentByOrderCode(orderCode);
            
            if (payment != null) {
                out.println("✓ Payment Found!");
                out.println("===========================================");
                out.println("Payment Details:");
                out.println("- PaymentID: " + payment.getPaymentID());
                out.println("- UserID: " + payment.getUserID());
                out.println("- PlanID: " + payment.getPlanID());
                out.println("- Amount: " + String.format("%,.0f", payment.getAmount()) + " VND");
                out.println("- OrderInfo: " + payment.getOrderInfo());
                out.println("- OrderCode: " + payment.getOrderCode());
                out.println("- CheckoutUrl: " + payment.getCheckoutUrl());
                out.println("- CreatedAt: " + payment.getCreatedAt());
                out.println();
                
                // Status information
                String transactionStatus = payment.getTransactionStatus();
                String status = payment.getStatus();
                
                out.println("STATUS INFORMATION:");
                out.print("- TransactionStatus: ");
                if ("PENDING".equals(transactionStatus)) {
                    out.println("⏳ " + transactionStatus + " (PENDING - Chưa thanh toán)");
                } else if ("Success".equals(transactionStatus)) {
                    out.println("✅ " + transactionStatus + " (SUCCESS - Đã thanh toán)");
                } else if ("Failed".equals(transactionStatus) || "Cancelled".equals(transactionStatus)) {
                    out.println("❌ " + transactionStatus + " (FAILED/CANCELLED)");
                } else {
                    out.println("❓ " + transactionStatus + " (UNKNOWN STATUS)");
                }
                
                out.print("- Status: ");
                if ("COMPLETED".equals(status)) {
                    out.println("✅ " + status + " (COMPLETED)");
                } else if (status == null || status.trim().isEmpty()) {
                    out.println("❓ (NULL/EMPTY)");
                } else {
                    out.println("❓ " + status + " (OTHER)");
                }
                
                out.println("- TransactionNo: " + (payment.getTransactionNo() != null ? payment.getTransactionNo() : "NULL"));
                out.println("- BankCode: " + (payment.getBankCode() != null ? payment.getBankCode() : "NULL"));
                out.println("- ResponseCode: " + (payment.getResponseCode() != null ? payment.getResponseCode() : "NULL"));
                out.println("- PaymentDate: " + (payment.getPaymentDate() != null ? payment.getPaymentDate() : "NULL"));
                
                out.println();
                out.println("===========================================");
                
                // Diagnosis
                if ("PENDING".equals(transactionStatus)) {
                    out.println("🔍 DIAGNOSIS:");
                    out.println("❗ Payment vẫn đang PENDING");
                    out.println("   Có thể do:");
                    out.println("   1. PayOS callback chưa được gọi");
                    out.println("   2. Callback bị lỗi và không update được status");
                    out.println("   3. User chưa hoàn thành thanh toán");
                    out.println();
                    out.println("💡 SOLUTION:");
                    out.println("   - Nếu user đã thanh toán thành công, dùng 'Force Update' để cập nhật thủ công");
                    out.println("   - Kiểm tra logs của ReturnFromPayOS servlet");
                }
                
            } else {
                out.println("❌ No payment found with OrderCode: " + orderCode);
                out.println("    Payment này có thể chưa được tạo hoặc OrderCode không đúng.");
            }
            
        } catch (NumberFormatException e) {
            out.println("❌ ERROR: Invalid OrderCode format: " + orderCodeParam);
        } catch (SQLException e) {
            out.println("❌ SQL ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } catch (Exception e) {
            out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?'>← Back to Menu</a>");
    }
    
    private void forceUpdatePaymentStatus(PrintWriter out, String orderCodeParam) {
        out.println("<h2>Force Update Payment Status</h2>");
        out.println("<pre>");
        
        try {
            long orderCode = Long.parseLong(orderCodeParam);
            System.out.println("=== FORCE UPDATE PAYMENT STATUS ===");
            System.out.println("OrderCode: " + orderCode);
            
            PaymentDAO paymentDAO = new PaymentDAO();
            
            // First check if payment exists
            Payment payment = paymentDAO.getPaymentByOrderCode(orderCode);
            if (payment == null) {
                out.println("❌ ERROR: Payment not found with OrderCode: " + orderCode);
                out.println("</pre>");
                out.println("<br><a href='?'>← Back to Menu</a>");
                return;
            }
            
            out.println("📋 Current Payment Status:");
            out.println("- TransactionStatus: " + payment.getTransactionStatus());
            out.println("- Status: " + payment.getStatus());
            out.println();
            
            // Force update to success
            out.println("🔧 Force updating to Success status...");
            
            boolean updateSuccess = paymentDAO.updatePaymentStatus(
                orderCode,
                "Success",
                "FORCE_UPDATE_" + System.currentTimeMillis(),
                "00"
            );
            
            if (updateSuccess) {
                out.println("✅ SUCCESS: Payment status force updated!");
                out.println();
                
                // Verify the update
                Payment updatedPayment = paymentDAO.getPaymentByOrderCode(orderCode);
                if (updatedPayment != null) {
                    out.println("✅ Verification - Updated Status:");
                    out.println("- TransactionStatus: " + updatedPayment.getTransactionStatus());
                    out.println("- Status: " + updatedPayment.getStatus());
                    out.println("- TransactionNo: " + updatedPayment.getTransactionNo());
                    out.println("- PaymentDate: " + updatedPayment.getPaymentDate());
                    out.println();
                    out.println("💡 Payment đã được cập nhật thành công!");
                    out.println("   User có thể cần đăng nhập lại để thấy premium status.");
                }
            } else {
                out.println("❌ FAILED: Could not update payment status");
            }
            
        } catch (NumberFormatException e) {
            out.println("❌ ERROR: Invalid OrderCode format: " + orderCodeParam);
        } catch (SQLException e) {
            out.println("❌ SQL ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } catch (Exception e) {
            out.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?'>← Back to Menu</a>");
    }
    
    private void listPendingPayments(PrintWriter out) {
        out.println("<h2>All Pending Payments</h2>");
        
        try {
            // This would require a new method in PaymentDAO to get pending payments
            // For now, we'll show a message
            out.println("<div style='background-color: #fff3cd; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
            out.println("<h4>⚠️ Feature Incomplete</h4>");
            out.println("<p>Để implement feature này, cần thêm method <code>getPendingPayments()</code> vào PaymentDAO.</p>");
            out.println("<p>Hiện tại bạn có thể:</p>");
            out.println("<ol>");
            out.println("<li>Check từng payment bằng OrderCode</li>");
            out.println("<li>Chạy query SQL trực tiếp: <code>SELECT * FROM Payments WHERE TransactionStatus = 'PENDING'</code></li>");
            out.println("</ol>");
            out.println("</div>");
            
        } catch (Exception e) {
            out.println("<div style='background-color: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px;'>");
            out.println("<h4>❌ Error</h4>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
        }
        
        out.println("<br><a href='?'>← Back to Menu</a>");
    }
} 