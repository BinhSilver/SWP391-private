package controller.admin;

import Dao.PremiumPlanDAO;
import model.PremiumPlan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

@WebServlet(name = "PremiumPlanManagementServlet", urlPatterns = {"/admin/premium-plans"})
public class PremiumPlanManagementServlet extends HttpServlet {

    private final PremiumPlanDAO premiumPlanDAO = new PremiumPlanDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            default:
                listPlans(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action) {
            case "add":
                addPlan(request, response);
                break;
            case "update":
                updatePlan(request, response);
                break;
            case "delete":
                deletePlan(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/premium-plans");
                break;
        }
    }

    private void listPlans(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<PremiumPlan> premiumPlans = premiumPlanDAO.getAllPremiumPlans();
            request.setAttribute("premiumPlans", premiumPlans);
        } catch (SQLException e) {
            request.setAttribute("premiumPlans", new ArrayList<>());
            request.setAttribute("error", "Không thể tải danh sách gói Premium");
        }
        request.getRequestDispatcher("/admin/premium-plans.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/admin/add-premium-plan.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int planId = Integer.parseInt(request.getParameter("planId"));
            PremiumPlan plan = premiumPlanDAO.getPremiumPlanByID(planId);
            request.setAttribute("plan", plan);
            request.getRequestDispatcher("/admin/edit-premium-plan.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/admin/premium-plans");
        }
    }

    private void addPlan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String planName = request.getParameter("planName");
            double price = Double.parseDouble(request.getParameter("price"));
            int duration = Integer.parseInt(request.getParameter("duration"));
            String description = request.getParameter("description");
            premiumPlanDAO.addPremiumPlan(planName, price, duration, description);
        } catch (Exception e) {
            // Log error or set an error message
        }
        response.sendRedirect(request.getContextPath() + "/admin/premium-plans");
    }

    private void updatePlan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int planId = Integer.parseInt(request.getParameter("planId"));
            String planName = request.getParameter("planName");
            double price = Double.parseDouble(request.getParameter("price"));
            int duration = Integer.parseInt(request.getParameter("duration"));
            String description = request.getParameter("description");
            premiumPlanDAO.updatePremiumPlan(planId, planName, price, duration, description);
        } catch (Exception e) {
            // Log error or set an error message
        }
        response.sendRedirect(request.getContextPath() + "/admin/premium-plans");
    }

    private void deletePlan(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int planId = Integer.parseInt(request.getParameter("planId"));
            premiumPlanDAO.deletePremiumPlan(planId);
        } catch (NumberFormatException | SQLException e) {
            // Log error or set an error message
        }
        response.sendRedirect(request.getContextPath() + "/admin/premium-plans");
    }
} 