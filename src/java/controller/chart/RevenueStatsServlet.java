package controller.chart;

import Dao.PaymentDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/api/revenuestat")
public class RevenueStatsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PaymentDAO paymentDAO = new PaymentDAO();
        try {
            String period = request.getParameter("period");
            if (period == null) period = "month";
            JsonObject revenueStats = paymentDAO.getRevenueStatsByPeriod(period);
            System.out.println("Dữ liệu từ PaymentDAO.getRevenueStatsByPeriod: " + revenueStats);
            String json = new Gson().toJson(revenueStats);
            System.out.println("JSON phản hồi: " + json);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Unable to fetch revenue data: " + e.getMessage() + "\"}");
            out.flush();
        }
    }
}