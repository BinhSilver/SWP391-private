package controller.payment;

import Dao.PremiumPlanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.PremiumPlan;

@WebServlet("/CreatePayment")
public class CreatePaymentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy planId từ request
            int planId = Integer.parseInt(request.getParameter("planId"));
            
            // Lưu planId vào session
            HttpSession session = request.getSession();
            session.setAttribute("selectedPlanId", planId);
            
            // Lấy thông tin plan
            PremiumPlanDAO planDAO = new PremiumPlanDAO();
            PremiumPlan plan = planDAO.getPlanById(planId);
            
            if (plan != null) {
                // Tạo URL cho return và cancel
                String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                String returnUrl = baseUrl + "/ReturnFromPayOS";
                String cancelUrl = baseUrl + "/CancelPayment";
                
                // Tạo URL thanh toán
                String paymentUrl = Config.createPaymentLink(
                    (int)plan.getPrice(),
                    "Thanh toán gói " + plan.getPlanName(),
                    returnUrl,
                    cancelUrl
                );
                
                if (paymentUrl != null) {
                    response.sendRedirect(paymentUrl);
                } else {
                    request.setAttribute("errorMessage", "Không thể tạo link thanh toán");
                    request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Không tìm thấy gói premium");
                request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/PaymentJSP/PaymentCancel.jsp").forward(request, response);
        }
    }
}
