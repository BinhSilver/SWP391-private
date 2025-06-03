package controller.payment;

import Dao.PremiumPlanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.PremiumPlan;

@WebServlet(name = "PaymentPageServlet", urlPatterns = {"/payment"})
public class PaymentPageServlet extends HttpServlet {
    private final PremiumPlanDAO premiumPlanDAO = new PremiumPlanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy danh sách các gói premium
            List<PremiumPlan> premiumPlans = premiumPlanDAO.getAllPremiumPlans();
            
            if (premiumPlans.isEmpty()) {
                request.setAttribute("errorMessage", "Không tìm thấy gói Premium nào. Vui lòng liên hệ admin.");
            }
            
            request.setAttribute("premiumPlans", premiumPlans);
            
            // Chuyển đến trang payment
            request.getRequestDispatcher("PaymentJSP/Payment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi tải thông tin gói Premium. Vui lòng thử lại sau.");
            request.getRequestDispatcher("PaymentJSP/Payment.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
} 