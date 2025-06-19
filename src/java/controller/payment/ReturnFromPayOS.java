package controller.payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ReturnFromPayOS")
public class ReturnFromPayOS extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get payment status from PayOS callback
        String status = request.getParameter("status");
        
        if ("PAID".equals(status)) {
            // Payment successful
            request.getSession().setAttribute("paymentSuccess", true);
            request.getSession().setAttribute("paymentMessage", "Thanh toán thành công!");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            // Payment failed or cancelled
            request.getSession().setAttribute("paymentSuccess", false);
            request.getSession().setAttribute("paymentMessage", "Thanh toán không thành công hoặc đã bị hủy!");
            response.sendRedirect(request.getContextPath() + "/PaymentJSP/Payment.jsp");
        }
    }
}