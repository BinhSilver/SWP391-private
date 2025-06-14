package controller.admin;

import Dao.PremiumPlanDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.PremiumPlan;
import model.User;
import java.util.List;

@WebServlet(name = "PremiumManagementServlet", urlPatterns = {"/premiumManagement"})
public class PremiumManagementServlet extends HttpServlet {
    private PremiumPlanDAO premiumPlanDAO = new PremiumPlanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra quyền admin
        User authUser = (User) request.getSession().getAttribute("authUser");
        if (authUser == null || !authUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Lấy danh sách các gói premium
        List<PremiumPlan> premiumPlans = premiumPlanDAO.getAllPremiumPlans();
        request.setAttribute("premiumPlans", premiumPlans);
        
        request.getRequestDispatcher("/admin/premiumManagement.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra quyền admin
        User authUser = (User) request.getSession().getAttribute("authUser");
        if (authUser == null || !authUser.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String action = request.getParameter("action");
        
        try {
            if (action == null) {
                // Thêm mới gói premium
                String planName = request.getParameter("planName");
                double price = Double.parseDouble(request.getParameter("price"));
                int duration = Integer.parseInt(request.getParameter("duration"));
                String description = request.getParameter("description");

                boolean success = premiumPlanDAO.addPremiumPlan(planName, price, duration, description);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/premiumManagement");
                } else {
                    request.setAttribute("error", "Không thể thêm gói premium");
                    doGet(request, response);
                }
            } else if (action.equals("update")) {
                // Cập nhật gói premium
                int planId = Integer.parseInt(request.getParameter("planId"));
                String planName = request.getParameter("planName");
                double price = Double.parseDouble(request.getParameter("price"));
                int duration = Integer.parseInt(request.getParameter("duration"));
                String description = request.getParameter("description");

                boolean success = premiumPlanDAO.updatePremiumPlan(planId, planName, price, duration, description);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/premiumManagement");
                } else {
                    request.setAttribute("error", "Không thể cập nhật gói premium");
                    doGet(request, response);
                }
            } else if (action.equals("delete")) {
                // Xóa gói premium
                int planId = Integer.parseInt(request.getParameter("planId"));
                boolean success = premiumPlanDAO.deletePremiumPlan(planId);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/premiumManagement");
                } else {
                    request.setAttribute("error", "Không thể xóa gói premium");
                    doGet(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            doGet(request, response);
        }
    }
} 