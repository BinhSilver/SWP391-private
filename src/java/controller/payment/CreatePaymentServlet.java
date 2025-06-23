package controller.payment;

import Dao.PremiumPlanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PremiumPlan;
import vn.payos.type.PaymentData;
import vn.payos.type.CheckoutResponseData;

import java.io.IOException;

/**
 * Servlet xử lý khi người dùng gửi yêu cầu tạo thanh toán
 */
@WebServlet(name = "CreatePaymentServlet", urlPatterns = {"/CreatePayment"})
public class CreatePaymentServlet extends HttpServlet {

    private final PremiumPlanDAO premiumPlanDAO = new PremiumPlanDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int planId = Integer.parseInt(request.getParameter("planId"));
            PremiumPlan plan = premiumPlanDAO.getPremiumPlanByID(planId);

            if (plan == null) {
                request.setAttribute("errorMessage", "Gói thanh toán không hợp lệ.");
                request.getRequestDispatcher("/PaymentJSP/Payment.jsp").forward(request, response);
                return;
            }

            int amount = (int) plan.getPrice();
            String description = plan.getPlanName();

            // Create unique order code
            long orderCode = System.currentTimeMillis();

            // Create payment data with all required fields
            String contextPath = request.getContextPath();
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + contextPath;
            PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .returnUrl(baseUrl + "/ReturnFromPayOS")
                .cancelUrl(baseUrl + "/CancelPayment")
                .signature(Config.checksumKey)
                .items(null)
                .build();

            // Create payment with PayOS
            CheckoutResponseData result = Config.payOS().createPaymentLink(paymentData);
            
            // Get the QR code URL and payment URL
            String qrCode = result.getQrCode();
            String paymentUrl = result.getCheckoutUrl();
            
            // Store in request attributes
            request.setAttribute("qrCode", qrCode);
            request.setAttribute("paymentUrl", paymentUrl);
            request.setAttribute("amount", amount);
            request.setAttribute("description", description);
            request.setAttribute("orderCode", String.valueOf(orderCode));
            
            // Forward to QR display page
            request.getRequestDispatcher("/PaymentJSP/PaymentQR.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Gói thanh toán không hợp lệ.");
            request.getRequestDispatcher("/PaymentJSP/Payment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/PaymentJSP/PaymentCancel.jsp");
        }
    }
}
