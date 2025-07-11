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

@WebServlet("/api/paymentstatus")
public class PaymentStatusServlet extends HttpServlet {
    
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
        
        System.out.println("=== PaymentStatusServlet: Processing request ===");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PaymentDAO paymentDAO = new PaymentDAO();
        PrintWriter out = response.getWriter();
        
        try {
            String period = request.getParameter("period");
            if (period == null) period = "month";
            
            System.out.println("PaymentStatusServlet: Requested period = " + period);
            
            JsonObject statusStats = paymentDAO.getPaymentStatsByStatus(period);
            System.out.println("PaymentStatusServlet: Data from PaymentDAO = " + statusStats);
            
            String json = new Gson().toJson(statusStats);
            System.out.println("PaymentStatusServlet: JSON response = " + json);
            
            out.print(json);
            out.flush();
            
            System.out.println("PaymentStatusServlet: Response sent successfully");
            
        } catch (SQLException e) {
            System.out.println("PaymentStatusServlet: SQL Error - " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "Unable to fetch payment status data: " + e.getMessage());
            errorObj.addProperty("statusStats", "[]");
            errorObj.addProperty("totalTransactions", 0);
            errorObj.addProperty("totalTransactionCount", 0);
            errorObj.addProperty("uniqueUserCount", 0);
            
            out.print(new Gson().toJson(errorObj));
            out.flush();
        } catch (Exception e) {
            System.out.println("PaymentStatusServlet: Unexpected Error - " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "Server error: " + e.getMessage());
            errorObj.addProperty("statusStats", "[]");
            errorObj.addProperty("totalTransactions", 0);
            errorObj.addProperty("totalTransactionCount", 0);
            errorObj.addProperty("uniqueUserCount", 0);
            
            out.print(new Gson().toJson(errorObj));
            out.flush();
        } finally {
            System.out.println("=== PaymentStatusServlet: Request completed ===");
        }
    }
} 