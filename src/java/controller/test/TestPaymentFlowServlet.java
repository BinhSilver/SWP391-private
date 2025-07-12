package controller.test;

import Dao.PaymentDAO;
import Dao.PremiumPlanDAO;
import model.Payment;
import model.PremiumPlan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/test/payment-flow")
public class TestPaymentFlowServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Test Payment Flow</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 20px; }");
        out.println("pre { background: #f5f5f5; padding: 10px; border-left: 3px solid #007bff; }");
        out.println(".success { color: green; } .error { color: red; }");
        out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        out.println("th { background-color: #f2f2f2; }");
        out.println("</style></head><body>");
        out.println("<h1>Test Payment Flow & Database Operations</h1>");
        
        String action = request.getParameter("action");
        
        if ("test-insert".equals(action)) {
            testPaymentInsertion(out);
        } else if ("test-update".equals(action)) {
            testPaymentUpdate(out);
        } else if ("test-query".equals(action)) {
            testPaymentQuery(out);
        } else {
            showTestMenu(out);
        }
        
        out.println("</body></html>");
    }
    
    private void showTestMenu(PrintWriter out) {
        out.println("<h2>Payment Flow Test Menu</h2>");
        out.println("<p>Choose a test to run:</p>");
        out.println("<ul>");
        out.println("<li><a href='?action=test-insert'>Test Payment Insertion</a> - Test inserting payment into database</li>");
        out.println("<li><a href='?action=test-update'>Test Payment Update</a> - Test updating payment status</li>");
        out.println("<li><a href='?action=test-query'>Test Payment Query</a> - Test querying payment by orderCode</li>");
        out.println("</ul>");
        
        out.println("<h3>Available Premium Plans:</h3>");
        try {
            PremiumPlanDAO planDAO = new PremiumPlanDAO();
            List<PremiumPlan> plans = planDAO.getAllPremiumPlans();
            
            if (!plans.isEmpty()) {
                out.println("<table>");
                out.println("<tr><th>Plan ID</th><th>Plan Name</th><th>Price</th><th>Duration (Months)</th><th>Description</th></tr>");
                for (PremiumPlan plan : plans) {
                    out.println("<tr>");
                    out.println("<td>" + plan.getPlanID() + "</td>");
                    out.println("<td>" + plan.getPlanName() + "</td>");
                    out.println("<td>" + String.format("%,.0f", plan.getPrice()) + " VND</td>");
                    out.println("<td>" + plan.getDurationInMonths() + "</td>");
                    out.println("<td>" + plan.getDescription() + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            } else {
                out.println("<p class='error'>No premium plans found in database!</p>");
            }
        } catch (SQLException e) {
            out.println("<p class='error'>Error loading premium plans: " + e.getMessage() + "</p>");
        }
    }
    
    private void testPaymentInsertion(PrintWriter out) {
        out.println("<h2>Test Payment Insertion</h2>");
        out.println("<pre>");
        
        try {
            System.out.println("=== TEST PAYMENT INSERTION ===");
            
            PaymentDAO paymentDAO = new PaymentDAO();
            long testOrderCode = System.currentTimeMillis();
            
            // Create test payment
            Payment payment = new Payment(
                1, // Test UserID
                1, // Test PlanID  
                25000.0, // Test Amount
                "Test payment for Gói Tháng",
                "PENDING",
                testOrderCode,
                "https://test-checkout-url.com"
            );
            
            out.println("Creating test payment:");
            out.println("- UserID: " + payment.getUserID());
            out.println("- PlanID: " + payment.getPlanID());
            out.println("- Amount: " + payment.getAmount());
            out.println("- OrderCode: " + payment.getOrderCode());
            out.println("- Status: " + payment.getTransactionStatus());
            out.println();
            
            int paymentId = paymentDAO.insertPayment(payment);
            
            if (paymentId > 0) {
                out.println("✓ SUCCESS: Payment inserted with ID: " + paymentId);
                out.println();
                
                // Test querying the inserted payment
                out.println("Verifying insertion by querying back:");
                Payment queriedPayment = paymentDAO.getPaymentByOrderCode(testOrderCode);
                
                if (queriedPayment != null) {
                    out.println("✓ Payment found in database:");
                    out.println("  PaymentID: " + queriedPayment.getPaymentID());
                    out.println("  UserID: " + queriedPayment.getUserID());
                    out.println("  PlanID: " + queriedPayment.getPlanID());
                    out.println("  Amount: " + queriedPayment.getAmount());
                    out.println("  OrderCode: " + queriedPayment.getOrderCode());
                    out.println("  Status: " + queriedPayment.getTransactionStatus());
                    out.println("  CreatedAt: " + queriedPayment.getCreatedAt());
                } else {
                    out.println("✗ ERROR: Could not find inserted payment!");
                }
            } else {
                out.println("✗ ERROR: Payment insertion failed!");
            }
            
        } catch (SQLException e) {
            out.println("✗ SQL ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } catch (Exception e) {
            out.println("✗ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?'>← Back to Menu</a>");
    }
    
    private void testPaymentUpdate(PrintWriter out) {
        out.println("<h2>Test Payment Update</h2>");
        out.println("<pre>");
        
        try {
            System.out.println("=== TEST PAYMENT UPDATE ===");
            
            PaymentDAO paymentDAO = new PaymentDAO();
            long testOrderCode = System.currentTimeMillis() - 1000; // Use recent orderCode
            
            out.println("Testing payment status update:");
            out.println("- OrderCode: " + testOrderCode);
            out.println("- New Status: Success");
            out.println("- Transaction ID: TEST_TXN_" + testOrderCode);
            out.println();
            
            boolean updateSuccess = paymentDAO.updatePaymentStatus(
                testOrderCode,
                "Success",
                "TEST_TXN_" + testOrderCode,
                "VCB", // Test bank code
                "00"   // Success response code
            );
            
            if (updateSuccess) {
                out.println("✓ SUCCESS: Payment status updated");
                
                // Verify the update
                Payment updatedPayment = paymentDAO.getPaymentByOrderCode(testOrderCode);
                if (updatedPayment != null) {
                    out.println("✓ Update verified:");
                    out.println("  Status: " + updatedPayment.getTransactionStatus());
                    out.println("  TransactionNo: " + updatedPayment.getTransactionNo());
                    out.println("  BankCode: " + updatedPayment.getBankCode());
                    out.println("  ResponseCode: " + updatedPayment.getResponseCode());
                    out.println("  PaymentDate: " + updatedPayment.getPaymentDate());
                } else {
                    out.println("⚠ WARNING: Payment not found for verification (OrderCode may not exist)");
                }
            } else {
                out.println("✗ ERROR: Payment status update failed");
                out.println("  (This is expected if the OrderCode doesn't exist in database)");
            }
            
        } catch (SQLException e) {
            out.println("✗ SQL ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } catch (Exception e) {
            out.println("✗ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?'>← Back to Menu</a>");
    }
    
    private void testPaymentQuery(PrintWriter out) {
        out.println("<h2>Test Payment Query</h2>");
        out.println("<pre>");
        
        try {
            System.out.println("=== TEST PAYMENT QUERY ===");
            
            PaymentDAO paymentDAO = new PaymentDAO();
            long testOrderCode = 1752242421036L; // Use a known OrderCode from your database
            
            out.println("Querying payment with OrderCode: " + testOrderCode);
            out.println();
            
            Payment payment = paymentDAO.getPaymentByOrderCode(testOrderCode);
            
            if (payment != null) {
                out.println("✓ SUCCESS: Payment found!");
                out.println("Payment Details:");
                out.println("  PaymentID: " + payment.getPaymentID());
                out.println("  UserID: " + payment.getUserID());
                out.println("  PlanID: " + payment.getPlanID());
                out.println("  Amount: " + String.format("%,.0f", payment.getAmount()) + " VND");
                out.println("  OrderInfo: " + payment.getOrderInfo());
                out.println("  TransactionStatus: " + payment.getTransactionStatus());
                out.println("  TransactionNo: " + payment.getTransactionNo());
                out.println("  BankCode: " + payment.getBankCode());
                out.println("  ResponseCode: " + payment.getResponseCode());
                out.println("  OrderCode: " + payment.getOrderCode());
                out.println("  CheckoutUrl: " + payment.getCheckoutUrl());
                out.println("  Status: " + payment.getStatus());
                out.println("  PaymentDate: " + payment.getPaymentDate());
                out.println("  CreatedAt: " + payment.getCreatedAt());
            } else {
                out.println("✗ No payment found with OrderCode: " + testOrderCode);
                out.println("Try running the insertion test first to create test data.");
            }
            
        } catch (SQLException e) {
            out.println("✗ SQL ERROR: " + e.getMessage());
            e.printStackTrace(out);
        } catch (Exception e) {
            out.println("✗ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace(out);
        }
        
        out.println("</pre>");
        out.println("<br><a href='?'>← Back to Menu</a>");
    }
} 