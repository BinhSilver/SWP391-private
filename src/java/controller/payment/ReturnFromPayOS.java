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

            // Cập nhật roleID cho user thành premium (2)
            model.User user = (model.User) request.getSession().getAttribute("authUser");
            if (user != null && user.getRoleID() != 2) {
                user.setRoleID(2);
                new service.UserService().updateUser(user);
                request.getSession().setAttribute("authUser", user); // cập nhật lại session
            }
        } else {
            // Payment failed or cancelled
            request.getSession().setAttribute("paymentSuccess", false);
            request.getSession().setAttribute("paymentMessage", "Thanh toán không thành công hoặc đã bị hủy!");
        }
        
        // Redirect to index page
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
} 