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
        boolean success = false;

        if (action == null) {
            // Thêm mới gói premium
            String planName = request.getParameter("planName");
            double price = Double.parseDouble(request.getParameter("price"));
            int duration = Integer.parseInt(request.getParameter("duration"));
            String description = request.getParameter("description");

            success = premiumPlanDAO.addPremiumPlan(planName, price, duration, description);
        } else if (action.equals("edit")) {
            // Cập nhật gói premium
            int planID = Integer.parseInt(request.getParameter("planID"));
            String planName = request.getParameter("planName");
            double price = Double.parseDouble(request.getParameter("price"));
            int duration = Integer.parseInt(request.getParameter("duration"));
            String description = request.getParameter("description");

            success = premiumPlanDAO.updatePremiumPlan(planID, planName, price, duration, description);
        } else if (action.equals("delete")) {
            // Xóa gói premium
            int planID = Integer.parseInt(request.getParameter("planID"));
            success = premiumPlanDAO.deletePremiumPlan(planID);
        }

        if (success) {
            response.sendRedirect(request.getContextPath() + "/premiumManagement");
        } else {
            request.setAttribute("error", "Không thể thực hiện thao tác");
            doGet(request, response);
        }
    }
} 