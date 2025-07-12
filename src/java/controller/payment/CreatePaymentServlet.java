package controller.payment;

import Dao.PremiumPlanDAO;
import Dao.PaymentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Payment;
import model.PremiumPlan;
import model.User;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/CreatePayment")
public class CreatePaymentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("=== CreatePaymentServlet: Starting payment process ===");
        
        try {
            // Get user from session
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("authUser");
            
            if (user == null) {
                System.out.println("ERROR: User not logged in");
                request.setAttribute("errorMessage", "Vui lòng đăng nhập để tiếp tục");
                request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
                return;
            }
            
            System.out.println("User authenticated: " + user.getEmail() + " (ID: " + user.getUserID() + ")");
            
            // Lấy planId từ request
            String planIdParam = request.getParameter("planId");
            System.out.println("Received planId parameter: " + planIdParam);
            
            if (planIdParam == null || planIdParam.trim().isEmpty()) {
                System.out.println("ERROR: planId is null or empty");
                request.setAttribute("errorMessage", "Không có thông tin gói premium");
                request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
                return;
            }
            
            int planId = Integer.parseInt(planIdParam);
            System.out.println("Parsed planId: " + planId);
            
            // Lưu planId vào session
            session.setAttribute("selectedPlanId", planId);
            System.out.println("Saved planId to session");
            
            // Lấy thông tin plan
            PremiumPlanDAO planDAO = new PremiumPlanDAO();
            PremiumPlan plan = planDAO.getPlanById(planId);
            System.out.println("Retrieved plan from database: " + (plan != null ? plan.getPlanName() : "null"));
            
            if (plan != null) {
                // Tạo URL cho return và cancel
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                String returnUrl = baseUrl + "/ReturnFromPayOS";
                String cancelUrl = baseUrl + "/CancelPayment";
                
                System.out.println("Base URL: " + baseUrl);
                System.out.println("Return URL: " + returnUrl);
                System.out.println("Cancel URL: " + cancelUrl);
                
                // Create order code
                long orderCode = System.currentTimeMillis();
                String orderInfo = "Thanh toán gói " + plan.getPlanName();
                
                System.out.println("Generated OrderCode: " + orderCode);
                System.out.println("Order Info: " + orderInfo);
                
                // Tạo URL thanh toán
                System.out.println("Creating payment link...");
                String paymentUrl = Config.createPaymentLink(
                    (int)plan.getPrice(),
                    orderInfo,
                    returnUrl,
                    cancelUrl,
                    orderCode  // Pass the generated orderCode
                );
                
                System.out.println("Payment URL result: " + (paymentUrl != null ? "SUCCESS" : "FAILED"));
                
                if (paymentUrl != null) {
                    // *** INSERT PAYMENT INTO DATABASE ***
                    try {
                        PaymentDAO paymentDAO = new PaymentDAO();
                        Payment payment = new Payment(
                            user.getUserID(),
                            planId,
                            plan.getPrice(),
                            orderInfo,
                            "PENDING", // Initial status
                            orderCode,
                            paymentUrl
                        );
                        
                        System.out.println("Inserting payment into database...");
                        int paymentId = paymentDAO.insertPayment(payment);
                        
                        if (paymentId > 0) {
                            System.out.println("✓ Payment record created successfully with ID: " + paymentId);
                            session.setAttribute("currentPaymentId", paymentId);
                            session.setAttribute("currentOrderCode", orderCode);
                        } else {
                            System.out.println("✗ Failed to create payment record");
                            request.setAttribute("errorMessage", "Không thể tạo bản ghi thanh toán");
                            request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
                            return;
                        }
                        
                    } catch (SQLException e) {
                        System.out.println("✗ Database error while creating payment record: " + e.getMessage());
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu khi tạo thanh toán");
                        request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
                        return;
                    }
                    
                    // Forward to payment method selection page
                    request.setAttribute("amount", (int)plan.getPrice());
                    request.setAttribute("description", orderInfo);
                    request.setAttribute("paymentUrl", paymentUrl);
                    request.setAttribute("planName", plan.getPlanName());
                    
                    System.out.println("Forwarding to PaymentQR.jsp");
                    request.getRequestDispatcher("/PaymentJSP/PaymentQR.jsp").forward(request, response);
                } else {
                    System.out.println("ERROR: Payment URL is null, forwarding to cancel page");
                    request.setAttribute("errorMessage", "Không thể tạo link thanh toán");
                    request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
                }
            } else {
                System.out.println("ERROR: Plan not found in database");
                request.setAttribute("errorMessage", "Không tìm thấy gói premium");
                request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid planId format - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Thông tin gói premium không hợp lệ");
            request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
        } catch (SQLException e) {
            System.out.println("ERROR: Database error - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("ERROR: Unexpected error - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
        }
        
        System.out.println("=== CreatePaymentServlet: Process completed ===");
    }
}
