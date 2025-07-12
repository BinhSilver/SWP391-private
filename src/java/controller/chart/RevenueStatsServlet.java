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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@WebServlet("/api/revenuestat")
public class RevenueStatsServlet extends HttpServlet {
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== RevenueStatsServlet: Processing request ===");
        
        // Debug current date/time
        LocalDate currentDate = LocalDate.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        Date javaDate = new Date();
        ZoneId zoneId = ZoneId.systemDefault();
        
        System.out.println("Server date debugging:");
        System.out.println("- LocalDate.now(): " + currentDate);
        System.out.println("- LocalDateTime.now(): " + currentDateTime);
        System.out.println("- new Date(): " + javaDate);
        System.out.println("- System timezone: " + zoneId);
        System.out.println("- System.currentTimeMillis(): " + System.currentTimeMillis());
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Enable CORS if needed
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PaymentDAO paymentDAO = new PaymentDAO();
        PrintWriter out = response.getWriter();
        
        try {
            String period = request.getParameter("period");
            if (period == null) period = "month";
            
            System.out.println("RevenueStatsServlet: Requested period = " + period);
            
            JsonObject revenueStats = paymentDAO.getRevenueStatsByPeriod(period);
            System.out.println("RevenueStatsServlet: Data from PaymentDAO = " + revenueStats);
            
            String json = new Gson().toJson(revenueStats);
            System.out.println("RevenueStatsServlet: JSON response = " + json);
            
            out.print(json);
            out.flush();
            
            System.out.println("RevenueStatsServlet: Response sent successfully");
            
        } catch (SQLException e) {
            System.out.println("RevenueStatsServlet: SQL Error - " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "Unable to fetch revenue data: " + e.getMessage());
            errorObj.addProperty("plans", "[]");
            errorObj.addProperty("totalRevenue", 0);
            errorObj.addProperty("purchaserCount", 0);
            
            out.print(new Gson().toJson(errorObj));
            out.flush();
        } catch (Exception e) {
            System.out.println("RevenueStatsServlet: Unexpected Error - " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "Server error: " + e.getMessage());
            errorObj.addProperty("plans", "[]");
            errorObj.addProperty("totalRevenue", 0);
            errorObj.addProperty("purchaserCount", 0);
            
            out.print(new Gson().toJson(errorObj));
            out.flush();
        } finally {
            System.out.println("=== RevenueStatsServlet: Request completed ===");
        }
    }
}