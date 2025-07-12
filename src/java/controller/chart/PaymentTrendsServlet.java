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

@WebServlet("/api/paymenttrends")
public class PaymentTrendsServlet extends HttpServlet {
    
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
        
        System.out.println("=== PaymentTrendsServlet: Processing request ===");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Enable CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        PaymentDAO paymentDAO = new PaymentDAO();
        PrintWriter out = response.getWriter();
        
        try {
            System.out.println("PaymentTrendsServlet: Fetching trends data");
            
            JsonObject trendsData = paymentDAO.getPaymentTrendsByMonth();
            System.out.println("PaymentTrendsServlet: Data from PaymentDAO = " + trendsData);
            
            String json = new Gson().toJson(trendsData);
            System.out.println("PaymentTrendsServlet: JSON response = " + json);
            
            out.print(json);
            out.flush();
            
            System.out.println("PaymentTrendsServlet: Response sent successfully");
            
        } catch (SQLException e) {
            System.out.println("PaymentTrendsServlet: SQL Error - " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "Unable to fetch trends data: " + e.getMessage());
            errorObj.addProperty("months", "[]");
            errorObj.addProperty("revenue", "[]");
            errorObj.addProperty("transactions", "[]");
            errorObj.addProperty("successTransactions", "[]");
            
            out.print(new Gson().toJson(errorObj));
            out.flush();
        } catch (Exception e) {
            System.out.println("PaymentTrendsServlet: Unexpected Error - " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("error", "Server error: " + e.getMessage());
            errorObj.addProperty("months", "[]");
            errorObj.addProperty("revenue", "[]");
            errorObj.addProperty("transactions", "[]");
            errorObj.addProperty("successTransactions", "[]");
            
            out.print(new Gson().toJson(errorObj));
            out.flush();
        } finally {
            System.out.println("=== PaymentTrendsServlet: Request completed ===");
        }
    }
} 