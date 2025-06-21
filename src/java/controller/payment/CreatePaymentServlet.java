package controller.payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.payos.type.PaymentData;
import vn.payos.type.CheckoutResponseData;

/**
 * Servlet xử lý khi người dùng gửi yêu cầu tạo thanh toán
 */
@WebServlet(name = "CreatePaymentServlet", urlPatterns = {"/CreatePayment"})
public class CreatePaymentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String plan = request.getParameter("plan");
            int amount;
            String description;
            
            if ("month".equals(plan)) {
                amount = 25000;
                description = "Gói Premium Hàng Tháng";
            } else {
                amount = 250000;
                description = "Gói Premium Hàng Năm";
            }

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
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/PaymentJSP/PaymentCancel.jsp");
        }
    }
}
